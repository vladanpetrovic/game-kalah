package me.vladanpetrovic.game.kalah.data;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.vladanpetrovic.game.kalah.config.GameConfig;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

@RepositoryEventHandler
@RequiredArgsConstructor
public class GameHandler {

    private final @NonNull GameRepository gameRepository;
    private final @NonNull GameConfig.Settings settings;

    @HandleAfterCreate
    public void onAfterCreate(Game game) {
        game.setup(settings);
        gameRepository.save(game);
    }
}
