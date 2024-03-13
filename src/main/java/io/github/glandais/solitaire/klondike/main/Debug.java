package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.solver.RecursiveSolitaireSolver;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "debug", mixinStandardHelpOptions = true)
public class Debug implements Callable<Integer> {

    public static void main(String[] args) {
        new Debug().call();
    }

    @Override
    public Integer call() {
//        Board<KlondikePilesEnum> board = Klondike.INSTANCE.getBoard(321358391);
        Board<KlondikePilesEnum> board = Klondike.INSTANCE.getBoard(0);
        RecursiveSolitaireSolver<KlondikePilesEnum> solitaireSolver =
                new RecursiveSolitaireSolver<>(
                        Klondike.INSTANCE,
                        board,
                        -1,
                        new KlondikeConsolePrinter()
                );
        solitaireSolver.solve();
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
