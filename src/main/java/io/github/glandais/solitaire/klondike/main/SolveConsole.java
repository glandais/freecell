package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.solver.SolitaireSolver;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;
import io.github.glandais.solitaire.klondike.serde.Serde;

import java.util.List;

public class SolveConsole {

    public static void main(String[] args) {
        // 1126119823
        Board<KlondikePilesEnum> board = Klondike.INSTANCE.getBoard(0);
        KlondikeConsolePrinter boardConsolePrinter = new KlondikeConsolePrinter();
        boardConsolePrinter.print(board);
        SolitaireSolver<KlondikePilesEnum> solitaireSolver = new SolitaireSolver<>(Klondike.INSTANCE, board, boardConsolePrinter);
        List<MovementScore<KlondikePilesEnum>> moves = solitaireSolver.solve();
        Serde.save("board.json", new BoardMoves(board, moves));
        if (moves != null) {
            boardConsolePrinter.printMovements(board, moves);
        }
    }

}
