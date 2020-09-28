package me.vladanpetrovic.game.kalah.data.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.vladanpetrovic.game.kalah.config.GameConfig;
import me.vladanpetrovic.game.kalah.data.GameHandler;
import me.vladanpetrovic.game.kalah.data.GameRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GameRepositoryConfig {

    private final @NonNull GameRepository gameRepository;
    private final @NonNull GameConfig.Settings settings;

    @Bean
    GameHandler gameHandler() {
        return new GameHandler(gameRepository, settings);
    }
}
