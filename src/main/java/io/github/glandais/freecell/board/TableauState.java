package io.github.glandais.freecell.board;

import io.github.glandais.freecell.cards.enums.CardEnum;

import java.util.List;

public record TableauState(List<CardEnum> hidden, List<CardEnum> visible) {
}
