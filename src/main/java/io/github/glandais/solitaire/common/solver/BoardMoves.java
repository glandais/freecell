package io.github.glandais.solitaire.common.solver;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.move.MovementScore;

import java.util.List;

public record BoardMoves(Board board, List moves) {
}
