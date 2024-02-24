package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.cards.CardEnum;

import java.util.List;

public record MovementScore<T extends PileType<T>>(T from, T to, List<CardEnum> cards, int score) implements Move<T> {
    public MovementScore(Movement<T> movement, int score) {
        this(movement.from(), movement.to(), movement.cards(), score);
    }

    @Override
    public String toString() {
        return from + " → " + to + " (" + cards + ") - " + score;
    }

}
