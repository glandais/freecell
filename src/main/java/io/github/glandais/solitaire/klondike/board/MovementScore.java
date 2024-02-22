package io.github.glandais.solitaire.klondike.board;

import io.github.glandais.solitaire.klondike.board.enums.PilesEnum;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;

import java.util.List;

public record MovementScore(PilesEnum from, PilesEnum to, List<CardEnum> cards, int score) implements Move {
    public MovementScore(Movement movement, int score) {
        this(movement.from(), movement.to(), movement.cards(), score);
    }

    @Override
    public String toString() {
        return from + " → " + to + " (" + cards + ") - " + score;
    }

}
