package io.github.glandais.solitaire.klondike.board;

import io.github.glandais.solitaire.klondike.board.enums.PilesEnum;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;

import java.util.List;

public record Movement(PilesEnum from, PilesEnum to, List<CardEnum> cards) implements Move {

    public Movement(MovableStack movableStack, PilesEnum to) {
        this(movableStack.from(), to, movableStack.cards());
    }

    @Override
    public String toString() {
        return from + " → " + to + " (" + cards + ")";
    }

}
