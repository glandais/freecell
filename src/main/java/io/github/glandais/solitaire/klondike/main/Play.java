package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.printer.gui.KlondikeGuiPrinter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "play", mixinStandardHelpOptions = true)
public class Play implements Callable<Integer> {

    @Option(names = {"--seed", "-s"}, defaultValue = "0")
    int seed;

    @Override
    public Integer call() {
        KlondikeGuiPrinter guiSolitairePrinter = new KlondikeGuiPrinter(true);
        guiSolitairePrinter.print(Klondike.INSTANCE.getBoard(this.seed));
        guiSolitairePrinter.awaitExit();
        return 0;
    }

}
