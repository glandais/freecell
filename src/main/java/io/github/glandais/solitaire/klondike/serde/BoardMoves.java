package io.github.glandais.solitaire.klondike.serde;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;

import java.util.List;

public record BoardMoves(Board<KlondikePilesEnum> board, List<MovementScore<KlondikePilesEnum>> moves) {
}
