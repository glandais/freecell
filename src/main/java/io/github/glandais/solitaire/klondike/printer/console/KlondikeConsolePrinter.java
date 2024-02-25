package io.github.glandais.solitaire.klondike.printer.console;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.cards.ColorEnum;
import io.github.glandais.solitaire.common.cards.OrderEnum;
import io.github.glandais.solitaire.common.move.Move;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.printer.SolitairePrinter;
import io.github.glandais.solitaire.klondike.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.TableauPilesEnum;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.fusesource.jansi.Ansi.ansi;

public class KlondikeConsolePrinter implements SolitairePrinter<KlondikePilesEnum> {

    int cardHeight = 3;
    int cardWidth = 4;

    @Override
    public void stop() {
        // NOOP
    }

    @Override
    public void printMovements(Board<KlondikePilesEnum> boardParam, List<MovementScore<KlondikePilesEnum>> moves) {
        if (moves != null) {
            for (Move<KlondikePilesEnum> move : moves) {
                Logger.infoln(move);
            }
            Board<KlondikePilesEnum> board = boardParam.copy();
            print(board);
            for (Move<KlondikePilesEnum> move : moves) {
                Logger.infoln(move);
                board.applyMovement(move);
                print(board);
            }
        }
    }

    @Override
    public void print(Board<KlondikePilesEnum> board) {
        List<PrintableCard> printableCards = getPrintableCards(board);
        printCards(printableCards);
    }

    private List<PrintableCard> getPrintableCards(Board<KlondikePilesEnum> board) {
        List<PrintableCard> printableCards = new ArrayList<>();
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            Pile<KlondikePilesEnum> foundation = board.getPile(foundationPilesEnum.getKlondikePilesEnum());
            int i = foundationPilesEnum.ordinal() * (cardWidth + 1);
            int j = 0;
            if (!foundation.visible().isEmpty()) {
                CardEnum last = foundation.visible().getLast();
                printableCards.add(new PrintableCard(i, j, 0, false, last, false));
            } else {
                printableCards.add(new PrintableCard(i, j, 0, true, null, false));
            }
        }
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        if (!stock.visible().isEmpty()) {
            CardEnum visible = stock.visible().getFirst();
            printableCards.add(new PrintableCard(8 * (cardWidth + 1), 0, 0, false, visible, false));
        } else {
            printableCards.add(new PrintableCard(8 * (cardWidth + 1), 0, 0, true, null, false));
        }
        int di = 0;
        for (CardEnum hidden : stock.hidden()) {
            printableCards.add(new PrintableCard(9 * (cardWidth + 1) + di, 0, -di, false, hidden, true));
            di++;
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            int i = tableauPilesEnum.ordinal() * (cardWidth + 1);
            int j = cardHeight + 1;
            Pile<KlondikePilesEnum> pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
            for (CardEnum hidden : pile.hidden()) {
                printableCards.add(new PrintableCard(i, j, j, false, hidden, true));
                j++;
            }
            for (CardEnum hidden : pile.visible()) {
                printableCards.add(new PrintableCard(i, j, j, false, hidden, false));
                j++;
            }
        }
        return printableCards;
    }

    private void printCards(List<PrintableCard> printableCards) {
        int maxi = 0;
        int maxj = 0;
        for (PrintableCard printableCard : printableCards) {
            maxi = Math.max(printableCard.i() + cardWidth, maxi);
            maxj = Math.max(printableCard.j() + cardHeight, maxj);
        }
        for (int j = 0; j < maxj; j++) {
            for (int i = 0; i < maxi; i++) {
                int finalI = i;
                int finalJ = j;
                Optional<PrintableCard> optionalPrintableCard = printableCards.stream()
                        .filter(p -> inCell(p, finalI, finalJ))
                        .max(Comparator.comparing(PrintableCard::zIndex));
                if (optionalPrintableCard.isPresent()) {
                    PrintableCard p = optionalPrintableCard.get();
                    printCard(p, i - p.i(), j - p.j());
                } else {
                    Logger.info(" ");
                }
            }
            Logger.infoln();
        }
    }

    private boolean inCell(PrintableCard p, int i, int j) {
        return p.i() <= i && i < p.i() + cardWidth &&
                p.j() <= j && j < p.j() + cardHeight;
    }

    private void printCard(PrintableCard printableCard, int i, int j) {
        if (printableCard == null) {
            Logger.info(" ");
        }
        if (printableCard.emptyStack()) {
            Logger.info(ansi().bg(Ansi.Color.WHITE).a(" ").reset());
        } else {
            if (printableCard.hidden()) {
                if (i + j < 2) {
                    Logger.info("░");
                } else if (i + j >= 4) {
                    Logger.info("▒");
                } else {
                    Logger.info("▓");
                }
            } else {
                String label = " ";
                if (
                        (i == 0 && (j == 0 || j == 2)) ||
                                (i == 2 && j == 1)
                ) {
                    label = printableCard.card().getSuiteEnum().getLabel();
                } else if (printableCard.card().getOrderEnum() == OrderEnum.TEN) {
                    if (i == 2 && (j == 0 || j == 2)) {
                        label = "1";
                    } else if (i == 3 && (j == 0 || j == 2)) {
                        label = "0";
                    }
                } else if (i == 3 && (j == 0 || j == 2)) {
                    label = "" + printableCard.card().getOrderEnum().getLabel();
                }
                Logger.info(ansi().bgBright(Ansi.Color.WHITE).fgBright(printableCard.card().getColorEnum() == ColorEnum.RED ? Ansi.Color.RED : Ansi.Color.BLACK).a(label).reset());
            }
        }
    }
}
