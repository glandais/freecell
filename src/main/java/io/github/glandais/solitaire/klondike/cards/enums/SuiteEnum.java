package io.github.glandais.solitaire.klondike.cards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuiteEnum {
    HEART(ColorEnum.RED, "♡"),
    DIAMOND(ColorEnum.RED, "♢"),
    CLUB(ColorEnum.BLACK, "♧"),
    SPADE(ColorEnum.BLACK, "♤");

    final ColorEnum colorEnum;

    final String label;

}
