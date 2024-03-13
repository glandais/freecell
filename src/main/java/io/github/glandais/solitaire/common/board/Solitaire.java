package io.github.glandais.solitaire.common.board;

import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.move.MovementScore;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;

public interface Solitaire<T extends PileType<T>> {
    int UNSOLVED = 10000;
    int ERASE_OTHER_MOVEMENTS = -1_000_000_000;

    default Board<T> getRandomBoard() {
        return getBoard(new SecureRandom().nextInt());
    }

    Board<T> getBoard(long seed);

    int movesToFinish(Board<T> board);

    List<MovementScore<T>> getFinishMovements(Board<T> board);

    List<MovementScore<T>> getMovementScores(Board<T> board, List<Movement<T>> possibleMovements);

    Object getBoardMoves(Board<T> board, List<MovementScore<T>> nodeBestMovements);

    default List<MovementScore<T>> getOrderedMovements(Board<T> board) {
        List<Movement<T>> possibleMovements = board.computePossibleMovements();
        if (possibleMovements.isEmpty()) {
            return List.of();
        }
        List<MovementScore<T>> orderedMovements = getMovementScores(board, possibleMovements);
        if (orderedMovements.isEmpty()) {
            return List.of();
        }
        if (orderedMovements.size() > 1) {
            orderedMovements.sort(Comparator.comparing(MovementScore::getScore));
        }
        if (orderedMovements.getFirst().getScore() == ERASE_OTHER_MOVEMENTS) {
            return List.of(orderedMovements.getFirst());
        }
        return orderedMovements;
    }

}
