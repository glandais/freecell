package io.github.glandais.solitaire.common.board;

public interface PileType<T extends PileType<T>> {
    String name();
    int ordinal();

    PlayablePile<T> playablePile();

    boolean isSwappable();
}
