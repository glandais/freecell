package io.github.glandais.solitaire.common.board;

public record Pile<T extends PileType<T>>(Cards hidden, Cards visible, T pileType) {
    public Pile(T pileType) {
        this(new Cards(), new Cards(), pileType);
    }

    @Override
    public String toString() {
        return pileType.name() + " [" + hidden() + "/" + visible() + "]";
    }

    public Pile<T> copy() {
        return new Pile<>(hidden.copy(), visible.copy(), pileType);
    }

}
