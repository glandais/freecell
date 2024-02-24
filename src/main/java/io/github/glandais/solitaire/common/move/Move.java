package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.cards.CardEnum;

import java.util.List;

public interface Move<T extends PileType<T>> {
    T from();

    T to();

    List<CardEnum> cards();
}
