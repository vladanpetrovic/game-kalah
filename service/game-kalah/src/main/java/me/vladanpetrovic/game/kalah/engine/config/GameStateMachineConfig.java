package me.vladanpetrovic.game.kalah.engine.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.vladanpetrovic.game.kalah.data.Game;
import me.vladanpetrovic.game.kalah.data.GameEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.mongodb.MongoDbPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.mongodb.MongoDbStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
public class GameStateMachineConfig {

    @Bean
    public StateMachineRuntimePersister<Game.States, GameEvent.Events, String> stateMachineRuntimePersister(
            MongoDbStateMachineRepository mongoRepository) {
        return new MongoDbPersistingStateMachineInterceptor<>(mongoRepository);
    }

    @Slf4j
    @Configuration
    @EnableStateMachineFactory
    @RequiredArgsConstructor
    public static class StateMachineConfig extends StateMachineConfigurerAdapter<Game.States, GameEvent.Events> {

        private final @NonNull StateMachineRuntimePersister<Game.States, GameEvent.Events, String> stateMachineRuntimePersister;

        @Override
        public void configure(StateMachineConfigurationConfigurer<Game.States, GameEvent.Events> config) throws Exception {
            config.withPersistence()
                    .runtimePersister(stateMachineRuntimePersister);
        }

        @Override
        public void configure(StateMachineStateConfigurer<Game.States, GameEvent.Events> states)
                throws Exception {
            states
                    .withStates()
                    .initial(Game.States.STARTED)
                    .state(Game.States.NEXT_PLAYER_TURN)
                    .state(Game.States.ANOTHER_TURN)
                    .end(Game.States.FINISHED);
        }

        @Override
        public void configure(StateMachineTransitionConfigurer<Game.States, GameEvent.Events> transitions)
                throws Exception {
            transitions
                    .withExternal()
                    .source(Game.States.STARTED)
                    .target(Game.States.NEXT_PLAYER_TURN)
                    .event(GameEvent.Events.MOVE_PIT_STONES)
                    .and()
                    .withExternal()
                    .source(Game.States.STARTED)
                    .target(Game.States.ANOTHER_TURN)
                    .event(GameEvent.Events.LAST_STONE_KALAH)
                    .and()
                    .withExternal()
                    .source(Game.States.NEXT_PLAYER_TURN)
                    .target(Game.States.NEXT_PLAYER_TURN)
                    .event(GameEvent.Events.MOVE_PIT_STONES)
                    .and()
                    .withExternal()
                    .source(Game.States.NEXT_PLAYER_TURN)
                    .target(Game.States.ANOTHER_TURN)
                    .event(GameEvent.Events.LAST_STONE_KALAH)
                    .and()
                    .withExternal()
                    .source(Game.States.NEXT_PLAYER_TURN)
                    .target(Game.States.NEXT_PLAYER_TURN)
                    .event(GameEvent.Events.LAST_STONE_EMPTY_PIT)
                    .and()
                    .withExternal()
                    .source(Game.States.ANOTHER_TURN)
                    .target(Game.States.NEXT_PLAYER_TURN)
                    .event(GameEvent.Events.MOVE_PIT_STONES)
                    .and()
                    .withExternal()
                    .source(Game.States.ANOTHER_TURN)
                    .target(Game.States.NEXT_PLAYER_TURN)
                    .event(GameEvent.Events.LAST_STONE_EMPTY_PIT)
                    .and()
                    .withExternal()
                    .source(Game.States.NEXT_PLAYER_TURN)
                    .target(Game.States.FINISHED)
                    .event(GameEvent.Events.PLAYER_OUT_OF_STONES)
                    .and()
                    .withExternal()
                    .source(Game.States.ANOTHER_TURN)
                    .target(Game.States.FINISHED)
                    .event(GameEvent.Events.PLAYER_OUT_OF_STONES);
        }
    }

    @Configuration
    public static class ServiceConfig {

        @Bean
        public StateMachineService<Game.States, GameEvent.Events> stateMachineService(
                StateMachineFactory<Game.States, GameEvent.Events> stateMachineFactory,
                StateMachineRuntimePersister<Game.States, GameEvent.Events, String> stateMachineRuntimePersister) {
            return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
        }
    }
}
