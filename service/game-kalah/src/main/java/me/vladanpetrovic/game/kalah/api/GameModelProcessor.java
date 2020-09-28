package me.vladanpetrovic.game.kalah.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.vladanpetrovic.game.kalah.data.Game;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class GameModelProcessor implements RepresentationModelProcessor<EntityModel<Game>> {

    private final @NonNull GameLinks gameLinks;

    @Override
    public EntityModel<Game> process(EntityModel<Game> resource) {
        var game = resource.getContent();

        if (game.getStatus().getPlayerTurn() == Game.Players.PLAYER1) {
            game.getPlayer1().getPits().forEach((pit, stones) -> {
                if (stones > 0) {
                    resource.add(gameLinks.getPitLink(game, pit));
                }
            });
        } else {
            game.getPlayer2().getPits().forEach((pit, stones) -> {
                if (stones > 0) {
                    resource.add(gameLinks.getPitLink(game, pit));
                }
            });
        }

        return resource;
    }
}
