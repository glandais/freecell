package io.github.glandais.freecell.printer.gui;

import dev.aurumbyte.sypherengine.util.math.Vector2;
import io.github.glandais.freecell.cards.enums.CardEnum;

public record PrintableCard(CardEnum card, Vector2 position, boolean faceUp, int zIndex) {

}
