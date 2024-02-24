package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.printer.SolitairePrinter;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;
import io.github.glandais.solitaire.klondike.printer.gui.KlondikeGuiPrinter;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;
import io.github.glandais.solitaire.klondike.serde.Serde;

public class Print {

    public static void main(String[] args) {
        BoardMoves boardMoves = Serde.load("board.json", BoardMoves.class);
        Board<KlondikePilesEnum> board = boardMoves.board();
        SolitairePrinter<KlondikePilesEnum> solitairePrinter = new KlondikeConsolePrinter();
        solitairePrinter.printMovements(board, boardMoves.moves());
        solitairePrinter = new KlondikeGuiPrinter();
        solitairePrinter.printMovements(board, boardMoves.moves());
    }

}
