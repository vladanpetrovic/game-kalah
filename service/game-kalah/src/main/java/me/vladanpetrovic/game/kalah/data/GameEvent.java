package me.vladanpetrovic.game.kalah.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("game_event")
public class GameEvent {

    @Id
    private String id;
    @JsonProperty("game_id")
    private String gameId;
    private Game.Players player;
    @JsonProperty("selected_pit")
    private Integer selectedPit;
    private Events event;
    private Game.States state;

    public enum Events {
        MOVE_PIT_STONES,
        LAST_STONE_KALAH,
        LAST_STONE_EMPTY_PIT,
        PLAYER_OUT_OF_STONES
    }
}
