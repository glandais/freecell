package io.github.glandais.solitaire.common.move;

import io.github.glandais.solitaire.common.board.Cards;
import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.cards.CardEnum;

import java.util.List;

public record MovableStack<T extends PileType<T>>(T from, Cards cards) {
}
