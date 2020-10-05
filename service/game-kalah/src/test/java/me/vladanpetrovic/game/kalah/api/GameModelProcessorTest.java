package me.vladanpetrovic.game.kalah.api;

import me.vladanpetrovic.game.kalah.config.GameConfig;
import me.vladanpetrovic.game.kalah.data.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.TypedEntityLinks;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameModelProcessorTest {

    @Mock
    EntityLinks entityLinks;
    @Mock
    TypedEntityLinks typedEntityLinks;

    Game game;
    GameLinks gameLinks;
    GameModelProcessor processor;

    @BeforeEach
    void setUp() {
        HttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        when(entityLinks.forType(any())).thenReturn(typedEntityLinks);
        when(typedEntityLinks.linkForItemResource(any(Game.class))).thenReturn(WebMvcLinkBuilder.linkTo(Game.class));

        gameLinks = new GameLinks(entityLinks);
        processor = new GameModelProcessor(gameLinks);
        game = new Game();
        game.setup(new GameConfig.Settings());
    }

    @Test
    void initialPlayer1HasAllPitMoveLinks() {
        // when
        EntityModel<Game> resource = processor.process(EntityModel.of(game));

        // then
        assertThat(resource.hasLinks()).isTrue();
        assertThat(resource.getLinks().stream().count()).isEqualTo(6);
        int pitId = 1;
        for (Link link : resource.getLinks()) {
            assertThat(link.getRel().toString()).isEqualTo(getPlayer1LinkRelForPitId(pitId));
            assertThat(link.getHref()).contains(getPitsIdLinkHrefPart(pitId));
            pitId++;
        }
    }

    @Test
    void player1MovePit1StonesAndHasOtherPitMoveLinks() {
        // given
        game.movePitStones(1);

        // when
        EntityModel<Game> resource = processor.process(EntityModel.of(game));

        // then
        assertThat(resource.hasLinks()).isTrue();
        assertThat(resource.getLinks().stream().count()).isEqualTo(5);
        int pitId = 2;
        for (Link link : resource.getLinks()) {
            assertThat(link.getRel().toString()).isNotEqualTo(getPlayer1LinkRelForPitId(1));
            assertThat(link.getRel().toString()).isEqualTo(getPlayer1LinkRelForPitId(pitId));
            assertThat(link.getHref()).contains(getPitsIdLinkHrefPart(pitId));
            pitId++;
        }
    }

    private String getPitsIdLinkHrefPart(int pitId) {
        return "pits/" + pitId;
    }

    private String getPlayer1LinkRelForPitId(int pitId) {
        return "player1:pit" + pitId;
    }

}
