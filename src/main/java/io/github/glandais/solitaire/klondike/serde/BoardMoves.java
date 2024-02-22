package io.github.glandais.solitaire.klondike.serde;

import io.github.glandais.solitaire.klondike.board.MovementScore;

import java.util.List;

public record BoardMoves(long seed, List<MovementScore> moves) {
}
