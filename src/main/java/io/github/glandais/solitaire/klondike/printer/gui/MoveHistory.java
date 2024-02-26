package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;

import java.util.List;

public record MoveHistory(Movement<KlondikePilesEnum> movement, List<CardAction<KlondikePilesEnum>> actions) {
}
