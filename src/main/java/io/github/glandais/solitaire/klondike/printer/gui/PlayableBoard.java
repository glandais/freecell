package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.move.MovableStack;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;

import java.util.List;

public class PlayableBoard {

    private final Board<KlondikePilesEnum> board;
    private final PrintableBoard printableBoard;

    double startX, startY;
    double origX, origY;
    int origZ;
    PrintableCard dragged = null;
    private List<MovableStack<KlondikePilesEnum>> movableStacks;
    private List<Movement<KlondikePilesEnum>> possibleMovements;

    public PlayableBoard(Board<KlondikePilesEnum> board, PrintableBoard printableBoard) {
        this.board = board;
        this.printableBoard = printableBoard;
        updatedBoard();
    }

    private void updatedBoard() {
        movableStacks = board.getMovableStacks();
        possibleMovements = board.computePossibleMovements();
    }

    public void mouseClicked(double x, double y, int clickCount) {
        if (clickCount == 2) {
            PrintableCard cardAt = getCardAt(x, y);
            System.out.println(cardAt);
        }
    }

    public void mousePressed(double x, double y) {
        startX = x;
        startY = y;
        dragged = getCardAt(x, y);
        if (dragged != null) {
            origX = dragged.position.x;
            origY = dragged.position.y;
            origZ = dragged.zIndex;
        }
    }

    public void mouseDragged(double x, double y) {
        if (dragged != null) {
            dragged.position.x = origX + (x - startX);
            dragged.position.y = origY + (y - startY);
            dragged.zIndex = -10000;
            printableBoard.sort();
        }
    }

    public void mouseReleased(double x, double y) {
        if (dragged != null) {
            dragged.position.x = origX;
            dragged.position.y = origY;
            dragged.zIndex = origZ;
            printableBoard.sort();
        }
    }

    private PrintableCard getCardAt(double x, double y) {
        for (PrintableCard printableCard : this.printableBoard.reversed()) {
            if (inBounds(printableCard, x, y)) {
                return printableCard;
            }
        }
        return null;
    }

    private boolean inBounds(PrintableCard printableCard, double x, double y) {
        Vector2 position = printableCard.position;
        return position.x <= x &&
                x <= position.x + Constants.CARD_WIDTH &&
                position.y <= y &&
                y <= position.y + Constants.CARD_HEIGHT;
    }

}
