package io.github.glandais.solitaire.common.solver;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.board.Solitaire;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.printer.SolitairePrinter;
import io.github.glandais.solitaire.klondike.serde.Serde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RecursiveSolitaireSolver<T extends PileType<T>> {

    private static final boolean DEBUG = false;

    private final Solitaire<T> solitaire;
    private final Board<T> originalBoard;
    private final Board<T> board;
    private final int maxComputeMs;
    private final SolitairePrinter<T> debugPrinter;

    private States<T> states;

    int cores;
    AtomicInteger explored;
    Map<Integer, AtomicLong> exploredPerLevel;
    AtomicInteger running;

    int maxLevel;
    int bestLevel;
    List<MovementScore<T>> bestMovements;
    long start;

    public RecursiveSolitaireSolver(Solitaire<T> solitaire, Board<T> board, int maxComputeMs, SolitairePrinter<T> debugPrinter) {
        this.solitaire = solitaire;
        this.originalBoard = board.copy();
        this.board = board;
        this.maxComputeMs = maxComputeMs;
        this.debugPrinter = debugPrinter;
    }

    public List<MovementScore<T>> solve() {
        states = new States<>();
        bestLevel = 1_000_000;
        bestMovements = null;
        maxLevel = 200;
        start = System.currentTimeMillis();
        explored = new AtomicInteger(0);
        exploredPerLevel = Collections.synchronizedMap(new TreeMap<>());

        cores = Runtime.getRuntime().availableProcessors();
        running = new AtomicInteger(1);

        explore(new RecursiveSolitaireState<>(board, 0, "root", new ArrayList<>()));

        printStatus();

        return bestMovements;
    }

    protected void explore(RecursiveSolitaireState<T> recursiveSolitaireState) {
        if (maxComputeMs > 0 && System.currentTimeMillis() - start > maxComputeMs) {
            return;
        }

        exploredPerLevel.computeIfAbsent(recursiveSolitaireState.level, l -> new AtomicLong()).incrementAndGet();
        int exploredValue = explored.incrementAndGet();
        if (exploredValue % 200_000 == 0) {
            printStatus();
        }
        if (states.hasState(recursiveSolitaireState.board, recursiveSolitaireState.level)) {
            if (DEBUG) {
                Logger.infoln("Already met state");
            }
            return;
        }

        int movesToFinish = solitaire.movesToFinish(recursiveSolitaireState.board);
        if (movesToFinish != Solitaire.UNSOLVED) {
            checkBestLevel(movesToFinish, recursiveSolitaireState);
            return;
        }

        if (recursiveSolitaireState.level < maxLevel && recursiveSolitaireState.level < bestLevel) {
            if (DEBUG) {
                Logger.infoln("explored : " + explored);
                Logger.infoln("START path : " + recursiveSolitaireState.tree);
                Logger.infoln("START level : " + recursiveSolitaireState.level);
                debugPrinter.print(recursiveSolitaireState.board);
            }
            List<MovementScore<T>> levelMovements = solitaire.getOrderedMovements(recursiveSolitaireState.board);

            int i = 0;
            for (MovementScore<T> movement : levelMovements) {
                if (!split(recursiveSolitaireState, movement, i)) {
                    exploreMovement(recursiveSolitaireState, movement, i);
                }
                i++;
            }
            if (DEBUG) {
                Logger.infoln("END path : " + recursiveSolitaireState.tree);
                Logger.infoln("END level : " + recursiveSolitaireState.level);
            }
        }
    }

    private boolean split(RecursiveSolitaireState<T> recursiveSolitaireState, MovementScore<T> movement, int i) {
        if (running.get() < cores) {
            synchronized (this) {
                if (running.get() < cores) {
                    running.incrementAndGet();
                    RecursiveSolitaireState<T> splitState = new RecursiveSolitaireState<>(recursiveSolitaireState);
                    new Thread(() -> {
                        exploreMovement(splitState, movement, i);
                        running.decrementAndGet();
                    }).start();
                    return true;
                }
            }
        }
        return false;
    }

    private void exploreMovement(RecursiveSolitaireState<T> recursiveSolitaireState, MovementScore<T> movement, int i) {
        if (DEBUG) {
            Logger.infoln("path : " + recursiveSolitaireState.tree);
            Logger.infoln("level : " + recursiveSolitaireState.level);
            Logger.infoln(movement);
        }
        recursiveSolitaireState.movements.add(movement);
        List<CardAction<T>> actions = recursiveSolitaireState.board.applyMovement(movement);
        String previousTree = recursiveSolitaireState.tree;
        recursiveSolitaireState.tree = previousTree + "->" + i;
        recursiveSolitaireState.level++;
        explore(recursiveSolitaireState);
        recursiveSolitaireState.level--;
        recursiveSolitaireState.tree = previousTree;
        recursiveSolitaireState.board.revertMovement(actions);
        recursiveSolitaireState.movements.removeLast();
    }

    private void checkBestLevel(int movesToFinish, RecursiveSolitaireState<T> recursiveSolitaireState) {
        synchronized (this) {
            int level = recursiveSolitaireState.level;
            if (level + movesToFinish < bestLevel) {
                bestLevel = level + movesToFinish;
                bestMovements = new ArrayList<>(recursiveSolitaireState.movements);
                bestMovements.addAll(solitaire.getFinishMovements(recursiveSolitaireState.board));
                states.discardStates(level);

                Logger.infoln("****************");
                printStatus();
                for (MovementScore<T> bestMovement : bestMovements) {
                    Logger.infoln(bestMovement);
                }
                Logger.infoln("****************");
                Serde.save("board-" + bestLevel + ".json", solitaire.getBoardMoves(originalBoard, bestMovements));
            }
        }
    }

    private void printStatus() {
        Logger.infoln("Elapsed : " + (System.currentTimeMillis() - start) + "ms / " + maxComputeMs + "ms");
        Logger.infoln(explored + " iterations");
        Logger.infoln("exploredPerLevel : " + exploredPerLevel);
        Logger.infoln("bestFinishedLevel : " + bestLevel);
        Logger.infoln(states);
    }

}
