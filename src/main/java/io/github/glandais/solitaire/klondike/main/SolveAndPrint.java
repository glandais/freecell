package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.printer.SolitairePrinter;
import io.github.glandais.solitaire.common.solver.SolitaireSolver;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;
import io.github.glandais.solitaire.klondike.printer.gui.KlondikeGuiPrinter;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;
import io.github.glandais.solitaire.klondike.serde.Serde;

import java.util.List;

public class SolveAndPrint {

    public static void main(String[] args) {
        // 1126119823
        Board<KlondikePilesEnum> board = Klondike.INSTANCE.getBoard(0);
        SolitairePrinter<KlondikePilesEnum> solitairePrinter = new KlondikeGuiPrinter();
        solitairePrinter.print(board);
        SolitaireSolver<KlondikePilesEnum> solitaireSolver = new SolitaireSolver<>(Klondike.INSTANCE, board, new KlondikeConsolePrinter());
        List<MovementScore<KlondikePilesEnum>> moves = solitaireSolver.solve();
        if (moves != null) {
            Serde.save("board.json", new BoardMoves(board, moves));
            solitairePrinter.printMovements(board, moves);
        } else {
            solitairePrinter.stop();
        }
    }

}
