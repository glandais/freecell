package io.github.glandais.solitaire.klondike.board.piles;

import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.MovableStack;
import io.github.glandais.solitaire.klondike.board.Move;
import io.github.glandais.solitaire.klondike.board.Movement;
import io.github.glandais.solitaire.klondike.board.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.board.execution.ActionEnum;
import io.github.glandais.solitaire.klondike.board.execution.CardAction;
import io.github.glandais.solitaire.klondike.board.execution.TargetEnum;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;
import io.github.glandais.solitaire.klondike.cards.enums.OrderEnum;
import io.github.glandais.solitaire.klondike.cards.enums.SuiteEnum;

import java.util.List;
import java.util.Optional;

public class FoundationPile extends Pile {
    private final SuiteEnum suiteEnum;

    public FoundationPile(FoundationPilesEnum foundationPilesEnum) {
        super(foundationPilesEnum.getPilesEnum());
        this.suiteEnum = foundationPilesEnum.getSuiteEnum();
    }

    @Override
    public List<MovableStack> getMovableStacks() {
        if (!getVisible().isEmpty()) {
            // top card can be used
            return List.of(
                    new MovableStack(this.pilesEnum, List.of(getVisible().getLast()))
            );
        }
        return List.of();
    }

    @Override
    public Optional<Movement> accept(Board board, MovableStack movableStack) {
        // can't move stack to itself AND single incoming card
        if (movableStack.from() != this.pilesEnum && movableStack.cards().size() == 1) {
            CardEnum cardEnum = movableStack.cards().getFirst();
            // correct suite
            if (cardEnum.getSuiteEnum() == this.suiteEnum) {
                int acceptableOrder;
                if (getVisible().isEmpty()) {
                    acceptableOrder = OrderEnum.ACE.getOrder();
                } else {
                    acceptableOrder = getVisible().getLast().getOrderEnum().getOrder() + 1;
                }
                // correct order
                if (cardEnum.getOrderEnum().getOrder() == acceptableOrder) {
                    return Optional.of(new Movement(movableStack, this.pilesEnum));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CardAction> getActions(Move move) {
        if (move.from() == this.pilesEnum) {
            // remove top card
            CardEnum last = getVisible().getLast();
            return List.of(
                    new CardAction(this.pilesEnum, TargetEnum.VISIBLE_LAST, ActionEnum.REMOVE, last)
            );
        } else {
            // add to top
            CardEnum cardEnum = move.cards().getFirst();
            return List.of(
                    new CardAction(this.pilesEnum, TargetEnum.VISIBLE_LAST, ActionEnum.ADD, cardEnum)
            );
        }
    }

}
