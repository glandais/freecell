package io.github.glandais.freecell.solver;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.BoardPrinter;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.execution.CardAction;
import io.github.glandais.freecell.cards.enums.CardEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardSolver {

    final Map<List<List<CardEnum>>, Integer> states = new HashMap<>();

    final Board board;
    private final Board originalBoard;

    private Node bestNode = null;

    public BoardSolver(Board board) {
        this.board = board;
        this.originalBoard = new Board(board);
    }

    public void solve() {

//        new Node(this, board, null, 0);

        int depth = 0;
        int bestDepth = 100_000;
        List<Movement> bestMovements = null;

        List<Movement> movementsStack = new ArrayList<>();
        List<List<CardAction>> actionsStack = new ArrayList<>();

        Map<Integer, List<Movement>> movementsToExplore = new HashMap<>();

        do {
            // we are trying to explore a suboptimal level
            if (depth >= bestDepth) {
                movementsStack.removeLast();
                board.revertMovement(actionsStack.removeLast());
                depth = depth - 1;
            }
            // current unexplored movements at level
            List<Movement> levelMovements = movementsToExplore.get(depth);
            // no movements set for current level
            if (levelMovements == null) {
                // get possible movements for current level
                levelMovements = board.getPossibleMovements();
                // track possible movements
                movementsToExplore.put(depth, levelMovements);
            }
            // we have not explored all movements for this level
            if (!levelMovements.isEmpty()) {
                Movement movement = levelMovements.removeFirst();
                List<CardAction> actions = board.applyMovement(movement);
                if (hasState(depth)) {
                    board.revertMovement(actions);
                } else {
                    movementsStack.add(movement);
                    actionsStack.add(actions);
                    depth = depth + 1;

                    if (board.isFinished() && depth < bestDepth) {
                        bestDepth = depth;
                        bestMovements = new ArrayList<>(movementsStack);
                    }
                }
            } else {
                movementsStack.removeLast();
                board.revertMovement(actionsStack.removeLast());
                depth = depth - 1;
            }
        } while (!movementsToExplore.get(0).isEmpty());

        if (bestMovements != null) {
            Board replayBoard = new Board(originalBoard);
            BoardPrinter.print(replayBoard);
            for (Movement movement : bestMovements) {
                replayBoard.applyMovement(movement);
                BoardPrinter.print(replayBoard);
                System.out.println(movement);
            }
        }
    }

    public boolean hasState(int depth) {
        List<List<CardEnum>> state = board.getState();
        Integer existingDepth = states.get(state);
        if (existingDepth == null || depth < existingDepth) {
            states.put(state, depth);
            return false;
        }
        if (states.size() % 100_000 == 0) {
            System.out.println("States : " + states.size());
        }
        return true;
    }

    public int getBestDepth() {
        if (this.bestNode == null) {
            return 100_000;
        }
        return this.bestNode.getDepth();
    }

    public void setBestNode(Node node) {
        this.bestNode = node;
        System.out.println("Best depth : " + this.bestNode.getDepth());
        BoardPrinter.print(board);
    }
}
