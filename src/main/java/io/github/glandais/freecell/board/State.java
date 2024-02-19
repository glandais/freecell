package io.github.glandais.freecell.board;

import io.github.glandais.freecell.cards.enums.CardEnum;

import java.util.List;
import java.util.Set;

public record State(List<CardEnum> stock, List<CardEnum> foundations, Set<TableauState> tableauSet) {

}
