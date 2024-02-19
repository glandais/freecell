package io.github.glandais.freecell;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.printer.BoardConsolePrinter;
import io.github.glandais.freecell.solver.BoardSolver;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Board board = new Board(0);
        BoardConsolePrinter.print(board);
        BoardSolver boardSolver = new BoardSolver(board);
        List<Movement> movements = boardSolver.solve();
        BoardConsolePrinter.printMovements(board, movements);
    }

}
