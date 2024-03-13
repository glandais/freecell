package io.github.glandais.solitaire.common.execution;

import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.cards.CardEnum;

public record CardAction<T extends PileType<T>>(T pileType, TargetEnum target, ActionEnum actionEnum, CardEnum card) {

    public void perform(Pile<T> pile) {
        if (actionEnum == ActionEnum.ADD) {
            switch (target) {
                case HIDDEN_FIRST -> pile.hidden().addFirst(card);
                case HIDDEN_LAST -> pile.hidden().add(card);
                case VISIBLE_FIRST -> pile.visible().addFirst(card);
                case VISIBLE_LAST -> pile.visible().add(card);
            }
        } else {
            if (target == TargetEnum.HIDDEN_FIRST || target == TargetEnum.HIDDEN_LAST) {
                pile.hidden().remove(card);
            } else {
                pile.visible().remove(card);
            }
        }
    }

    public void revert(Pile<T> pile) {
        if (actionEnum == ActionEnum.ADD) {
            if (target == TargetEnum.HIDDEN_FIRST || target == TargetEnum.HIDDEN_LAST) {
                pile.hidden().remove(card);
            } else {
                pile.visible().remove(card);
            }
        } else {
            switch (target) {
                case HIDDEN_FIRST -> pile.hidden().addFirst(card);
                case HIDDEN_LAST -> pile.hidden().add(card);
                case VISIBLE_FIRST -> pile.visible().addFirst(card);
                case VISIBLE_LAST -> pile.visible().add(card);
            }
        }
    }

}
