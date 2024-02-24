package io.github.glandais.solitaire.common.cards;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuiteEnum {
    SPADE(ColorEnum.BLACK, "♠"),
    HEART(ColorEnum.RED, "♥"),
    DIAMOND(ColorEnum.RED, "♦"),
    CLUB(ColorEnum.BLACK, "♣");

    final ColorEnum colorEnum;

    final String label;

}
