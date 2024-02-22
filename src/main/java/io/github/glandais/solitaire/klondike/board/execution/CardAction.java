package io.github.glandais.solitaire.klondike.board.execution;

import io.github.glandais.solitaire.klondike.board.enums.PilesEnum;
import io.github.glandais.solitaire.klondike.board.piles.Pile;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;

public record CardAction(PilesEnum pilesEnum, TargetEnum target, ActionEnum actionEnum, CardEnum card) {

    public void perform(Pile pile) {
        if (actionEnum == ActionEnum.ADD) {
            switch (target) {
                case HIDDEN_FIRST -> pile.getHidden().addFirst(card);
                case HIDDEN_LAST -> pile.getHidden().addLast(card);
                case VISIBLE_FIRST -> pile.getVisible().addFirst(card);
                case VISIBLE_LAST -> pile.getVisible().addLast(card);
            }
        } else {
            if (target == TargetEnum.HIDDEN_FIRST || target == TargetEnum.HIDDEN_LAST) {
                pile.getHidden().remove(card);
            } else {
                pile.getVisible().remove(card);
            }
        }
    }

    public void revert(Pile pile) {
        if (actionEnum == ActionEnum.ADD) {
            if (target == TargetEnum.HIDDEN_FIRST || target == TargetEnum.HIDDEN_LAST) {
                pile.getHidden().remove(card);
            } else {
                pile.getVisible().remove(card);
            }
        } else {
            switch (target) {
                case HIDDEN_FIRST -> pile.getHidden().addFirst(card);
                case HIDDEN_LAST -> pile.getHidden().addLast(card);
                case VISIBLE_FIRST -> pile.getVisible().addFirst(card);
                case VISIBLE_LAST -> pile.getVisible().addLast(card);
            }
        }
    }

}
