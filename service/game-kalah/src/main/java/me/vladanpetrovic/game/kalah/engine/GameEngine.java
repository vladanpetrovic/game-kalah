package me.vladanpetrovic.game.kalah.engine;

import me.vladanpetrovic.game.kalah.data.Game;

public interface GameEngine {
    Game movePitStones(String gameId, Integer pitId);
}
