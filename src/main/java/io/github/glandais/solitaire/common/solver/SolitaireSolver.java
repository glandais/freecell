package io.github.glandais.solitaire.common.solver;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.board.Solitaire;
import io.github.glandais.solitaire.common.board.State;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.common.printer.SolitairePrinter;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SolitaireSolver<T extends PileType<T>> {

    private final Solitaire<T> solitaire;
    private final Board<T> originalBoard;
    private final boolean follow;
    private Board<T> board;
    private final SolitairePrinter<T> printer;
    private final Map<State, Integer> states = new HashMap<>();

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
    long newStates = 0;
    long existingStates = 0;
    Map<Integer, AtomicLong> movementsPerLevel = new HashMap<>();

    public SolitaireSolver(Solitaire<T> solitaire, Board<T> board, SolitairePrinter<T> printer, boolean follow) {
        this.solitaire = solitaire;
        this.originalBoard = board.copy();
        this.board = originalBoard.copy();
        this.printer = printer;
        this.follow = follow;
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
                // get possible moves for current level
                List<Movement<T>> possibleMovements = board.computePossibleMovements();
                levelMovements = getOrderedMovements(possibleMovements);
                // track possible moves
                movementsToExplore.put(level, levelMovements);
            }
            // we have not explored all moves for this level
            if (!levelMovements.isEmpty()) {
                // retrieve possible movement
                MovementScore<T> movement = levelMovements.removeFirst();
                // apply movement
                List<CardAction<T>> actions = board.applyMovement(movement);
                if (hasState()) {
                    // already met state, rollback actions
                    board.revertMovement(actions);
                } else
                    // board finished and better ?
                    if (solitaire.isFinished(board) && level < bestLevel) {
                        newBestLevel();
                        // rollback actions
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
                    }
            } else {
                // rollback, move level up
                rollback();
            }
            loops++;
            if (follow && loops % 10_000 == 0) {
                printer.print(board);
            }
            if (loops % 100_000 == 0) {
                printStatus();
            }
//            sleep(500);
        } while (movementsToExplore.get(0) != null && loops < 10_000_000);
//        } while (movementsToExplore.get(0) != null && loops < 1_000_000 && bestMovements == null);

        printStatus();

        this.board = this.originalBoard.copy();
        return bestMovements;
    }

    private List<MovementScore<T>> getOrderedMovements(List<Movement<T>> possibleMovements) {
        List<MovementScore<T>> orderedMovements = possibleMovements.stream().map(
                this::getMovementScoreWithBoardScore
//                this::getMovementScoreWithMovementScore
        ).collect(Collectors.toList());
        orderedMovements.sort(Comparator.comparing(MovementScore::score));
        if (Logger.DEBUG) {
            Logger.debug("orderedMovements : " + orderedMovements);
        }
        return orderedMovements;
    }

    private MovementScore<T> getMovementScoreWithBoardScore(Movement<T> movement) {
        List<CardAction<T>> actions = board.applyMovement(movement);
        int score = solitaire.getScore(board);
        board.revertMovement(actions);
        return new MovementScore<>(movement, score);
    }

    private MovementScore<T> getMovementScoreWithMovementScore(Movement<T> movement) {
        Board<T> oldBoard = board.copy();
        List<CardAction<T>> actions = board.applyMovement(movement);
        Board<T> newBoard = board.copy();
        board.revertMovement(actions);

        int score = solitaire.getMovementScore(movement, newBoard, oldBoard);
        if (score == 0) {
            Logger.infoln("Unsupported movement ! " + movement);
//            printer.print();
            solitaire.getMovementScore(movement, newBoard, oldBoard);
        }
        return new MovementScore<>(movement, score);
    }

    private void newBestLevel() {
        Logger.infoln("New best level : " + level + " at iteration " + movements);
        Logger.infoln(bestMovements);
        printer.print(board);
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
        Logger.infoln("moves : " + movements);
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
        List<CardAction<T>> actions = actionsStack.removeLast();
        // revert actions
        board.revertMovement(actions);
        // decrease level
        level = level - 1;
        rollbacks++;
    }

    public boolean hasState() {
        State state = board.computeState();
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
