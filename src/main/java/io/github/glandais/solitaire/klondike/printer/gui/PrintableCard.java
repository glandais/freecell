package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.cards.CardEnum;
import lombok.Data;

@Data
public class PrintableCard {
    CardEnum card;
    Vector2 position;
    Vector2 speed;
    boolean faceUp;
    int zIndex;

    public PrintableCard(CardEnum card, Vector2 position, Vector2 speed, boolean faceUp, int zIndex) {
        this.card = card;
        this.position = position;
        this.speed = speed;
        this.faceUp = faceUp;
        this.zIndex = zIndex;
    }
}
