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
public class Movement<T extends PileType<T>> implements Move<T> {

    T from;
    T to;
    List<CardEnum> cards;

    public Movement(MovableStack<T> movableStack, T to) {
        this(movableStack.from(), to, movableStack.cards());
    }

    @Override
    public String toString() {
        return from + " → " + to + " (" + cards + ")";
    }

}
