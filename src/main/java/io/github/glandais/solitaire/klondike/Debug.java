package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.MovementScore;
import io.github.glandais.solitaire.klondike.printer.console.BoardConsolePrinter;
import io.github.glandais.solitaire.klondike.serde.Serde;
import io.github.glandais.solitaire.klondike.serde.SerializableBoard;

import java.util.List;

public class Debug {

    public static void main(String[] args) {
        Board board = Serde.load("debug.json", SerializableBoard.class).toBoard();
        new BoardConsolePrinter().print(board);
        Logger.infoln(board.getScore());
        Logger.infoln("possibleMovements");
        List<MovementScore> possibleMovements = board.getPossibleMovements();
        for (MovementScore possibleMovement : possibleMovements) {
            Logger.infoln(possibleMovement);
        }
    }

}
