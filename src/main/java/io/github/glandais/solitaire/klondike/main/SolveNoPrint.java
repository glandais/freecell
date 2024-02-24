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

public class SolveNoPrint {

    public static void main(String[] args) {
        // 1126119823
        Board<KlondikePilesEnum> board = Klondike.INSTANCE.getBoard(0);
        KlondikeConsolePrinter klondikeConsolePrinter = new KlondikeConsolePrinter();
        klondikeConsolePrinter.print(board.copy());
        SolitaireSolver<KlondikePilesEnum> solitaireSolver = new SolitaireSolver<>(Klondike.INSTANCE, board.copy(), klondikeConsolePrinter);
        List<MovementScore<KlondikePilesEnum>> moves = solitaireSolver.solve();
        Serde.save("board.json", new BoardMoves(board.copy(), moves));
        if (moves != null) {
            SolitairePrinter<KlondikePilesEnum> solitairePrinter = new KlondikeGuiPrinter();
            solitairePrinter.printMovements(board.copy(), moves);
        }
    }

}
