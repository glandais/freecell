package io.github.glandais.freecell;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.BoardPrinter;
import io.github.glandais.freecell.solver.BoardSolver;

public class Main {

    public static void main(String[] args) {
        Board board = new Board(0);
        BoardPrinter.print(board);
        BoardSolver boardSolver = new BoardSolver(board);
        boardSolver.solve();
    }

}
