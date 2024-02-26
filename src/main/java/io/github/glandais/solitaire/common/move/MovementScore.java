package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.cards.CardEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementScore<T extends PileType<T>> implements Move<T> {

    T from;
    T to;
    List<CardEnum> cards;
    int score;

    public MovementScore(Movement<T> movement, int score) {
        this(movement.getFrom(), movement.getTo(), movement.getCards(), score);
    }

    @Override
    public String toString() {
        return from + " → " + to + " (" + cards + ") - " + score;
    }

}
