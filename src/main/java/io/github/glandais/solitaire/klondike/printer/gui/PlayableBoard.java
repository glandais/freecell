package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.MovableStack;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;

import java.util.ArrayList;
import java.util.List;

public class PlayableBoard {

    private final Board<KlondikePilesEnum> board;
    private final PrintableBoard printableBoard;

    double dragX, dragY;
    List<DraggedCard> dragged = null;
    private MovableStack<KlondikePilesEnum> draggedStack;
    private List<MovableStack<KlondikePilesEnum>> movableStacks;
    private List<Movement<KlondikePilesEnum>> possibleMovements;
    private List<List<CardAction<KlondikePilesEnum>>> actionList;

    public PlayableBoard(Board<KlondikePilesEnum> board, PrintableBoard printableBoard) {
        this.board = board;
        this.printableBoard = printableBoard;
        this.actionList = new ArrayList<>();
        updatedBoard();
    }

    private void updatedBoard() {
        movableStacks = board.getMovableStacks();
        Logger.infoln("movableStacks");
        for (MovableStack<KlondikePilesEnum> movableStack : movableStacks) {
            Logger.infoln(movableStack);
        }
        possibleMovements = board.computePossibleMovements();
        Logger.infoln("possibleMovements");
        for (Movement<KlondikePilesEnum> movement : possibleMovements) {
            Logger.infoln(movement);
        }
        printableBoard.setCardsPosition();
    }

    private void apply(Movement<KlondikePilesEnum> movement) {
        if (movement != null) {
            List<CardAction<KlondikePilesEnum>> actions = board.applyMovement(movement);
            actionList.add(actions);
        }
        updatedBoard();
    }

    public void undo() {
        if (!actionList.isEmpty()) {
            List<CardAction<KlondikePilesEnum>> actions = actionList.removeLast();
            board.revertMovement(actions);
            printableBoard.setCardsPosition();
            updatedBoard();
        }
    }

    public void redo() {
        // FIXME
    }

    public void mouseClicked(double x, double y, int clickCount) {
        if (clickCount == 2) {
            PrintableCard cardAt = printableBoard.getCardAt(x, y);
            if (cardAt != null) {
                List<Movement<KlondikePilesEnum>> matchedMovements = possibleMovements
                        .stream()
                        .filter(m -> m.cards().contains(cardAt.getCard()))
                        .toList();
                if (matchedMovements.size() > 1) {
                    matchedMovements = matchedMovements
                            .stream()
                            .filter(m -> !(m.from() == KlondikePilesEnum.STOCK && m.to() == KlondikePilesEnum.STOCK))
                            .toList();
                }
                if (matchedMovements.size() == 1) {
                    Movement<KlondikePilesEnum> movement = matchedMovements.getFirst();
                    apply(movement);
                }
            }
        }
    }

    public void mousePressed(double x, double y) {
        dragX = x;
        dragY = y;

        dragged = null;
        this.draggedStack = null;
        PrintableCard cardAt = printableBoard.getCardAt(x, y);
        System.out.println(cardAt);
        if (cardAt != null) {
            List<MovableStack<KlondikePilesEnum>> matchedMovements = movableStacks
                    .stream()
                    .filter(m -> m.cards().contains(cardAt.getCard()))
                    .toList();
            if (matchedMovements.size() > 1) {
                matchedMovements = matchedMovements
                        .stream()
                        .filter(m -> m.cards().size() == 1)
                        .toList();
            }
            if (matchedMovements.size() == 1) {
                this.draggedStack = matchedMovements.getFirst();
                this.dragged = printableBoard.stream()
                        .filter(p -> this.draggedStack.cards().contains(p.getCard()))
                        .map(DraggedCard::new)
                        .toList();
                setDragged();
            } else if (matchedMovements.size() > 1) {
                Logger.infoln("invalid !");
            }
        }
        System.out.println(this.draggedStack);
    }

    public void mouseDragged(double x, double y) {
        if (dragged != null) {
            boolean fixed = false;
            Movement<KlondikePilesEnum> movement = getMovement(x, y);
            if (movement == null) {
                printableBoard.setCardsPosition();
            } else {
                fixed = true;
                List<CardAction<KlondikePilesEnum>> actions = board.applyMovement(movement, false);
                printableBoard.setCardsPosition();
                board.revertMovement(actions);
            }
            setDragged();
            if (!fixed) {
                for (DraggedCard draggedCard : dragged) {
                    draggedCard.printableCard().position.x = draggedCard.origX() + (x - dragX);
                    draggedCard.printableCard().position.y = draggedCard.origY() + (y - dragY);
                }
            }
        }
    }

    private Movement<KlondikePilesEnum> getMovement(double x, double y) {
        KlondikePilesEnum pilesEnum = printableBoard.getPileEnum(x, y);
        if (pilesEnum != null) {
            for (Movement<KlondikePilesEnum> possibleMovement : possibleMovements) {
                if (possibleMovement.from() == draggedStack.from() &&
                        possibleMovement.to() == pilesEnum &&
                        possibleMovement.cards().equals(draggedStack.cards())) {
                    return possibleMovement;
                }
            }
        }
        return null;
    }

    private void setDragged() {
        this.dragged.forEach(d -> {
            d.printableCard().zIndex = d.origZ() - 10000;
            d.printableCard().dragged = true;
        });
        printableBoard.sort();
    }

    public void mouseReleased(double x, double y) {
        if (dragged != null) {
            Movement<KlondikePilesEnum> movement = getMovement(x, y);
            apply(movement);
            for (DraggedCard draggedCard : dragged) {
                draggedCard.printableCard().dragged = false;
            }
            dragged = null;
        }
    }

}
