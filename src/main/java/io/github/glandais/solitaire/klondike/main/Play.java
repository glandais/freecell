package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.gui.KlondikeGuiPrinter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "play", mixinStandardHelpOptions = true)
public class Play implements Callable<Integer> {

    @Option(names = {"--seed", "-s"})
    Integer seed;

    @Override
    public Integer call() {
        KlondikeGuiPrinter guiSolitairePrinter = new KlondikeGuiPrinter(true);
        Board<KlondikePilesEnum> board;
        if (seed == null) {
            board = Klondike.INSTANCE.getRandomBoard();
        } else {
            board = Klondike.INSTANCE.getBoard(this.seed);
        }
        guiSolitairePrinter.print(board);
        guiSolitairePrinter.awaitExit();
        return 0;
    }

}
