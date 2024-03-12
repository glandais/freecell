package io.github.glandais.solitaire.common.solver;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.board.Solitaire;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.printer.SolitairePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class RecursiveSolitaireSolver<T extends PileType<T>> {

    private static final boolean DEBUG = false;

    private final Solitaire<T> solitaire;
    private final Board<T> initialBoard;
    private final int maxComputeMs;
    private final SolitairePrinter<T> debugPrinter;

    private States<T> states;

    Map<Integer, AtomicLong> exploredPerLevel;
    Map<Integer, Exploring> exploringPerLevel;
    int explored;
    int bestLevel;
    List<MovementScore<T>> bestMovements;
    List<MovementScore<T>> movements;
    long start;

    public RecursiveSolitaireSolver(Solitaire<T> solitaire, Board<T> board, int maxComputeMs, SolitairePrinter<T> debugPrinter) {
        this.solitaire = solitaire;
        this.initialBoard = board;
        this.maxComputeMs = maxComputeMs;
        this.debugPrinter = debugPrinter;
    }

    public List<MovementScore<T>> solve() {
        Board<T> board = this.initialBoard;//.copy();
        exploredPerLevel = new TreeMap<>();
        exploringPerLevel = new TreeMap<>();
        explored = 0;
        states = new States<>();
        bestLevel = 1_000_000;
        bestMovements = null;
        movements = new ArrayList<>();
        start = System.currentTimeMillis();
        explore(board, 0, 200, "root");

        printStatus();

        return bestMovements;
    }

    private void printStatus() {
        Logger.infoln("Ellapsed : " + (System.currentTimeMillis() - start) + "ms");
        Logger.infoln(explored + " iterations");
        Logger.infoln("bestFinishedLevel : " + bestLevel);
        String exploreState = exploredPerLevel.entrySet().stream()
                .map(e -> {
                    String s = e.getKey() + "=" + e.getValue();
                    Exploring exploring = exploringPerLevel.get(e.getKey());
                    if (exploring != null) {
                        s = s + "(" + exploring + ")";
                    }
                    return s;
                })
                .collect(Collectors.joining(", "));
        Logger.infoln("exploreState : " + exploreState);
        Logger.infoln(states);
    }

    protected void explore(Board<T> board, int level, int maxLevel, String tree) {
        if (System.currentTimeMillis() - start > maxComputeMs) {
            return;
        }

        exploredPerLevel.computeIfAbsent(level, l -> new AtomicLong()).incrementAndGet();
        explored++;
        if (explored % 200_000 == 0) {
            printStatus();
        }
        if (states.hasState(board, level)) {
            if (DEBUG) {
                Logger.infoln("Already met state");
            }
            return;
        }

        int movesToFinish = solitaire.movesToFinish(board);
        if (movesToFinish != Solitaire.UNSOLVED) {
            if (level + movesToFinish < bestLevel) {
                bestLevel = level + movesToFinish;
                bestMovements = new ArrayList<>(movements);
                bestMovements.addAll(solitaire.getFinishMovements(board));

                Logger.infoln("****************");
                printStatus();
                for (MovementScore<T> bestMovement : bestMovements) {
                    Logger.infoln(bestMovement);
                }
                Logger.infoln("****************");
            }
            return;
        }

        if (level < maxLevel && level < bestLevel) {
            if (DEBUG) {
                Logger.infoln("explored : " + explored);
                Logger.infoln("START path : " + tree);
                Logger.infoln("START level : " + level);
                debugPrinter.print(board);
            }
            List<MovementScore<T>> levelMovements = solitaire.getOrderedMovements(board);
            Exploring exploring = new Exploring(levelMovements.size());
            exploringPerLevel.put(level, exploring);
            int i = 0;
            for (MovementScore<T> movement : levelMovements) {
                if (DEBUG) {
                    Logger.infoln("path : " + tree);
                    Logger.infoln("level : " + level);
                    Logger.infoln(movement);
                }
                exploring.i++;
                movements.add(movement);
                List<CardAction<T>> actions = board.applyMovement(movement);
                explore(board, level + 1, maxLevel, tree + "->" + i++);
                board.revertMovement(actions);
                movements.removeLast();
            }
            exploringPerLevel.remove(level);
            if (DEBUG) {
                Logger.infoln("END path : " + tree);
                Logger.infoln("END level : " + level);
            }
        }
    }

}
