package io.github.glandais.solitaire.klondike.printer.console;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.cards.ColorEnum;
import io.github.glandais.solitaire.common.move.Move;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.printer.SolitairePrinter;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.TableauPilesEnum;
import org.fusesource.jansi.Ansi;

import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class KlondikeConsolePrinter implements SolitairePrinter<KlondikePilesEnum> {

    @Override
    public void print(Board<KlondikePilesEnum> board) {

        Logger.infoln(Klondike.INSTANCE.getScore(board));
        Logger.infoln(board.computeState());
        printFoundation(board, KlondikePilesEnum.FOUNDATION_CLUB);
        Logger.info(" ");
        printFoundation(board, KlondikePilesEnum.FOUNDATION_HEART);
        Logger.info(" ");
        printFoundation(board, KlondikePilesEnum.FOUNDATION_DIAMOND);
        Logger.info(" ");
        printFoundation(board, KlondikePilesEnum.FOUNDATION_SPADE);
        Logger.info("         ");
        printStock(board);
        Logger.infoln();
        Logger.infoln();

        boolean printed = true;
        int row = 0;
        while (printed) {
            printed = false;
            for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
                if (printTableau(board, row, tableauPilesEnum.getKlondikePilesEnum())) {
                    printed = true;
                }
                Logger.info(" ");
            }
            row++;
            Logger.infoln();
        }
    }

    private boolean printTableau(Board<KlondikePilesEnum> board, int row, KlondikePilesEnum klondikePilesEnum) {
        Pile<?> pile = getPile(board, klondikePilesEnum);
        if (row < pile.hidden().size()) {
            printCard(pile.hidden().get(row), true);
            return true;
        } else {
            int rowVisible = row - pile.hidden().size();
            if (rowVisible < pile.visible().size()) {
                printCard(pile.visible().get(rowVisible), false);
                return true;
            }
        }
        Logger.info("  ");
        return false;
    }

    private void printCard(CardEnum cardEnum, boolean hidden) {
        if (hidden) {
            Logger.info(cardEnum.getLabel());
        } else {
            Logger.info(getAnsi(cardEnum).a(cardEnum.getLabel()).reset());
        }
    }

    private static Ansi getAnsi(CardEnum cardEnum) {
        Ansi ansi = ansi();
        if (cardEnum.getColorEnum() == ColorEnum.BLACK) {
            ansi = ansi.fg(WHITE).bg(BLACK);
        } else {
            ansi = ansi.fg(BLACK).bg(RED);
        }
        return ansi;
    }

    private void printStock(Board<KlondikePilesEnum> board) {
        Pile<KlondikePilesEnum> pile = getPile(board, KlondikePilesEnum.STOCK);
        for (CardEnum cardEnum : pile.visible().reversed()) {
            printCard(cardEnum, false);
            Logger.info(" ");
        }
        for (CardEnum cardEnum : pile.hidden().reversed()) {
            printCard(cardEnum, true);
            Logger.info(" ");
        }
    }

    private void printFoundation(Board<KlondikePilesEnum> board, KlondikePilesEnum klondikePilesEnum) {
        Pile<KlondikePilesEnum> pile = getPile(board, klondikePilesEnum);
        if (pile.visible().isEmpty()) {
            Logger.info("  ");
        } else {
            printCard(pile.visible().getLast(), false);
        }
    }

    private Pile<KlondikePilesEnum> getPile(Board<KlondikePilesEnum> board, KlondikePilesEnum klondikePilesEnum) {
        return board.getPile(klondikePilesEnum);
    }

    @Override
    public void printMovements(Board<KlondikePilesEnum> board, List<MovementScore<KlondikePilesEnum>> moves) {
        if (moves != null) {
            print(board);
            for (Move<KlondikePilesEnum> move : moves) {
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
