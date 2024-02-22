package io.github.glandais.solitaire.klondike.board.piles;

import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.MovableStack;
import io.github.glandais.solitaire.klondike.board.Move;
import io.github.glandais.solitaire.klondike.board.Movement;
import io.github.glandais.solitaire.klondike.board.enums.TableauPilesEnum;
import io.github.glandais.solitaire.klondike.board.execution.ActionEnum;
import io.github.glandais.solitaire.klondike.board.execution.CardAction;
import io.github.glandais.solitaire.klondike.board.execution.TargetEnum;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;
import io.github.glandais.solitaire.klondike.cards.enums.ColorEnum;
import io.github.glandais.solitaire.klondike.cards.enums.OrderEnum;
import io.github.glandais.solitaire.klondike.board.enums.PileTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class TableauPile extends Pile {

    public TableauPile(TableauPilesEnum tableauPilesEnum) {
        super(tableauPilesEnum.getPilesEnum());
    }

    public List<MovableStack> getMovableStacks() {
//        if (moveIncompleteStacks) {
//            return getAllStacks();
//        } else {
        return getStacksSimple();
//        }
    }

    private List<MovableStack> getAllStacks() {
        return IntStream.range(0, getVisible().size())
                .mapToObj(c -> {
                    List<CardEnum> cards = List.copyOf(getVisible().subList(c, getVisible().size()));
                    return new MovableStack(this.pilesEnum, cards);
                })
                .toList();
    }

    private List<MovableStack> getStacksSimple() {
        List<MovableStack> movableStacks = new ArrayList<>(2);
        // all stack
        if (!getVisible().isEmpty()) {
            List<CardEnum> cards = List.copyOf(getVisible());
            movableStacks.add(new MovableStack(this.pilesEnum, cards));
        }
        if (getVisible().size() > 1) {
            List<CardEnum> cards = List.of(getVisible().getLast());
            // single card
            movableStacks.add(new MovableStack(this.pilesEnum, cards));
        }
        return movableStacks;
    }

    @Override
    public Optional<Movement> accept(Board board, MovableStack movableStack) {
        // can't move stack to itself
        if (movableStack.from() == this.pilesEnum) {
            return Optional.empty();
        }
        if (!isPossible(movableStack)) {
            return Optional.empty();
        }
        return switch (movableStack.from().getPileTypeEnum()) {
            case PileTypeEnum.STOCK, PileTypeEnum.FOUNDATION -> Optional.of(new Movement(movableStack, this.pilesEnum));
            case PileTypeEnum.TABLEAU -> acceptFromTableau(board, movableStack);
        };
    }

    private Optional<Movement> acceptFromTableau(Board board, MovableStack movableStack) {
        // accept only full stacks
        if (movableStack.cards().size() != board.getPile(movableStack.from()).getVisible().size()) {
            return Optional.empty();
        }
        // do not move a king starting stack without hidden cards
        if (
                movableStack.cards().getFirst().getOrderEnum() == OrderEnum.KING &&
                        board.getPile(movableStack.from()).getHidden().isEmpty()
        ) {
            return Optional.empty();
        } else {
            return Optional.of(new Movement(movableStack, this.pilesEnum));
        }
    }

    private boolean isPossible(MovableStack movableStack) {
        CardEnum first = movableStack.cards().getFirst();
        if (getVisible().isEmpty()) {
            return first.getOrderEnum() == OrderEnum.KING;
        }
        CardEnum last = getVisible().getLast();
        ColorEnum lastColor = last.getColorEnum();
        ColorEnum firstColor = first.getColorEnum();
        if (lastColor == firstColor) {
            return false;
        } else {
            int lastOrder = last.getOrderEnum().getOrder();
            int firstOrder = first.getOrderEnum().getOrder();
            return firstOrder + 1 == lastOrder;
        }
    }

    @Override
    public List<CardAction> getActions(Move move) {
        List<CardEnum> cards = move.cards();
        List<CardAction> actions = new ArrayList<>(cards.size() + 1);
        if (move.from() == this.pilesEnum) {
            // remove cards
            for (CardEnum cardEnum : cards.reversed()) {
                actions.add(new CardAction(this.pilesEnum, TargetEnum.VISIBLE_LAST, ActionEnum.REMOVE, cardEnum));
            }
            // no more visible card, show last hidden if present
            if (cards.size() == getVisible().size() && !getHidden().isEmpty()) {
                CardEnum last = getHidden().getLast();
                actions.add(new CardAction(this.pilesEnum, TargetEnum.HIDDEN_LAST, ActionEnum.REMOVE, last));
                actions.add(new CardAction(this.pilesEnum, TargetEnum.VISIBLE_LAST, ActionEnum.ADD, last));
            }
        } else {
            // add cards
            for (CardEnum cardEnum : cards) {
                actions.add(new CardAction(this.pilesEnum, TargetEnum.VISIBLE_LAST, ActionEnum.ADD, cardEnum));
            }
        }
        return actions;
    }

}
