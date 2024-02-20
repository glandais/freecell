package io.github.glandais.freecell;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.Movements;
import io.github.glandais.freecell.printer.BoardPrinter;
import io.github.glandais.freecell.printer.gui.BoardGuiPrinter;
import io.github.glandais.freecell.serde.BoardMovements;
import io.github.glandais.freecell.serde.Serde;
import io.github.glandais.freecell.solver.BoardSolver;

public class SolveAndPrint {

    public static void main(String[] args) {
        Board board = new Board(1126119823);
        BoardPrinter boardPrinter = new BoardGuiPrinter();
        boardPrinter.print(board);
        BoardSolver boardSolver = new BoardSolver(board);
        Movements movements = boardSolver.solve();
        if (movements != null) {
            Serde.save(new BoardMovements(board.getSeed(), movements));
            boardPrinter.printMovements(board, movements);
        }
    }

}
