package io.github.glandais.solitaire.common.printer;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.move.MovementScore;

import java.util.List;

public interface SolitairePrinter<T extends PileType<T>> {
    void print(Board<T> board);

    void printMovements(Board<T> board, List<MovementScore<T>> movements);

    void stop();
}
