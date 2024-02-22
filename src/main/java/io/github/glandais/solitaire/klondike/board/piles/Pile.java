package io.github.glandais.solitaire.klondike.board.piles;

import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.MovableStack;
import io.github.glandais.solitaire.klondike.board.Move;
import io.github.glandais.solitaire.klondike.board.Movement;
import io.github.glandais.solitaire.klondike.board.enums.PilesEnum;
import io.github.glandais.solitaire.klondike.board.execution.CardAction;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class Pile {
    protected final List<CardEnum> hidden = new ArrayList<>();
    protected final List<CardEnum> visible = new ArrayList<>();

    protected final PilesEnum pilesEnum;
    private final int ordinal;

    protected Pile(PilesEnum pilesEnum) {
        this.pilesEnum = pilesEnum;
        this.ordinal = pilesEnum.ordinal();
    }

    public abstract List<MovableStack> getMovableStacks();

    public abstract Optional<Movement> accept(Board board, MovableStack movableStack);

    public abstract List<CardAction> getActions(Move movement);

    @Override
    public String toString() {
        return pilesEnum.name();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pile otherPile) {
            return ordinal == otherPile.ordinal;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ordinal;
    }
}
