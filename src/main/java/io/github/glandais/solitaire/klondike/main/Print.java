package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;
import io.github.glandais.solitaire.klondike.printer.gui.KlondikeGuiPrinter;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;
import io.github.glandais.solitaire.klondike.serde.Serde;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "print", mixinStandardHelpOptions = true)
public class Print implements Callable<Integer> {

    @Override
    public Integer call() {
        BoardMoves boardMoves = Serde.load("board.json", BoardMoves.class);
        Board<KlondikePilesEnum> board = boardMoves.board();
        KlondikeConsolePrinter klondikeConsolePrinter = new KlondikeConsolePrinter();
        klondikeConsolePrinter.printMovements(board, boardMoves.moves());
        KlondikeGuiPrinter klondikeGuiPrinter = new KlondikeGuiPrinter();
        klondikeGuiPrinter.printMovements(board, boardMoves.moves());
        klondikeGuiPrinter.awaitExit();
        return 0;
    }

}
