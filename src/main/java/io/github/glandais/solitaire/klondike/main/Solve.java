package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.solver.SolitaireSolver;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;
import io.github.glandais.solitaire.klondike.printer.gui.KlondikeGuiPrinter;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;
import io.github.glandais.solitaire.klondike.serde.Serde;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "solve", mixinStandardHelpOptions = true)
public class Solve implements Callable<Integer> {

    @Option(names = {"--seed", "-s"})
    Integer seed;

    @Option(names = "--follow")
    boolean follow;

    @Override
    public Integer call() {
        // 1126119823
        Board<KlondikePilesEnum> board;
        if (seed == null) {
            board = Klondike.INSTANCE.getRandomBoard();
        } else {
            board = Klondike.INSTANCE.getBoard(this.seed);
        }
        KlondikeGuiPrinter guiSolitairePrinter = new KlondikeGuiPrinter(false);
        KlondikeConsolePrinter klondikeConsolePrinter = new KlondikeConsolePrinter();
        klondikeConsolePrinter.print(board);
        if (follow) {
            guiSolitairePrinter.print(board);
        } else {
            guiSolitairePrinter.print(board.copy());
        }
        SolitaireSolver<KlondikePilesEnum> solitaireSolver = new SolitaireSolver<>(Klondike.INSTANCE, board, klondikeConsolePrinter);
        List<MovementScore<KlondikePilesEnum>> moves = solitaireSolver.solve();
        if (moves != null) {
            Serde.save("board.json", new BoardMoves(board, moves));
            klondikeConsolePrinter.printMovements(board, moves);
            guiSolitairePrinter.printMovements(board, moves);
            guiSolitairePrinter.awaitExit();
        } else {
            guiSolitairePrinter.stop();
        }
        return 0;
    }

}
