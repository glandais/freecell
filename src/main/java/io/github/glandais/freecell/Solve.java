package io.github.glandais.freecell;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.Movements;
import io.github.glandais.freecell.printer.console.BoardConsolePrinter;
import io.github.glandais.freecell.printer.BoardPrinter;
import io.github.glandais.freecell.serde.Serde;
import io.github.glandais.freecell.solver.BoardSolver;

public class Solve {

    public static void main(String[] args) {
        int seed = 0;
        Board board = new Board(seed);
        BoardPrinter boardPrinter = new BoardConsolePrinter();
        boardPrinter.print(board);
        BoardSolver boardSolver = new BoardSolver(board);
        Movements movements = boardSolver.solve();
        Serde.save(new BoardMovements(seed, movements));
    }

}
