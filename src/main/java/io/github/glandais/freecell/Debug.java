package io.github.glandais.freecell;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.MovementScore;
import io.github.glandais.freecell.printer.console.BoardConsolePrinter;
import io.github.glandais.freecell.serde.Serde;
import io.github.glandais.freecell.serde.SerializableBoard;

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
