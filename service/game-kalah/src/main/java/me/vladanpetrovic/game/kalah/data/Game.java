package me.vladanpetrovic.game.kalah.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import me.vladanpetrovic.game.kalah.config.GameConfig;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Document
public class Game {
    @Id
    private String id;

    private GameConfig.Settings settings;
    private Status status;
    private Player player1;
    private Player player2;

    public Game() {
        this.status = Status.initial();
        this.player1 = new Player();
        this.player2 = new Player();
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Status {
        private States state;
        @JsonProperty("player_turn")
        private Players playerTurn;
        private String winner;

        public static Status initial() {
            Status status = new Status();
            status.state = States.STARTED;
            status.playerTurn = Players.PLAYER1;
            return status;
        }
    }

    public enum States {
        STARTED, NEXT_PLAYER_TURN, ANOTHER_TURN, FINISHED
    }

    public enum Players {
        PLAYER1, PLAYER2
    }

    @Data
    public static class Player {
        @JsonProperty("user_id")
        private String userId;
        private int kalah;
        private Map<Integer, Integer> pits;

        public Player() {
            this.kalah = 0;
            this.pits = Collections.emptyMap();
        }

        public void initPits(GameConfig.Settings settings) {
            this.pits = new ConcurrentHashMap<>() {
                {
                    for (int pit = 1; pit <= settings.getNumOfPits(); pit++) {
                        put(pit, settings.getNumOfStones());
                    }
                }
            };
        }
    }

    public void setup(GameConfig.Settings settings) {
        this.settings = settings;
        this.player1.initPits(settings);
        this.player2.initPits(settings);
    }

    public GameEvent movePitStones(Integer pitId) {
        var player = status.getPlayerTurn() == Players.PLAYER1 ? player1 : player2;
        var nextPlayer = status.getPlayerTurn() == Players.PLAYER1 ? player2 : player1;

        var stones = player.pits.get(pitId);
        player.pits.put(pitId, 0);
        var nextPitId = pitId + 1;
        while (stones > 0) {
            while (stones > 0 && nextPitId <= settings.getNumOfPits()) {
                var numOfStonesInPit = player.pits.get(nextPitId);
                numOfStonesInPit++;
                player.pits.put(nextPitId, numOfStonesInPit);
                stones--;
                if (stones == 0 && numOfStonesInPit == 1) {
                    player.pits.put(nextPitId, 0);
                    player.kalah++;
                    var nextPlayerOppositePit = settings.getNumOfPits() - nextPitId + 1;
                    player.kalah += nextPlayer.pits.get(nextPlayerOppositePit);
                    nextPlayer.pits.put(nextPlayerOppositePit, 0);
                    return GameEvent.builder()
                            .gameId(id)
                            .selectedPit(pitId)
                            .event(GameEvent.Events.LAST_STONE_EMPTY_PIT)
                            .player(status.getPlayerTurn())
                            .build();
                }
                nextPitId++;
            }
            nextPitId = 1;
            if (stones > 0) {
                player.kalah++;
                stones--;
                if (stones == 0) {
                    return GameEvent.builder()
                            .gameId(id)
                            .selectedPit(pitId)
                            .event(GameEvent.Events.LAST_STONE_KALAH)
                            .player(status.getPlayerTurn())
                            .build();
                }
            }
            var nextPlayerPitId = 1;
            while (stones > 0 && nextPlayerPitId <= settings.getNumOfPits()) {
                nextPlayer.pits.put(nextPlayerPitId, nextPlayer.pits.get(nextPlayerPitId) + 1);
                stones--;
                nextPlayerPitId++;
            }
        }
        return GameEvent.builder()
                .gameId(id)
                .selectedPit(pitId)
                .event(GameEvent.Events.MOVE_PIT_STONES)
                .player(status.getPlayerTurn())
                .build();
    }

    public boolean handleOutOfStonesSituation() {
        var player = status.getPlayerTurn() == Players.PLAYER1 ? player1 : player2;
        var otherPlayer = status.getPlayerTurn() == Players.PLAYER1 ? player2 : player1;
        
        if (player.pits.values().stream().allMatch(i -> i == 0)) {
            movePlayerStonesToOtherPlayer(otherPlayer, player);
            return true;
        }
        if (otherPlayer.pits.values().stream().allMatch(i -> i == 0)) {
            movePlayerStonesToOtherPlayer(player, otherPlayer);
            return true;
        }
        return false;
    }

    private void movePlayerStonesToOtherPlayer(Player player, Player otherPlayer) {
        player.pits.forEach((pitId, stones) -> {
            if (stones > 0) {
                otherPlayer.kalah += stones;
                player.pits.put(pitId, 0);
            }
        });
    }

    public void determineWinner() {
        if (player1.kalah > player2.kalah) {
            status.setWinner(Players.PLAYER1.toString());
        } else if (player2.kalah > player1.kalah) {
            status.setWinner(Players.PLAYER2.toString());
        } else {
            status.setWinner("DRAW");
        }
    }
}
