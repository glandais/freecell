package io.github.glandais.solitaire.common.solver;

public class Exploring {

    int i = 0;

    int count;

    public Exploring(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return i + "/" + count;
    }
}
