package io.github.glandais.solitaire.common.solver;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.board.Solitaire;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.move.MovementScore;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SolitaireSolver<T extends PileType<T>> {

    private final Solitaire<T> solitaire;
    private Board<T> board;
    private final Map<String, Integer> states = new TreeMap<>();

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

    public SolitaireSolver(Solitaire<T> solitaire, Board<T> board) {
        this.solitaire = solitaire;
        this.board = board;
    }

    /*
    final Map<Integer, AtomicLong> nodeCountPerLevel = new HashMap<>();
    int nodeCount = 0;
    int nodeBestScore = 1_000_000;
    List<MovementScore<T>> nodeBestMovements;
    List<MovementScore<T>> nodeMovements = new ArrayList<>();

    public class Node<N extends PileType<N>> {

        final int nodeLevel;
        final String tree;

        public Node(int nodeLevel, int maxLevel, String tree) {
            this.nodeLevel = nodeLevel;
            this.tree = tree;
            nodeCountPerLevel.computeIfAbsent(nodeLevel, l -> new AtomicLong()).incrementAndGet();
            nodeCount++;
            if (nodeCount % 100_000 == 0) {
                System.out.println(nodeCount + " nodes");
                System.out.println(states.size() + " states");
            }

            int score = solitaire.getScore(board);
            if (score < nodeBestScore) {
                nodeBestScore = score;
                nodeBestMovements = new ArrayList<>(nodeMovements);
                System.out.println(tree);
                System.out.println(nodeCountPerLevel);
                System.out.println(score);
                for (MovementScore<T> nodeBestMovement : nodeBestMovements) {
                    System.out.println(nodeBestMovement);
                }
            }

            if (nodeLevel < maxLevel) {
                List<Movement<T>> possibleMovements = board.computePossibleMovements();
                List<MovementScore<T>> levelMovements = getOrderedMovements(possibleMovements);
                int i = 0;
                for (MovementScore<T> movement : levelMovements) {
                    if (movement.score() <= 0) {
                        nodeMovements.add(movement);
                        List<CardAction<T>> actions = board.applyMovement(movement);
                        level = nodeLevel;
                        if (!hasState()) {
                            if (solitaire.isFinished(board)) {
                                Logger.infoln("!!!!!!!!!!!!!!");
                            }
                            new Node(nodeLevel + 1, maxLevel, tree + "->" + i);
                            i++;
                        }
                        board.revertMovement(actions);
                        nodeMovements.removeLast();
                    }
                }
            }
        }

        public Map<Integer, AtomicLong> getCountPerLevel() {
            return nodeCountPerLevel;
        }
    }

    public List<MovementScore<T>> solve() {
        Node<T> root = new Node<>(0, 20, "root");
        System.out.println(root.getCountPerLevel());
        for (Map.Entry<String, Integer> entry : states.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println(states.size());
        return List.of();
    }
     */

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
                {
                    int movesToFinish = solitaire.movesToFinish(board);
                    if (movesToFinish != 10000 && level + 1 + movesToFinish < bestLevel) {
                        newBestLevel(movement, movesToFinish);
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

    private List<MovementScore<T>> getOrderedMovements(List<Movement<T>> possibleMovements) {
        List<MovementScore<T>> orderedMovements = solitaire.getMovementScores(board, possibleMovements);
        orderedMovements.sort(Comparator.comparing(MovementScore::score));
        if (Logger.DEBUG) {
            Logger.debug("orderedMovements : " + orderedMovements);
        }
        return orderedMovements;
    }

    private void newBestLevel(MovementScore<T> movement, int movesToFinish) {
        level++;
        // track best
        bestLevel = level + movesToFinish;
        bestMovements = new ArrayList<>(movementsStack);
        bestMovements.add(movement);

        bestMovements.addAll(solitaire.getFinishMovements(board));

        states.values().removeIf(l -> l > level);

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
        String state = board.computeState();
        Integer existingLevel = states.get(state);
        if (existingLevel == null || level < existingLevel) {
            newStates++;
            states.put(state, level);
            return false;
        }
        existingStates++;
        return true;
    }

}
