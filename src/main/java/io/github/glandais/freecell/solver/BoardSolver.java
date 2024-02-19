package io.github.glandais.freecell.solver;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.State;
import io.github.glandais.freecell.board.execution.CardAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class BoardSolver {

    private final Board board;
    private Map<State, Integer> states = new HashMap<>();

    int level = 0;
    int bestLevel = 100_000;
    List<Movement> bestMovements = null;

    // current path in tree
    List<Movement> movementsStack = new ArrayList<>();
    // actions performed in tree
    List<List<CardAction>> actionsStack = new ArrayList<>();

    // branches to explore in tree
    Map<Integer, List<Movement>> movementsToExplore = new HashMap<>();

    long movements = 0;
    Map<Integer, AtomicLong> movementsPerLevel = new HashMap<>();

    public BoardSolver(Board board) {
        this.board = board;
    }

    public List<Movement> solve() {
        // do while root has not been completely explored
        do {
            // we are trying to explore a suboptimal level
            if (level >= bestLevel) {
                // rollback, move level up
                rollback();
            }
            // current unexplored movements at level
            List<Movement> levelMovements = movementsToExplore.get(level);
            // no movements set for current level
            if (levelMovements == null) {
                // get possible movements for current level
                levelMovements = board.getPossibleMovements();
                // track possible movements
                movementsToExplore.put(level, levelMovements);
            }
            // we have not explored all movements for this level
            if (!levelMovements.isEmpty()) {
                // retrieve possible movement
                Movement movement = levelMovements.removeFirst();
                // apply movement
                List<CardAction> actions = board.applyMovement(movement);
                if (hasState()) {
                    // already met state, rollback actions
                    board.revertMovement(actions);
                } else {
                    incMovements();
                    // go deeper :
                    // add movement to stack
                    movementsStack.add(movement);
                    // add actions to stack
                    actionsStack.add(actions);
                    // deeper level
                    level = level + 1;

                    // board finished and better ?
                    if (board.isFinished() && level < bestLevel) {
                        System.out.println("New best level : " + level + " at iteration " + movements);
                        // track best
                        bestLevel = level;
                        bestMovements = new ArrayList<>(movementsStack);
                    }
                }
            } else {
                // rollback, move level up
                rollback();
            }
        } while (movementsToExplore.get(0) != null && movements < 100_000);

        this.board.init();
        return bestMovements;
    }

    private void incMovements() {
        movements++;
        movementsPerLevel.computeIfAbsent(level, i -> new AtomicLong(0)).incrementAndGet();
        if (movements % 100_000 == 0) {
            System.out.println("movements : " + movements);
            System.out.println("movementsPerLevel : " + movementsPerLevel);
            List<Tuple> tuples = movementsToExplore.entrySet().stream()
                    .map(e -> new Tuple(e.getKey(), e.getValue().size()))
                    .toList();
            System.out.println("movementsToExplore count : " + tuples);
            System.out.println("movementsToExplore : " + movementsToExplore);
            System.out.println("movementsStack : " + movementsStack);
            System.out.println("Best level : " + bestLevel + " at iteration " + bestMovements);
        }
    }

    private void rollback() {
        // discard level
        movementsToExplore.remove(level);
        // discard movement
        movementsStack.removeLast();
        // discard actions
        List<CardAction> actions = actionsStack.removeLast();
        // revert actions
        board.revertMovement(actions);
        // decrease level
        level = level - 1;
    }

    public boolean hasState() {
        State state = board.getState();
        Integer existingLevel = states.get(state);
        if (existingLevel == null || level < existingLevel) {
            states.put(state, level);
            return false;
        }
        if (states.size() % 100_000 == 0) {
            System.out.println("States : " + states.size());
        }
        return true;
    }

}
