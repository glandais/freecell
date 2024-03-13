package io.github.glandais.solitaire.common.solver;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.move.MovementScore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecursiveSolitaireState<T extends PileType<T>> {

    final Board<T> board;
    int level;
    String tree;
    final List<MovementScore<T>> movements;

    public RecursiveSolitaireState(Board<T> board, int level, String tree, List<MovementScore<T>> movements) {
        this.board = board;
        this.level = level;
        this.tree = tree;
        this.movements = new ArrayList<>();
    }

    public RecursiveSolitaireState(io.github.glandais.solitaire.common.solver.RecursiveSolitaireState<T> parent) {
        this(
                parent.board.copy(),
                parent.level,
                parent.tree,
                new ArrayList<>(parent.movements)
        );
    }

}
