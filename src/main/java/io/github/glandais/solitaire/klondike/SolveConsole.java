package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.MovementScore;
import io.github.glandais.solitaire.klondike.printer.BoardPrinter;
import io.github.glandais.solitaire.klondike.printer.console.BoardConsolePrinter;
import io.github.glandais.solitaire.klondike.printer.gui.BoardGuiPrinter;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;
import io.github.glandais.solitaire.klondike.serde.Serde;
import io.github.glandais.solitaire.klondike.solver.BoardSolver;

import java.util.List;

public class SolveConsole {

    public static void main(String[] args) {
        // 1126119823
        Board board = new Board(0);
        BoardConsolePrinter boardConsolePrinter = new BoardConsolePrinter();
        boardConsolePrinter.print(board);
        BoardSolver boardSolver = new BoardSolver(board);
        List<MovementScore> moves = boardSolver.solve();
        Serde.save("board.json", new BoardMoves(board.getSeed(), moves));
        if (moves != null) {
            boardConsolePrinter.printMovements(board, moves);
        }
    }

}
