package io.github.glandais.freecell;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.printer.console.BoardConsolePrinter;
import io.github.glandais.freecell.printer.BoardPrinter;
import io.github.glandais.freecell.serde.Serde;

public class Print {

    public static void main(String[] args) {
        BoardMovements boardMovements = Serde.load();
        Board board = new Board(boardMovements.seed());
        BoardPrinter boardPrinter = new BoardConsolePrinter();
        boardPrinter.printMovements(board, boardMovements.movements());
    }

}
