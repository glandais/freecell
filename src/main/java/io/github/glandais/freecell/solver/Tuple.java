package io.github.glandais.freecell.solver;

public record Tuple(int level, int size) {

    @Override
    public String toString() {
        return level + "=" + size;
    }
}
