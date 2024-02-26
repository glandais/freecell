package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.MovableStack;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayableBoard {

    private final Board<KlondikePilesEnum> board;
    private final PrintableBoard printableBoard;

    double dragX, dragY;
    List<DraggedCard> dragged = null;
    private MovableStack<KlondikePilesEnum> draggedStack;
    private Set<CardEnum> hideCards;
    private List<MovableStack<KlondikePilesEnum>> movableStacks;
    private List<Movement<KlondikePilesEnum>> possibleMovements;

    private int historyIndex;
    private List<MoveHistory> history;

    public PlayableBoard(Board<KlondikePilesEnum> board, PrintableBoard printableBoard) {
        this.board = board;
        this.printableBoard = printableBoard;
        this.historyIndex = -1;
        this.history = new ArrayList<>();
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
            historyIndex++;
            history = history.subList(0, historyIndex);
            history.add(new MoveHistory(movement, actions));
        }
        updatedBoard();
    }

    public void undo() {
        if (historyIndex >= 0) {
            List<CardAction<KlondikePilesEnum>> actions = history.get(historyIndex).actions();
            historyIndex--;
            board.revertMovement(actions);
            updatedBoard();
        }
    }

    public void redo() {
        if (historyIndex + 1 < history.size()) {
            historyIndex++;
            Movement<KlondikePilesEnum> movement = history.get(historyIndex).movement();
            board.applyMovement(movement);
            printableBoard.setCardsPosition();
            updatedBoard();
        }
    }

    public void mouseClicked(double x, double y, int clickCount) {
        if (clickCount == 2) {
            if (printableBoard.onStockVisible(x, y) || printableBoard.onStockPickable(x, y)) {
                Optional<Movement<KlondikePilesEnum>> stockToStock = possibleMovements.stream()
                        .filter(m -> m.from() == KlondikePilesEnum.STOCK && m.to() == KlondikePilesEnum.STOCK)
                        .findFirst();
                if (stockToStock.isPresent()) {
                    apply(stockToStock.get());
                    return;
                }
            }
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
        initDrag();
    }

    public void initDrag() {
        this.dragged = null;
        this.draggedStack = null;
        PrintableCard cardAt = printableBoard.getCardAt(dragX, dragY);

        if (cardAt != null) {
            Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
            if (!stock.hidden().isEmpty() && stock.hidden().getLast() == cardAt.getCard()) {
                this.draggedStack = new MovableStack<>(KlondikePilesEnum.STOCK, List.of(cardAt.getCard()));
            }

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
            }
            if (this.draggedStack != null) {
                this.dragged = printableBoard.stream()
                        .filter(p -> this.draggedStack.cards().contains(p.getCard()))
                        .map(DraggedCard::new)
                        .toList();
                updatePrintableBoard();
            } else if (matchedMovements.size() > 1) {
                Logger.infoln("invalid !");
            }
        }
    }

    public void mouseDragged(double x, double y) {
        if (dragged != null) {
            boolean fixed = false;
            Movement<KlondikePilesEnum> movement = getMovement(x, y);
            if (movement == null) {
                printableBoard.setCardsPosition();
            } else {
                fixed = true;
                Set<CardEnum> visible = getVisible();
                List<CardAction<KlondikePilesEnum>> actions = board.applyMovement(movement);
                Set<CardEnum> newVisible = getVisible();
                newVisible.removeAll(visible);
                hideCards = newVisible;
                printableBoard.setCardsPosition();
                board.revertMovement(actions);

                if (board.getPile(KlondikePilesEnum.STOCK).visible().isEmpty() &&
                        movement.from() == KlondikePilesEnum.STOCK && movement.to() == KlondikePilesEnum.STOCK) {
                }
            }
            updatePrintableBoard();
            if (!fixed) {
                for (DraggedCard draggedCard : dragged) {
                    draggedCard.printableCard().position.x = draggedCard.origX() + (x - dragX);
                    draggedCard.printableCard().position.y = draggedCard.origY() + (y - dragY);
                }
            }
        }
    }

    private Set<CardEnum> getVisible() {
        return board.getPileValues().stream()
                .map(Pile::visible)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    private Movement<KlondikePilesEnum> getMovement(double x, double y) {

        KlondikePilesEnum pilesEnum = printableBoard.getPileEnum(x, y);
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        boolean returnStock = false;
        if (!stock.visible().isEmpty()) {
            CardEnum first = stock.visible().getFirst();
            if (draggedStack.cards().equals(List.of(first)) &&
                    printableBoard.onHiddenStockPickable(x, y)) {
                returnStock = true;
            }
        }
        if (!stock.hidden().isEmpty()) {
            CardEnum last = stock.hidden().getLast();
            if (draggedStack.cards().equals(List.of(last)) &&
                    printableBoard.onStockVisible(x, y)) {
                returnStock = true;
            }
        }
        if (returnStock) {
            Optional<Movement<KlondikePilesEnum>> optionalMovement = possibleMovements.stream()
                    .filter(m -> m.from() == KlondikePilesEnum.STOCK && m.to() == KlondikePilesEnum.STOCK)
                    .findFirst();
            if (optionalMovement.isPresent()) {
                return optionalMovement.get();
            }
        }
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

    private void updatePrintableBoard() {
        this.dragged.forEach(d -> {
            d.printableCard().zIndex = d.origZ() - 10000;
            d.printableCard().dragged = true;
        });
        if (hideCards != null) {
            for (CardEnum hideCard : hideCards) {
                PrintableCard printableCard = printableBoard.getCardsMap().get(hideCard);
                printableCard.setFace(PrintableCardFace.JOCKER);
            }
        }
        printableBoard.sort();
    }

    public void mouseReleased(double x, double y) {
        if (dragged != null) {
            if (Math.hypot(x - dragX, y - dragY) > 20) {
                Movement<KlondikePilesEnum> movement = getMovement(x, y);
                apply(movement);
            }
            for (DraggedCard draggedCard : dragged) {
                draggedCard.printableCard().zIndex = draggedCard.origZ();
                draggedCard.printableCard().dragged = false;
            }
            printableBoard.setCardsPosition();
            dragged = null;
            hideCards = null;
        }
    }

}
