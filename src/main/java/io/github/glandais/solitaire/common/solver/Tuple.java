package io.github.glandais.solitaire.common.solver;

public record Tuple(int level, int size) {

    @Override
    public String toString() {
        return level + "=" + size;
    }
}
