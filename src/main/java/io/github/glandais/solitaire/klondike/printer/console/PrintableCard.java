package io.github.glandais.solitaire.klondike.printer.console;

import io.github.glandais.solitaire.common.cards.CardEnum;

public record PrintableCard(int i, int j, int zIndex, boolean emptyStack, CardEnum card, boolean hidden) {

}
