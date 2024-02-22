package io.github.glandais.freecell.board;

import io.github.glandais.freecell.board.enums.PilesEnum;

public record Movement(MovableStack movableStack, PilesEnum to) {

    @Override
    public String toString() {
        return movableStack.from() + " → " + to + " (" + movableStack.cards() + ")";
    }

}
