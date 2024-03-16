package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.MovableStack;
import io.github.glandais.solitaire.common.move.Move;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.PileTypeEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class PlayableBoard {

    private final Board<KlondikePilesEnum> board;
    private final PrintableBoard printableBoard;

    double dragX;
    double dragY;
    List<DraggedCard> dragged = null;
    private MovableStack<KlondikePilesEnum> draggedStack;
    private Set<CardEnum> hideCards;
    private List<MovableStack<KlondikePilesEnum>> movableStacks;
    private List<Movement<KlondikePilesEnum>> possibleMovements;
    private List<MovementScore<KlondikePilesEnum>> orderedMovements;

    private int historyIndex;
    private List<MoveHistory> history;

    private final KlondikeConsolePrinter consolePrinter = new KlondikeConsolePrinter();

    public PlayableBoard(Board<KlondikePilesEnum> board, PrintableBoard printableBoard) {
        this.board = board;
        this.printableBoard = printableBoard;
        this.historyIndex = -1;
        this.history = new ArrayList<>();
        afterMove();
    }

    public void mousePressed(double x, double y) {
        this.dragX = x;
        this.dragY = y;
        this.draggedStack = null;
        this.dragged = null;
        PrintableCard cardAt = printableBoard.getCardAt(dragX, dragY);

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
                updatePrintableBoard();
            }
        }
    }

    public void mouseDragged(double x, double y) {
        if (dragged != null) {
            boolean fixed = false;
            Movement<KlondikePilesEnum> movement = getMovement(x, y);
            if (movement == null) {
                printableBoard.setCardsPosition();
                hideCards = Set.of();
            } else {
                fixed = true;
                Set<CardEnum> visible = getVisible();
                List<CardAction<KlondikePilesEnum>> actions = board.applyMovement(movement);
                Set<CardEnum> newVisible = getVisible();
                newVisible.removeAll(visible);
                hideCards = newVisible;
                printableBoard.setCardsPosition();
                board.revertMovement(actions);
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
            dragged = null;
            hideCards = null;
        }
        printableBoard.setCardsPosition();
    }

    public void mouseClicked(double x, double y, MouseButton mouseButton, int clickCount) {
        if (mouseButton == MouseButton.BACK) {
            undo();
        } else if (mouseButton == MouseButton.FORWARD) {
            redo();
        } else if (mouseButton == MouseButton.PRIMARY && clickCount == 2) {
            PrintableCard cardAt = printableBoard.getCardAt(x, y);
            if (cardAt != null) {
                if (!applySingleMovementMatching(m -> m.getCards().contains(cardAt.getCard()))) {
                    applySingleMovementMatching(m -> m.getCards().contains(cardAt.getCard()) && m.getTo().getPileTypeEnum() == PileTypeEnum.FOUNDATION);
                }
            }
        }
    }

    public void keyReleased(KeyCode code) {
        if (code == KeyCode.LEFT) {
            undo();
        }
        if (code == KeyCode.RIGHT) {
            redo();
        }
        if (code == KeyCode.S || code == KeyCode.DOWN && !orderedMovements.isEmpty()) {
            apply(orderedMovements.getFirst());
        }
    }

    private void afterMove() {
//        consolePrinter.print(board);
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
        orderedMovements = Klondike.INSTANCE.getOrderedMovements(board);
        Logger.infoln("orderedMovements");
        for (Move<KlondikePilesEnum> move : orderedMovements) {
            Logger.infoln(move);
        }
        printableBoard.setCardsPosition();
    }

    private void updatePrintableBoard() {
        if (dragged != null) {
            this.dragged.forEach(d -> {
                d.printableCard().zIndex = d.origZ() - 10000;
                d.printableCard().dragged = true;
            });
        }
        if (hideCards != null) {
            for (CardEnum hideCard : hideCards) {
                PrintableCard printableCard = printableBoard.getCardsMap().get(hideCard);
                printableCard.setFace(PrintableCardFace.JOCKER);
            }
        }
        printableBoard.sort();
    }

    private void apply(Move<KlondikePilesEnum> movement) {
        if (movement != null) {
            List<CardAction<KlondikePilesEnum>> actions = board.applyMovement(movement);
            historyIndex++;
            history = history.subList(0, historyIndex);
            history.add(new MoveHistory(movement, actions));
            afterMove();
        }
    }

    private void undo() {
        if (historyIndex >= 0) {
            List<CardAction<KlondikePilesEnum>> actions = history.get(historyIndex).actions();
            historyIndex--;
            board.revertMovement(actions);
            afterMove();
        }
    }

    private void redo() {
        if (historyIndex + 1 < history.size()) {
            historyIndex++;
            Move<KlondikePilesEnum> movement = history.get(historyIndex).move();
            board.applyMovement(movement);
            afterMove();
        }
    }

    private boolean applySingleMovementMatching(Predicate<Movement<KlondikePilesEnum>> filter) {
        List<Movement<KlondikePilesEnum>> matchedMovements = possibleMovements
                .stream()
                .filter(filter)
                .toList();
        if (matchedMovements.size() == 1) {
            Movement<KlondikePilesEnum> movement = matchedMovements.getFirst();
            apply(movement);
            return true;
        }
        return false;
    }

    private Set<CardEnum> getVisible() {
        Set<CardEnum> visible = new HashSet<>();
        for (Pile<KlondikePilesEnum> pile : board.getPileValues()) {
            pile.visible().forEach(visible::add);
        }
        return visible;
    }

    private Movement<KlondikePilesEnum> getMovement(double x, double y) {

        KlondikePilesEnum pilesEnum = printableBoard.getPileEnum(x, y);

        Movement<KlondikePilesEnum> movement = null;
        if (pilesEnum != null) {
            for (Movement<KlondikePilesEnum> possibleMovement : possibleMovements) {
                if (possibleMovement.getFrom() == draggedStack.from() &&
                        possibleMovement.getTo() == pilesEnum &&
                        possibleMovement.getCards().equals(draggedStack.cards())) {
                    movement = possibleMovement;
                }
            }
        }

        if (movement != null && movement.getFrom() == KlondikePilesEnum.STOCK && movement.getTo() == KlondikePilesEnum.STOCK) {
            // only on empty hidden stack if visible
            Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
            if (stock.hidden().isEmpty() && !printableBoard.onStockHidden(x, y)) {
                movement = null;
            }
        }
        return movement;
    }

}
