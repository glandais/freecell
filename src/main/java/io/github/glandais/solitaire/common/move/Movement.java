package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.Cards;
import io.github.glandais.solitaire.common.board.PileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movement<T extends PileType<T>> implements Move<T> {

    T from;
    T to;
    Cards cards;

    public Movement(MovableStack<T> movableStack, T to) {
        this(movableStack.from(), to, movableStack.cards());
    }

    @Override
    public String toString() {
        return from + " → " + to + " (" + cards + ")";
    }

}
