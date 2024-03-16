package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.Cards;
import io.github.glandais.solitaire.common.board.PileType;

public interface Move<T extends PileType<T>> {
    T getFrom();

    T getTo();

    Cards getCards();
}
