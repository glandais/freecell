package io.github.glandais.freecell.printer;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.MovementScore;

import java.util.List;

public interface BoardPrinter {
    void print(Board board);

    void printMovements(Board board, List<MovementScore> movements);
}
