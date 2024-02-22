package io.github.glandais.freecell;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.MovementScore;
import io.github.glandais.freecell.printer.BoardPrinter;
import io.github.glandais.freecell.printer.gui.BoardGuiPrinter;
import io.github.glandais.freecell.serde.BoardMovements;
import io.github.glandais.freecell.serde.Serde;
import io.github.glandais.freecell.solver.BoardSolver;

import java.util.List;

public class SolveAndPrint {

    public static void main(String[] args) {
        // 1126119823
        Board board = new Board(0);
        BoardPrinter boardPrinter = new BoardGuiPrinter();
        boardPrinter.print(board);
        BoardSolver boardSolver = new BoardSolver(board);
        List<MovementScore> movements = boardSolver.solve();
        if (movements != null) {
            Serde.save("board.json", new BoardMovements(board.getSeed(), movements));
            boardPrinter.printMovements(board, movements);
        }
    }

}
