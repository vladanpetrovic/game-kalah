package me.vladanpetrovic.game.kalah.api;

import lombok.Builder;
import lombok.Value;
import me.vladanpetrovic.game.kalah.GameDataTestConfig;
import me.vladanpetrovic.game.kalah.data.Game;
import me.vladanpetrovic.game.kalah.data.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@Import(GameDataTestConfig.class)
public class GameApiDocumentation {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    WebApplicationContext context;
    protected MockMvc mockMvc;

    @BeforeEach
    protected void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context) //
                .apply(documentationConfiguration(restDocumentation)) //
                .build();
    }

    @Test
    void gamesResource() throws Exception {
        this.mockMvc.perform(get("/api/games").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andDo(document("games"))
                .andDo(print());
    }

    @Test
    void gameResource() throws Exception {
        this.mockMvc.perform(get("/api/games/1").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andDo(document("game"))
                .andDo(print());
    }

    @Test
    void movePitStones() throws Exception {
        performValidateAndDocumentPitStonesMove(MovePitStonesData.builder()
                .selectedPitId(1)
                .statusState(Game.States.ANOTHER_TURN.toString())
                .statusPlayerTurn(Game.Players.PLAYER1.toString())
                .player1pit1(0).player1pit2(7).player1pit3(7).player1pit4(7).player1pit5(7).player1pit6(7)
                .player1Kalah(1)
                .player2pit1(6).player2pit2(6).player2pit3(6).player2pit4(6).player2pit5(6).player2pit6(6)
                .player2Kalah(0)
                .pit1MovePossible(false).pit2MovePossible(true).pit3MovePossible(true)
                .pit4MovePossible(true).pit5MovePossible(true).pit6MovePossible(true)
                .documentName("game1-move-stones-player1-pit1")
                .build());
        performValidateAndDocumentPitStonesMove(MovePitStonesData.builder()
                .selectedPitId(4)
                .statusState(Game.States.NEXT_PLAYER_TURN.toString())
                .statusPlayerTurn(Game.Players.PLAYER2.toString())
                .player1pit1(0).player1pit2(7).player1pit3(7).player1pit4(0).player1pit5(8).player1pit6(8)
                .player1Kalah(2)
                .player2pit1(7).player2pit2(7).player2pit3(7).player2pit4(7).player2pit5(6).player2pit6(6)
                .player2Kalah(0)
                .pit1MovePossible(true).pit2MovePossible(true).pit3MovePossible(true)
                .pit4MovePossible(true).pit5MovePossible(true).pit6MovePossible(true)
                .documentName("game1-move-stones-player1-pit4")
                .build());
        performValidateAndDocumentPitStonesMove(MovePitStonesData.builder()
                .selectedPitId(2)
                .statusState(Game.States.NEXT_PLAYER_TURN.toString())
                .statusPlayerTurn(Game.Players.PLAYER1.toString())
                .player1pit1(1).player1pit2(8).player1pit3(7).player1pit4(0).player1pit5(8).player1pit6(8)
                .player1Kalah(2)
                .player2pit1(7).player2pit2(0).player2pit3(8).player2pit4(8).player2pit5(7).player2pit6(7)
                .player2Kalah(1)
                .pit1MovePossible(true).pit2MovePossible(true).pit3MovePossible(true)
                .pit4MovePossible(false).pit5MovePossible(true).pit6MovePossible(true)
                .documentName("game1-move-stones-player2-pit2")
                .build());
    }

    private void performValidateAndDocumentPitStonesMove(MovePitStonesData movePitStonesData) throws Exception {
        String playerTurn = movePitStonesData.getStatusPlayerTurn().toLowerCase();
        this.mockMvc.perform(put("/api/games/1/pits/" + movePitStonesData.getSelectedPitId()).accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status.state", is(movePitStonesData.getStatusState())))
                .andExpect(jsonPath("status.player_turn", is(movePitStonesData.getStatusPlayerTurn())))
                .andExpect(jsonPath("player1.pits.1", is(movePitStonesData.getPlayer1pit1())))
                .andExpect(jsonPath("player1.pits.2", is(movePitStonesData.getPlayer1pit2())))
                .andExpect(jsonPath("player1.pits.3", is(movePitStonesData.getPlayer1pit3())))
                .andExpect(jsonPath("player1.pits.4", is(movePitStonesData.getPlayer1pit4())))
                .andExpect(jsonPath("player1.pits.5", is(movePitStonesData.getPlayer1pit5())))
                .andExpect(jsonPath("player1.pits.6", is(movePitStonesData.getPlayer1pit6())))
                .andExpect(jsonPath("player1.kalah", is(movePitStonesData.getPlayer1Kalah())))
                .andExpect(jsonPath("player2.pits.1", is(movePitStonesData.getPlayer2pit1())))
                .andExpect(jsonPath("player2.pits.2", is(movePitStonesData.getPlayer2pit2())))
                .andExpect(jsonPath("player2.pits.3", is(movePitStonesData.getPlayer2pit3())))
                .andExpect(jsonPath("player2.pits.4", is(movePitStonesData.getPlayer2pit4())))
                .andExpect(jsonPath("player2.pits.5", is(movePitStonesData.getPlayer2pit5())))
                .andExpect(jsonPath("player2.pits.6", is(movePitStonesData.getPlayer2pit6())))
                .andExpect(jsonPath("player2.kalah", is(movePitStonesData.getPlayer2Kalah())))
                .andExpect(checkIfPlayerPitMoveLinkExists(playerTurn, 1, movePitStonesData.pit1MovePossible))
                .andExpect(checkIfPlayerPitMoveLinkExists(playerTurn, 2, movePitStonesData.pit2MovePossible))
                .andExpect(checkIfPlayerPitMoveLinkExists(playerTurn, 3, movePitStonesData.pit3MovePossible))
                .andExpect(checkIfPlayerPitMoveLinkExists(playerTurn, 4, movePitStonesData.pit4MovePossible))
                .andExpect(checkIfPlayerPitMoveLinkExists(playerTurn, 5, movePitStonesData.pit5MovePossible))
                .andExpect(checkIfPlayerPitMoveLinkExists(playerTurn, 6, movePitStonesData.pit6MovePossible))
                .andDo(document(movePitStonesData.getDocumentName()))
                .andDo(print());

    }

   private ResultMatcher checkIfPlayerPitMoveLinkExists(String playerTurn, int pitId, boolean pitMovePossible) {
        String playerPitMoveLinkJsonPath = "_links." + playerTurn + ":pit" + pitId;
        return pitMovePossible ?
                jsonPath(playerPitMoveLinkJsonPath).exists() :
                jsonPath(playerPitMoveLinkJsonPath).doesNotExist();
    }

    @Value
    @Builder
    static class MovePitStonesData {
        int selectedPitId;
        String statusState, statusPlayerTurn;
        int player1pit1, player1pit2, player1pit3, player1pit4, player1pit5, player1pit6, player1Kalah;
        int player2pit1, player2pit2, player2pit3, player2pit4, player2pit5, player2pit6, player2Kalah;
        boolean pit1MovePossible, pit2MovePossible, pit3MovePossible, pit4MovePossible, pit5MovePossible, pit6MovePossible;
        String documentName;
    }

    private RestDocumentationResultHandler document(String name, Snippet... snippets) {
        return MockMvcRestDocumentation.document(name, snippets);
    }
}
