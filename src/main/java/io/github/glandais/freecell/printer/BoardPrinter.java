package io.github.glandais.freecell.printer;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.Movements;

public interface BoardPrinter {
    void print(Board board);

    void printMovements(Board board, Movements movements);
}
