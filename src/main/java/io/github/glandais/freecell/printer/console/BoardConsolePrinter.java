package io.github.glandais.freecell.printer.console;

import io.github.glandais.freecell.Logger;
import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.MovementScore;
import io.github.glandais.freecell.board.enums.PilesEnum;
import io.github.glandais.freecell.board.enums.TableauPilesEnum;
import io.github.glandais.freecell.board.piles.Pile;
import io.github.glandais.freecell.cards.enums.CardColorEnum;
import io.github.glandais.freecell.cards.enums.CardEnum;
import io.github.glandais.freecell.printer.BoardPrinter;
import org.fusesource.jansi.Ansi;

import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class BoardConsolePrinter implements BoardPrinter {

    @Override
    public void print(Board board) {

        printSuite(board, PilesEnum.SUITE_CLUB);
        Logger.info(" ");
        printSuite(board, PilesEnum.SUITE_HEART);
        Logger.info(" ");
        printSuite(board, PilesEnum.SUITE_DIAMOND);
        Logger.info(" ");
        printSuite(board, PilesEnum.SUITE_SPADE);
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
//            Logger.info("XXXX");
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
            if (cardEnum.getCardColorEnum() == CardColorEnum.BLACK) {
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

    private void printSuite(Board board, PilesEnum pilesEnum) {
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
    public void printMovements(Board board, List<MovementScore> movements) {
        if (movements != null) {
            print(board);
            for (MovementScore movement : movements) {
                board.applyMovement(movement.movement());
                print(board);
                Logger.infoln(movement);
            }
        }
    }
}
