package io.github.glandais.freecell.solver;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.execution.CardAction;
import lombok.Getter;

import java.util.List;

@Getter
public class Node {

    private final Node parent;
    private final int depth;

    public Node(BoardSolver solver, Board board, Node parent, int depthParam) {
        this.parent = parent;
        this.depth = depthParam;

        if (depth < solver.getBestDepth()) {
            if (board.isFinished()) {
                solver.setBestNode(this);
            } else {
                List<Movement> possibleMovements = board.getPossibleMovements();
                for (Movement movement : possibleMovements) {
                    List<CardAction> actions = board.applyMovement(movement);
                    if (!solver.hasState(this.depth)) {
                        new Node(solver, board, this, this.depth + 1);
                    }
                    board.revertMovement(actions);
                }
            }
        }
    }
}
