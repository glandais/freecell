package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.Move;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;

import java.util.List;

public record MoveHistory(Move<KlondikePilesEnum> move, List<CardAction<KlondikePilesEnum>> actions) {
}
