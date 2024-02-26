package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.solver.SolitaireSolver;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;
import io.github.glandais.solitaire.klondike.serde.Serde;
import picocli.CommandLine.Command;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "debug", mixinStandardHelpOptions = true)
public class Debug implements Callable<Integer> {

    public static void main(String[] args) {
        new Debug().call();
    }

    @Override
    public Integer call() {
        Board<KlondikePilesEnum> board = Klondike.INSTANCE.getBoard(0);
        SolitaireSolver<KlondikePilesEnum> solitaireSolver = new SolitaireSolver<>(Klondike.INSTANCE, board, new KlondikeConsolePrinter());
        solitaireSolver.solveNodes();
        /*
        Board<KlondikePilesEnum> board = Serde.loadBoard("debug.json");
        KlondikeConsolePrinter klondikeConsolePrinter = new KlondikeConsolePrinter();
        klondikeConsolePrinter.print(board);
        Logger.infoln(Klondike.INSTANCE.getScore(board));
        Logger.infoln("possibleMovements");
        List<Movement<KlondikePilesEnum>> possibleMovements = board.computePossibleMovements();
        for (Movement<KlondikePilesEnum> possibleMovement : possibleMovements) {
            Logger.infoln(possibleMovement);
        }

         */
        return 0;
    }

}
