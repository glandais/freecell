package io.github.glandais.solitaire.klondike.solver;

public record Tuple(int level, int size) {

    @Override
    public String toString() {
        return level + "=" + size;
    }
}
