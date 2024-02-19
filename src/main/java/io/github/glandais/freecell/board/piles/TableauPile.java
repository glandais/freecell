package io.github.glandais.freecell.board.piles;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.MovableStack;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.enums.TableauPilesEnum;
import io.github.glandais.freecell.board.execution.ActionEnum;
import io.github.glandais.freecell.board.execution.CardAction;
import io.github.glandais.freecell.board.execution.TargetEnum;
import io.github.glandais.freecell.cards.enums.CardColorEnum;
import io.github.glandais.freecell.cards.enums.CardEnum;
import io.github.glandais.freecell.cards.enums.CardOrderEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class TableauPile extends Pile {

    private final boolean moveIncompleteStacks;

    public TableauPile(TableauPilesEnum tableauPilesEnum, boolean moveIncompleteStacks) {
        super(tableauPilesEnum.getPilesEnum());
        this.moveIncompleteStacks = moveIncompleteStacks;
    }

    public List<MovableStack> getMovableStacks() {
        if (moveIncompleteStacks) {
            return getAllStacks();
        } else {
            return getStacksSimple();
        }
    }

    private List<MovableStack> getAllStacks() {
        return IntStream.range(0, getVisible().size())
                .mapToObj(c -> {
                    List<CardEnum> cards = List.copyOf(getVisible().subList(c, getVisible().size()));
                    int score;
                    if (c == 0) {
                        score = getAllStackScore();
                    } else {
                        score = 300;
                    }
                    return new MovableStack(this.pilesEnum, cards, score);
                })
                .toList();
    }

    private int getAllStackScore() {
        int score;
        if (getHidden().isEmpty()) {
            score = -99;
        } else {
            score = -getHidden().size();
        }
        return score;
    }

    private List<MovableStack> getStacksSimple() {
        List<MovableStack> movableStacks = new ArrayList<>(2);
        // all stack
        if (!getVisible().isEmpty()) {
            List<CardEnum> cards = List.copyOf(getVisible());
            movableStacks.add(new MovableStack(this.pilesEnum, cards, getAllStackScore()));
        }
        if (getVisible().size() > 1) {
            List<CardEnum> cards = List.of(getVisible().getLast());
            // single card
            movableStacks.add(new MovableStack(this.pilesEnum, cards, 300));
        }
        return movableStacks;
    }

    @Override
    public Optional<Movement> accept(Board board, MovableStack movableStack) {
        // can't move stack to itself
        if (movableStack.from() == this.pilesEnum) {
            return Optional.empty();
        }
        // no visible -> empty tableau
        CardEnum first = movableStack.cards().getFirst();
        if (getVisible().isEmpty()) {
            // only for kings
            if (first.getCardOrderEnum() == CardOrderEnum.KING) {
                if (movableStack.cards().size() == board.getPile(movableStack.from()).getVisible().size() &&
                        board.getPile(movableStack.from()).getHidden().isEmpty()
                ) {
                    // no need to move if king is already at the top
                    return Optional.empty();
                } else {
                    return Optional.of(new Movement(movableStack, this.pilesEnum, -100));
                }
            } else {
                return Optional.empty();
            }
        }
        CardEnum last = getVisible().getLast();
        CardColorEnum lastColor = last.getCardColorEnum();
        CardColorEnum firstColor = first.getCardColorEnum();
        if (lastColor == firstColor) {
            return Optional.empty();
        }
        int lastOrder = last.getCardOrderEnum().getOrder();
        int firstOrder = first.getCardOrderEnum().getOrder();
        if (firstOrder + 1 == lastOrder) {
            return Optional.of(new Movement(movableStack, this.pilesEnum, movableStack.score()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<CardAction> getActions(Movement movement) {
        MovableStack movableStack = movement.movableStack();
        List<CardEnum> cards = movableStack.cards();
        List<CardAction> actions = new ArrayList<>(cards.size() + 1);
        if (movableStack.from() == this.pilesEnum) {
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
