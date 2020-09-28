package me.vladanpetrovic.game.kalah.api;

import lombok.Getter;
import me.vladanpetrovic.game.kalah.data.Game;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.hal.HalLinkRelation;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.TypedEntityLinks;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class GameLinks {
	static final String PITS = "/pits";

	private final @Getter TypedEntityLinks<Game> gameLinks;

	GameLinks(EntityLinks entityLinks) {
		Assert.notNull(entityLinks, "EntityLinks must not be null!");
		this.gameLinks = entityLinks.forType(Game::getId);
	}

	Link getPitLink(Game game, Integer pitId) {
		return gameLinks.linkForItemResource(game).slash(PITS).slash(pitId)
				.withRel(HalLinkRelation.curied(game.getStatus().getPlayerTurn().toString().toLowerCase(), "pit" + pitId));
	}
}
