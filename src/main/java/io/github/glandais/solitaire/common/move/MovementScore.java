package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.Cards;
import io.github.glandais.solitaire.common.board.PileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementScore<T extends PileType<T>> implements Move<T> {

    T from;
    T to;
    Cards cards;
    int score;
    Object debug;

    public MovementScore(Movement<T> movement, int score, Object debug) {
        this(movement.getFrom(), movement.getTo(), movement.getCards(), score, debug);
    }

    @Override
    public String toString() {
        return from + " → " + to + " (" + cards + ") - " + score + " " + debug;
    }

}
