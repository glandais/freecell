package io.github.glandais.freecell.board;

import io.github.glandais.freecell.board.enums.PilesEnum;
import io.github.glandais.freecell.cards.enums.CardEnum;

import java.util.List;

public record MovableStack(PilesEnum from, List<CardEnum> cards) {
}
