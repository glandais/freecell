package io.github.glandais.freecell.board.piles;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.MovableStack;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.enums.PilesEnum;
import io.github.glandais.freecell.board.execution.ActionEnum;
import io.github.glandais.freecell.board.execution.CardAction;
import io.github.glandais.freecell.board.execution.TargetEnum;
import io.github.glandais.freecell.cards.enums.CardEnum;

import java.util.List;
import java.util.Optional;

public class StockPile extends Pile {
    public StockPile() {
        super(PilesEnum.STOCK);
    }

    public List<MovableStack> getMovableStacks() {
        if (!getVisible().isEmpty()) {
            return List.of(
                    new MovableStack(this.pilesEnum, List.of(getVisible().getLast()))
            );
        } else {
            return List.of();
        }
    }

    @Override
    public Optional<Movement> accept(Board board, MovableStack movableStack) {
        if (movableStack.from() == this.pilesEnum) {
            // card can be discarded
            return Optional.of(new Movement(movableStack, this.pilesEnum, 100));
        }
        // no card can be moved to stock pile
        return Optional.empty();
    }

    @Override
    public List<CardAction> getActions(Movement movement) {
        MovableStack movableStack = movement.movableStack();
        // a single card
        if (movableStack.from() == this.pilesEnum) {
            CardEnum cardEnum = getVisible().getLast();
            if (movement.to() == this.pilesEnum) {
                // discard card
                return List.of(
                        new CardAction(this.pilesEnum, TargetEnum.VISIBLE_LAST, ActionEnum.REMOVE, cardEnum),
                        new CardAction(this.pilesEnum, TargetEnum.VISIBLE_FIRST, ActionEnum.ADD, cardEnum)
                );
            } else {
                // remove top visible card
                return List.of(
                        new CardAction(this.pilesEnum, TargetEnum.VISIBLE_LAST, ActionEnum.REMOVE, cardEnum)
                );
            }
        }
        return List.of();
    }

}
