package io.github.glandais.solitaire.klondike.printer.console;

import io.github.glandais.solitaire.klondike.Logger;
import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.Move;
import io.github.glandais.solitaire.klondike.board.MovementScore;
import io.github.glandais.solitaire.klondike.board.enums.PilesEnum;
import io.github.glandais.solitaire.klondike.board.enums.TableauPilesEnum;
import io.github.glandais.solitaire.klondike.board.piles.Pile;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;
import io.github.glandais.solitaire.klondike.cards.enums.ColorEnum;
import io.github.glandais.solitaire.klondike.printer.BoardPrinter;
import org.fusesource.jansi.Ansi;

import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class BoardConsolePrinter implements BoardPrinter {

    @Override
    public void print(Board board) {

        printFoundation(board, PilesEnum.FOUNDATION_CLUB);
        Logger.info(" ");
        printFoundation(board, PilesEnum.FOUNDATION_HEART);
        Logger.info(" ");
        printFoundation(board, PilesEnum.FOUNDATION_DIAMOND);
        Logger.info(" ");
        printFoundation(board, PilesEnum.FOUNDATION_SPADE);
        Logger.info("         ");
        printStock(board);
        Logger.infoln();
        Logger.infoln();

        boolean printed = true;
        int row = 0;
        while (printed) {
            printed = false;
            for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
                if (printTableau(board, row, tableauPilesEnum.getPilesEnum())) {
                    printed = true;
                }
                Logger.info(" ");
            }
            row++;
            Logger.infoln();
        }
    }

    private boolean printTableau(Board board, int row, PilesEnum pilesEnum) {
        Pile pile = getPile(board, pilesEnum);
        if (row < pile.getHidden().size()) {
            printCard(pile.getHidden().get(row), true);
            return true;
        } else {
            int rowVisible = row - pile.getHidden().size();
            if (rowVisible < pile.getVisible().size()) {
                printCard(pile.getVisible().get(rowVisible), false);
                return true;
            }
        }
        Logger.info("    ");
        return false;
    }

    private void printCard(CardEnum cardEnum, boolean hidden) {
        if (hidden) {
            Logger.info(cardEnum.getLabel());
        } else {
            Ansi ansi = ansi();
            if (cardEnum.getColorEnum() == ColorEnum.BLACK) {
                ansi = ansi.fg(WHITE).bg(BLACK);
            } else {
                ansi = ansi.fg(BLACK).bg(RED);
            }
            Logger.info(ansi.a(cardEnum.getLabel()).reset());
        }
    }

    private void printStock(Board board) {
        Pile pile = getPile(board, PilesEnum.STOCK);
        boolean first = true;
        for (CardEnum cardEnum : pile.getVisible().reversed()) {
            printCard(cardEnum, !first);
            Logger.info(" ");
            first = false;
        }
    }

    private void printFoundation(Board board, PilesEnum pilesEnum) {
        Pile pile = getPile(board, pilesEnum);
        if (pile.getVisible().isEmpty()) {
            Logger.info("    ");
        } else {
            printCard(pile.getVisible().getLast(), false);
        }
    }

    private Pile getPile(Board board, PilesEnum pilesEnum) {
        return board.getPiles().get(pilesEnum);
    }

    @Override
    public void printMovements(Board board, List<MovementScore> moves) {
        if (moves != null) {
            print(board);
            for (Move move : moves) {
                board.applyMovement(move);
                print(board);
                Logger.infoln(move);
            }
        }
    }

    @Override
    public void stop() {
        // NOOP
    }
}
