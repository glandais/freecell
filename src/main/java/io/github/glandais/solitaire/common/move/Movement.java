package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.cards.CardEnum;

import java.util.List;

public record Movement<T extends PileType<T>>(T from, T to, List<CardEnum> cards) implements Move<T> {

    public Movement(MovableStack<T> movableStack, T to) {
        this(movableStack.from(), to, movableStack.cards());
    }

    @Override
    public String toString() {
        return from + " → " + to + " (" + cards + ")";
    }

}
