package io.github.glandais.freecell.board.piles;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.MovableStack;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.enums.SuitePilesEnum;
import io.github.glandais.freecell.board.execution.ActionEnum;
import io.github.glandais.freecell.board.execution.CardAction;
import io.github.glandais.freecell.board.execution.TargetEnum;
import io.github.glandais.freecell.cards.enums.CardEnum;
import io.github.glandais.freecell.cards.enums.CardOrderEnum;
import io.github.glandais.freecell.cards.enums.CardSuiteEnum;

import java.util.List;
import java.util.Optional;

public class FoundationPile extends Pile {
    private final CardSuiteEnum cardSuiteEnum;

    public FoundationPile(SuitePilesEnum suitePilesEnum) {
        super(suitePilesEnum.getPilesEnum());
        this.cardSuiteEnum = suitePilesEnum.getCardSuiteEnum();
    }

    @Override
    public List<MovableStack> getMovableStacks() {
        if (!getVisible().isEmpty()) {
            // top card can be used
            return List.of(
                    new MovableStack(this.pilesEnum, List.of(getVisible().getLast()), 150)
            );
        } else {
            return List.of();
        }
    }

    @Override
    public Optional<Movement> accept(Board board, MovableStack movableStack) {
        // can't move stack to itself AND single incoming card
        if (movableStack.from() != this.pilesEnum && movableStack.cards().size() == 1) {
            CardEnum cardEnum = movableStack.cards().getFirst();
            // correct suite
            if (cardEnum.getCardSuiteEnum() == this.cardSuiteEnum) {
                int acceptableOrder;
                if (getVisible().isEmpty()) {
                    acceptableOrder = CardOrderEnum.ACE.getOrder();
                } else {
                    acceptableOrder = getVisible().getLast().getCardOrderEnum().getOrder() + 1;
                }
                // correct order
                if (cardEnum.getCardOrderEnum().getOrder() == acceptableOrder) {
                    return Optional.of(new Movement(movableStack, this.pilesEnum, -50));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CardAction> getActions(Movement movement) {
        MovableStack movableStack = movement.movableStack();
        if (movableStack.from() == this.pilesEnum) {
            // remove top card
            CardEnum last = getVisible().getLast();
            return List.of(
                    new CardAction(this.pilesEnum, TargetEnum.VISIBLE_LAST, ActionEnum.REMOVE, last)
            );
        } else {
            // add to top
            CardEnum cardEnum = movableStack.cards().getFirst();
            return List.of(
                    new CardAction(this.pilesEnum, TargetEnum.VISIBLE_LAST, ActionEnum.ADD, cardEnum)
            );
        }
    }

}
