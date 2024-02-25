package io.github.glandais.solitaire.common.board;

import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.move.MovementScore;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;

public interface Solitaire<T extends PileType<T>> {
    default Board<T> getRandomBoard() {
        return getBoard(new SecureRandom().nextInt());
    }

    Board<T> getBoard(long seed);

    int movesToFinish(Board<T> board);

    List<MovementScore<T>> getMovementScores(Board<T> board, List<Movement<T>> possibleMovements);

    int getScore(Board<T> board);

    List<MovementScore<T>> getFinishMovements(Board<T> board);
}
