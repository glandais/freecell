package io.github.glandais.freecell.board.printer;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.enums.PilesEnum;
import io.github.glandais.freecell.board.enums.TableauPilesEnum;
import io.github.glandais.freecell.board.piles.Pile;
import io.github.glandais.freecell.cards.enums.CardColorEnum;
import io.github.glandais.freecell.cards.enums.CardEnum;
import lombok.experimental.UtilityClass;
import org.fusesource.jansi.Ansi;

import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

@UtilityClass
public class BoardConsolePrinter {

    public static void print(Board board) {

        printSuite(board, PilesEnum.SUITE_CLUB);
        System.out.print(" ");
        printSuite(board, PilesEnum.SUITE_HEART);
        System.out.print(" ");
        printSuite(board, PilesEnum.SUITE_DIAMOND);
        System.out.print(" ");
        printSuite(board, PilesEnum.SUITE_SPADE);
        System.out.print("         ");
        printStock(board);
        System.out.println();
        System.out.println();

        boolean printed = true;
        int row = 0;
        while (printed) {
            printed = false;
            for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
                if (printTableau(board, row, tableauPilesEnum.getPilesEnum())) {
                    printed = true;
                }
                System.out.print(" ");
            }
            row++;
            System.out.println();
        }
    }

    private static boolean printTableau(Board board, int row, PilesEnum pilesEnum) {
        Pile pile = getPile(board, pilesEnum);
        if (row < pile.getHidden().size()) {
            printCard(pile.getHidden().get(row), true);
//            System.out.print("XXXX");
            return true;
        } else {
            int rowVisible = row - pile.getHidden().size();
            if (rowVisible < pile.getVisible().size()) {
                printCard(pile.getVisible().get(rowVisible), false);
                return true;
            }
        }
        System.out.print("    ");
        return false;
    }

    private static void printCard(CardEnum cardEnum, boolean hidden) {
        if (hidden) {
            System.out.print(cardEnum.getLabel());
        } else {
            Ansi ansi = ansi();
            if (cardEnum.getCardColorEnum() == CardColorEnum.BLACK) {
                ansi = ansi.fg(WHITE).bg(BLACK);
            } else {
                ansi = ansi.fg(BLACK).bg(RED);
            }
            System.out.print(ansi.a(cardEnum.getLabel()).reset());
        }
    }

    private static void printStock(Board board) {
        Pile pile = getPile(board, PilesEnum.STOCK);
        boolean first = true;
        for (CardEnum cardEnum : pile.getVisible().reversed()) {
            printCard(cardEnum, !first);
            System.out.print(" ");
            first = false;
        }
    }

    private static void printSuite(Board board, PilesEnum pilesEnum) {
        Pile pile = getPile(board, pilesEnum);
        if (pile.getVisible().isEmpty()) {
            System.out.print("    ");
        } else {
            printCard(pile.getVisible().getLast(), false);
        }
    }

    private static Pile getPile(Board board, PilesEnum pilesEnum) {
        return board.getPiles().get(pilesEnum);
    }

    public static void printMovements(Board board, List<Movement> movements) {
        if (movements != null) {
            print(board);
            for (Movement movement : movements) {
                board.applyMovement(movement);
                print(board);
                System.out.println(movement);
            }
        }
    }
}
