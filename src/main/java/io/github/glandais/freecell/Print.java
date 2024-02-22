package io.github.glandais.freecell;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.printer.BoardPrinter;
import io.github.glandais.freecell.printer.gui.BoardGuiPrinter;
import io.github.glandais.freecell.serde.BoardMovements;
import io.github.glandais.freecell.serde.Serde;

public class Print {

    public static void main(String[] args) {
        BoardMovements boardMovements = Serde.load("board.json", BoardMovements.class);
        Board board = new Board(boardMovements.seed());
        BoardPrinter boardPrinter = new BoardGuiPrinter();
        boardPrinter.printMovements(board, boardMovements.movements());
    }

}
