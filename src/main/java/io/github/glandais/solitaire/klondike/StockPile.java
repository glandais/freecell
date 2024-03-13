package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Cards;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.board.PlayablePile;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.execution.ActionEnum;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.execution.TargetEnum;
import io.github.glandais.solitaire.common.move.MovableStack;
import io.github.glandais.solitaire.common.move.Move;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockPile implements PlayablePile<KlondikePilesEnum> {

    @Override
    public List<MovableStack<KlondikePilesEnum>> getMovableStacks(Board<KlondikePilesEnum> board, Pile<KlondikePilesEnum> pile) {
        List<MovableStack<KlondikePilesEnum>> result = new ArrayList<>();
        if (!pile.visible().isEmpty()) {
            result.add(
                    new MovableStack<>(KlondikePilesEnum.STOCK, Cards.of(pile.visible().getLast()))
            );
        }
        if (!pile.hidden().isEmpty()) {
            result.add(
                    new MovableStack<>(KlondikePilesEnum.STOCK, Cards.of(pile.hidden().getLast()))
            );
        }
        return result;
    }

    @Override
    public Movement<KlondikePilesEnum> accept(Board<KlondikePilesEnum> board, Pile<KlondikePilesEnum> pile, MovableStack<KlondikePilesEnum> movableStack) {
        if (movableStack.from() == KlondikePilesEnum.STOCK) {

            CardEnum cardEnum = movableStack.cards().getLast();
            boolean fromVisible = !pile.visible().isEmpty() && cardEnum == pile.visible().getLast();
            // visible cards can be discarded only if hidden is empty
            if (fromVisible && !pile.hidden().isEmpty()) {
                return null;
            }
            // can't discard single visible card
            if (fromVisible && pile.visible().size() == 1) {
                return null;
            }
            return new Movement<>(movableStack, KlondikePilesEnum.STOCK);
        }
        // no card can be moved to stock pile
        return null;
    }

    @Override
    public List<CardAction<KlondikePilesEnum>> getActions(Board<KlondikePilesEnum> board, Pile<KlondikePilesEnum> pile, Move<KlondikePilesEnum> move) {
        // a single card
        if (move.getFrom() == KlondikePilesEnum.STOCK) {
            List<CardAction<KlondikePilesEnum>> actions = new ArrayList<>();

            CardEnum cardEnum = move.getCards().getLast();

            boolean fromVisible = !pile.visible().isEmpty() && cardEnum == pile.visible().getLast();
            if (fromVisible) {
                if (move.getTo() == KlondikePilesEnum.STOCK) {
                    Cards visible = pile.visible();
                    for (int i = visible.size() - 1; i >= 0; i--) {
                        CardEnum card = visible.get(i);
                        actions.add(new CardAction<>(KlondikePilesEnum.STOCK, TargetEnum.VISIBLE_LAST, ActionEnum.REMOVE, card));
                        actions.add(new CardAction<>(KlondikePilesEnum.STOCK, TargetEnum.HIDDEN_LAST, ActionEnum.ADD, card));
                    }
                    CardEnum first = visible.getFirst();
                    actions.add(new CardAction<>(KlondikePilesEnum.STOCK, TargetEnum.HIDDEN_LAST, ActionEnum.REMOVE, first));
                    actions.add(new CardAction<>(KlondikePilesEnum.STOCK, TargetEnum.VISIBLE_LAST, ActionEnum.ADD, first));
                } else {
                    // remove top visible card
                    actions.add(new CardAction<>(KlondikePilesEnum.STOCK, TargetEnum.VISIBLE_LAST, ActionEnum.REMOVE, cardEnum));
                }
            } else {
                actions.add(new CardAction<>(KlondikePilesEnum.STOCK, TargetEnum.HIDDEN_LAST, ActionEnum.REMOVE, cardEnum));
                actions.add(new CardAction<>(KlondikePilesEnum.STOCK, TargetEnum.VISIBLE_LAST, ActionEnum.ADD, cardEnum));
            }

            return actions;
        } else {
            // never happens
            Logger.infoln("INVALID STATE");
        }
        return List.of();
    }

}
