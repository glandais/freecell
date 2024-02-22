package io.github.glandais.solitaire.klondike.printer;

import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.MovementScore;

import java.util.List;

public interface BoardPrinter {
    void print(Board board);

    void printMovements(Board board, List<MovementScore> movements);

    void stop();
}
