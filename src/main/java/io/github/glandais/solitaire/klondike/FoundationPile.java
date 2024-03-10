package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.board.PlayablePile;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.cards.OrderEnum;
import io.github.glandais.solitaire.common.cards.SuiteEnum;
import io.github.glandais.solitaire.common.execution.ActionEnum;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.execution.TargetEnum;
import io.github.glandais.solitaire.common.move.MovableStack;
import io.github.glandais.solitaire.common.move.Move;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;

import java.util.List;
import java.util.Optional;

public class FoundationPile implements PlayablePile<KlondikePilesEnum> {

    final SuiteEnum suiteEnum;

    public FoundationPile(SuiteEnum suiteEnum) {
        this.suiteEnum = suiteEnum;
    }

    @Override
    public List<MovableStack<KlondikePilesEnum>> getMovableStacks(Board<KlondikePilesEnum> board, Pile<KlondikePilesEnum> pile) {
        return List.of();
    }

    @Override
    public Optional<Movement<KlondikePilesEnum>> accept(Board<KlondikePilesEnum> board, Pile<KlondikePilesEnum> pile, MovableStack<KlondikePilesEnum> movableStack) {
        // single incoming card
        if (movableStack.cards().size() == 1) {
            CardEnum cardEnum = movableStack.cards().getFirst();
            if (movableStack.from() == KlondikePilesEnum.STOCK &&
                    !board.getPile(movableStack.from()).hidden().isEmpty() &&
                    board.getPile(movableStack.from()).hidden().getLast() == cardEnum) {
                return Optional.empty();
            }
            // correct suite
            if (cardEnum.getSuiteEnum() == this.suiteEnum) {
                int acceptableOrder;
                if (pile.visible().isEmpty()) {
                    acceptableOrder = OrderEnum.ACE.getOrder();
                } else {
                    acceptableOrder = pile.visible().getLast().getOrderEnum().getOrder() + 1;
                }
                // correct order
                if (cardEnum.getOrderEnum().getOrder() == acceptableOrder) {
                    return Optional.of(new Movement<>(movableStack, pile.pileType()));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CardAction<KlondikePilesEnum>> getActions(Board<KlondikePilesEnum> board, Pile<KlondikePilesEnum> pile, Move<KlondikePilesEnum> move) {
        // add to top
        CardEnum cardEnum = move.getCards().getFirst();
        return List.of(
                new CardAction<>(pile.pileType(), TargetEnum.VISIBLE_LAST, ActionEnum.ADD, cardEnum)
        );
    }

}
