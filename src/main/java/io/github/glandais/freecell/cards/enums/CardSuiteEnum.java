package io.github.glandais.freecell.cards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardSuiteEnum {
    HEART(CardColorEnum.RED, "♡"),
    DIAMOND(CardColorEnum.RED, "♢"),
    CLUB(CardColorEnum.BLACK, "♧"),
    SPADE(CardColorEnum.BLACK, "♤");

    final CardColorEnum cardColorEnum;

    final String label;

}
