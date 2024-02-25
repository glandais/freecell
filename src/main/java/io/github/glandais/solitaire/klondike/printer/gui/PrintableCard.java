package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.cards.CardEnum;
import lombok.Data;

@Data
public class PrintableCard {
    CardEnum card;
    Vector2 position;
    Vector2 speed;
    PrintableCardFace face;
    int zIndex;
    boolean dragged = false;

    public PrintableCard(CardEnum card, Vector2 position, Vector2 speed, PrintableCardFace face, int zIndex) {
        this.card = card;
        this.position = position;
        this.speed = speed;
        this.face = face;
        this.zIndex = zIndex;
    }
}
