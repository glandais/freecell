package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.Cards;
import io.github.glandais.solitaire.common.board.PileType;

public record MovableStack<T extends PileType<T>>(T from, Cards cards) {
}
