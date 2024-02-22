package io.github.glandais.solitaire.klondike.printer.gui;

import dev.aurumbyte.sypherengine.util.math.Vector2;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;

public record PrintableCard(CardEnum card, Vector2 position, boolean faceUp, int zIndex) {

}
