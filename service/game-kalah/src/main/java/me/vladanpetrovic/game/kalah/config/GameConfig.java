package me.vladanpetrovic.game.kalah.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GameConfig.Settings.class)
public class GameConfig {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ConfigurationProperties(prefix = "kalah.game.settings")
    public static class Settings {

        private Integer numOfPits = 6;
        private Integer numOfStones = 6;
    }
}
