package io.github.glandais.solitaire.common.board;

import io.github.glandais.solitaire.common.cards.CardEnum;

import java.util.ArrayList;
import java.util.List;

public record Pile<T extends PileType<T>>(List<CardEnum> hidden, List<CardEnum> visible, T pileType) {
    public Pile(T pileType) {
        this(new ArrayList<>(), new ArrayList<>(), pileType);
    }

    @Override
    public String toString() {
        return pileType.name() + " [" + hidden() + "/" + visible() + "]";
    }

    public Pile<T> copy() {
        return new Pile<>(new ArrayList<>(hidden), new ArrayList<>(visible), pileType);
    }

}
