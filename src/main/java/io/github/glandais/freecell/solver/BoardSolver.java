package io.github.glandais.freecell.solver;

import io.github.glandais.freecell.Logger;
import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.Movement;
import io.github.glandais.freecell.board.MovementScore;
import io.github.glandais.freecell.board.State;
import io.github.glandais.freecell.board.execution.CardAction;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class BoardSolver {

    private final Board board;
    private final Map<State, Integer> states = new HashMap<>();

    int level = 0;
    int bestLevel = 100_000;
    List<MovementScore> bestMovements = null;

    // current path in tree
    List<MovementScore> movementsStack = new ArrayList<>();
    // actions performed in tree
    List<List<CardAction>> actionsStack = new ArrayList<>();

    // branches to explore in tree
    Map<Integer, List<MovementScore>> movementsToExplore = new HashMap<>();

    long loops = 0;
    long movements = 0;
    long rollbacks = 0;
    long newStates = 0;
    long existingStates = 0;
    Map<Integer, AtomicLong> movementsPerLevel = new HashMap<>();

    public BoardSolver(Board board) {
        this.board = board;
    }

    public List<MovementScore> solve() {
        // do while root has not been completely explored
        do {
            // we are trying to explore a suboptimal level
            if (level >= bestLevel) {
                // rollback, move level up
                rollback();
            }
            // current unexplored movements at level
            List<MovementScore> levelMovements = movementsToExplore.get(level);
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
                MovementScore movement = levelMovements.removeFirst();
                // apply movement
                List<CardAction> actions = board.applyMovement(movement.movement());
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
                        newBestLevel();
                    }
                }
            } else {
                // rollback, move level up
                rollback();
            }
            loops++;
            if (loops % 100_000 == 0) {
                printStatus();
            }
//            sleep(500);
        } while (movementsToExplore.get(0) != null && loops < 10_000_000);
//        } while (movementsToExplore.get(0) != null && loops < 1_000_000 && bestMovements == null);

        printStatus();

        this.board.init();
        return bestMovements;
    }

    private void newBestLevel() {
        Logger.infoln("New best level : " + level + " at iteration " + movements);
        // track best
        bestLevel = level;
        bestMovements = new ArrayList<>(movementsStack);
        states.values().removeIf(l -> l > bestLevel);
    }

    @SneakyThrows
    private void sleep(int ms) {
        Thread.sleep(ms);
    }

    private void incMovements() {
        movements++;
        movementsPerLevel.computeIfAbsent(level, i -> new AtomicLong(0)).incrementAndGet();
    }

    private void printStatus() {
        Logger.infoln("**************************************************");
        Logger.infoln("loops : " + loops);
        Logger.infoln("movements : " + movements);
        Logger.infoln("rollbacks : " + rollbacks);
        Logger.infoln("newStates : " + newStates);
        Logger.infoln("existingStates : " + existingStates);
        Logger.infoln("states : " + states.size());
        Logger.infoln("movementsPerLevel : " + movementsPerLevel);
        List<Tuple> tuples = movementsToExplore.entrySet().stream()
                .map(e -> new Tuple(e.getKey(), e.getValue().size()))
                .toList();
        Logger.infoln("movementsToExplore count : " + tuples);
        Logger.infoln("movementsToExplore : " + movementsToExplore);
        Logger.infoln("movementsStack : " + movementsStack);
        Logger.infoln("level : " + level);
        Logger.infoln("Best level : " + bestLevel + " : " + bestMovements);
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
        rollbacks++;
    }

    public boolean hasState() {
        State state = board.getState();
        Integer existingLevel = states.get(state);
        if (existingLevel != null && existingLevel < 10) {
            Logger.infoln(level);
        }
        if (existingLevel == null || level < existingLevel) {
            newStates++;
            states.put(state, level);
            return false;
        }
        existingStates++;
        return true;
    }

}
