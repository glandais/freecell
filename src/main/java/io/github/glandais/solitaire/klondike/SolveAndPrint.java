package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.MovementScore;
import io.github.glandais.solitaire.klondike.printer.BoardPrinter;
import io.github.glandais.solitaire.klondike.printer.gui.BoardGuiPrinter;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;
import io.github.glandais.solitaire.klondike.serde.Serde;
import io.github.glandais.solitaire.klondike.solver.BoardSolver;

import java.util.List;

public class SolveAndPrint {

    public static void main(String[] args) {
        // 1126119823
        Board board = new Board(0);
        BoardPrinter boardPrinter = new BoardGuiPrinter();
        boardPrinter.print(board);
        BoardSolver boardSolver = new BoardSolver(board);
        List<MovementScore> moves = boardSolver.solve();
        if (moves != null) {
            Serde.save("board.json", new BoardMoves(board.getSeed(), moves));
            boardPrinter.printMovements(board, moves);
        } else {
            boardPrinter.stop();
        }
    }

}
