package me.vladanpetrovic.game.kalah.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.vladanpetrovic.game.kalah.data.Game;
import me.vladanpetrovic.game.kalah.engine.GameEngine;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/games")
@ExposesResourceFor(Game.class)
@RequiredArgsConstructor
public class GameApiEndpoint {

    private final @NonNull GameEngine gameEngine;

    @PutMapping(path = "/{gameId}/pits/{pitId}")
    ResponseEntity<?> movePitStones(@PathVariable("gameId") String gameId, @PathVariable("pitId") Integer pitId) {
        var game = gameEngine.movePitStones(gameId, pitId);
        return ResponseEntity.ok().body(EntityModel.of(game));
    }
}
