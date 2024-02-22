package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.printer.BoardPrinter;
import io.github.glandais.solitaire.klondike.printer.gui.BoardGuiPrinter;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;
import io.github.glandais.solitaire.klondike.serde.Serde;

public class Print {

    public static void main(String[] args) {
        BoardMoves boardMoves = Serde.load("board.json", BoardMoves.class);
        Board board = new Board(boardMoves.seed());
        BoardPrinter boardPrinter = new BoardGuiPrinter();
        boardPrinter.printMovements(board, boardMoves.moves());
    }

}
