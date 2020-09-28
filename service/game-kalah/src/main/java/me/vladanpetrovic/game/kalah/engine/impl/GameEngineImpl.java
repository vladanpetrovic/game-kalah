package me.vladanpetrovic.game.kalah.engine.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.vladanpetrovic.game.kalah.data.Game;
import me.vladanpetrovic.game.kalah.data.GameEvent;
import me.vladanpetrovic.game.kalah.data.GameEventRepository;
import me.vladanpetrovic.game.kalah.data.GameRepository;
import me.vladanpetrovic.game.kalah.data.exceptions.GameNotFoundException;
import me.vladanpetrovic.game.kalah.engine.GameEngine;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class GameEngineImpl implements GameEngine {

    private final @NonNull GameRepository gameRepository;
    private final @NonNull GameEventRepository gameEventRepository;

    private final @NonNull StateMachineService<Game.States, GameEvent.Events> stateMachineService;
    private StateMachine<Game.States, GameEvent.Events> currentStateMachine;

    @Override
    @Transactional
    public Game movePitStones(String gameId, Integer pitId) {
        var game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(String.format("Game with id %s not found", gameId)));
        var gameEvent = game.movePitStones(pitId);

        var gameStateMachine = getStateMachine(game.getId());
        gameStateMachine.sendEvent(gameEvent.getEvent());
        var currentState = gameStateMachine.getState().getId();

        gameEvent.setState(currentState);
        gameEventRepository.save(gameEvent);
        game.getStatus().setState(currentState);

        if (game.handleOutOfStonesSituation()) {
            gameStateMachine.sendEvent(GameEvent.Events.PLAYER_OUT_OF_STONES);
            game.getStatus().setState(gameStateMachine.getState().getId());
            game.getStatus().setPlayerTurn(null);
            game.determineWinner();
        } else {
            if (gameStateMachine.getState().getId() == Game.States.NEXT_PLAYER_TURN) {
                if (game.getStatus().getPlayerTurn() == Game.Players.PLAYER1) {
                    game.getStatus().setPlayerTurn(Game.Players.PLAYER2);
                } else {
                    game.getStatus().setPlayerTurn(Game.Players.PLAYER1);
                }
            }
        }
        gameRepository.save(game);

        return game;
    }

    private synchronized StateMachine<Game.States, GameEvent.Events> getStateMachine(String machineId) {
        if (currentStateMachine == null) {
            currentStateMachine = stateMachineService.acquireStateMachine(machineId);
            currentStateMachine.start();
        } else if (!ObjectUtils.nullSafeEquals(currentStateMachine.getId(), machineId)) {
            stateMachineService.releaseStateMachine(currentStateMachine.getId());
            currentStateMachine.stop();
            currentStateMachine = stateMachineService.acquireStateMachine(machineId);
            currentStateMachine.start();
        }
        return currentStateMachine;
    }
}
