package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.Cards;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.cards.CardEnum;

import java.util.List;

public interface Move<T extends PileType<T>> {
    T getFrom();

    T getTo();

    Cards getCards();
}
