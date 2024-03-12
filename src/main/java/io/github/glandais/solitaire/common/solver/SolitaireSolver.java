package io.github.glandais.solitaire.common.solver;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.board.Solitaire;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.printer.SolitairePrinter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SolitaireSolver<T extends PileType<T>> {

    private final Solitaire<T> solitaire;
    private final SolitairePrinter<T> debugPrinter;
    private final Board<T> board;
    private final States<T> states;

    int level = 0;
    int bestLevel = 100_000;
    List<MovementScore<T>> bestMovements = null;

    // current path in tree
    List<MovementScore<T>> movementsStack = new ArrayList<>();
    // actions performed in tree
    List<List<CardAction<T>>> actionsStack = new ArrayList<>();

    // branches to explore in tree
    Map<Integer, List<MovementScore<T>>> movementsToExplore = new HashMap<>();

    long loops = 0;
    long movements = 0;
    long rollbacks = 0;
    Map<Integer, AtomicLong> movementsPerLevel = new HashMap<>();

    public SolitaireSolver(Solitaire<T> solitaire, Board<T> board, SolitairePrinter<T> debugPrinter) {
        this.solitaire = solitaire;
        this.board = board;
        this.debugPrinter = debugPrinter;
        this.states = new States<>();
    }

    public List<MovementScore<T>> solve() {
        // do while root has not been completely explored
        do {
            // we are trying to explore a suboptimal level
            if (level >= bestLevel) {
                // rollback, move level up
                rollback();
            }
            // current unexplored moves at level
            List<MovementScore<T>> levelMovements = movementsToExplore.get(level);
            // no moves set for current level
            if (levelMovements == null) {
                if (solitaire.movesToFinish(board) != 10000) {
                    levelMovements = new ArrayList<>();
                } else {
                    // get possible moves for current level
                    levelMovements = solitaire.getOrderedMovements(board);
                    if (Logger.DEBUG) {
                        debugPrinter.print(board);
                        Logger.debug("orderedMovements : " + levelMovements);
                    }
                }
                // track possible moves
                movementsToExplore.put(level, levelMovements);
            }
            // we have not explored all moves for this level
            if (!levelMovements.isEmpty()) {
                // retrieve possible movement
                MovementScore<T> movement = levelMovements.removeFirst();
                // apply movement
                List<CardAction<T>> actions = board.applyMovement(movement);
                if (states.hasState(board, level)) {
                    // already met state, rollback actions
                    board.revertMovement(actions);
                } else
                // board finished and better ?
                {
                    int movesToFinish = solitaire.movesToFinish(board);
                    if (movesToFinish != 10000 && level + 1 + movesToFinish < bestLevel) {
                        newBestLevel(movement, movesToFinish);
                        // rollback actions
                        board.revertMovement(actions);
                    } else {
                        // inc counters
                        movements++;
                        movementsPerLevel.computeIfAbsent(level, i -> new AtomicLong(0)).incrementAndGet();
                        // go deeper :
                        // add movement to stack
                        movementsStack.add(movement);
                        // add actions to stack
                        actionsStack.add(actions);
                        // deeper level
                        level = level + 1;
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
        } while (movementsToExplore.get(0) != null && loops < 10_000_000);

        printStatus();
        while (!actionsStack.isEmpty()) {
            rollback();
        }
        printStatus();

        return bestMovements;
    }

    private void newBestLevel(MovementScore<T> movement, int movesToFinish) {
        level++;
        // track best
        bestLevel = level + movesToFinish;
        bestMovements = new ArrayList<>(movementsStack);
        bestMovements.add(movement);

        bestMovements.addAll(solitaire.getFinishMovements(board));

        states.discardStates(level);

        Logger.infoln("****************");
        Logger.infoln("New best level : " + bestLevel + " at iteration " + movements);
        for (MovementScore<T> bestMovement : bestMovements) {
            Logger.infoln(bestMovement);
        }
        Logger.infoln("****************");

        level--;
    }

    @SneakyThrows
    private void sleep(int ms) {
        Thread.sleep(ms);
    }

    private void printStatus() {
        Logger.infoln("**************************************************");
        Logger.infoln("loops : " + loops);
        Logger.infoln("moves : " + movements);
        Logger.infoln("rollbacks : " + rollbacks);
        Logger.infoln(states);
        Logger.infoln("movementsPerLevel : " + movementsPerLevel);
        List<Tuple> tuples = movementsToExplore.entrySet().stream()
                .map(e -> new Tuple(e.getKey(), e.getValue().size()))
                .toList();
        Logger.infoln("movementsToExplore count : " + tuples);
        Logger.infoln("movementsToExplore : " + movementsToExplore);
        Logger.infoln("movementsStack : " + movementsStack);
        Logger.infoln("level : " + level);
        Logger.infoln("Best level : " + bestLevel + ", bestMovements : ");
        Logger.infoln(bestMovements);
    }

    private void rollback() {
        // discard level
        movementsToExplore.remove(level);
        if (!movementsStack.isEmpty()) {
            // discard movement
            movementsStack.removeLast();
        }
        if (!actionsStack.isEmpty()) {
            // discard actions
            List<CardAction<T>> actions = actionsStack.removeLast();
            // revert actions
            board.revertMovement(actions);
        }
        // decrease level
        level = level - 1;
        rollbacks++;
    }

}
