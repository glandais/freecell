package io.github.glandais.solitaire.common.board;

import io.github.glandais.solitaire.common.move.Movement;

import java.security.SecureRandom;

public interface Solitaire<T extends PileType<T>> {
    default Board<T> getRandomBoard() {
        return getBoard(new SecureRandom().nextInt());
    }

    Board<T> getBoard(long seed);

    boolean isFinished(Board<T> board);

    int getScore(Board<T> board);

    int getMovementScore(Movement<T> movement, Board<T> newBoard, Board<T> oldBoard);
}
