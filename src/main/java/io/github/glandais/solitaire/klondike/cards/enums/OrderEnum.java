package io.github.glandais.solitaire.klondike.cards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderEnum {
    ACE(1, " A"),
    TWO(2, " 2"),
    THREE(3, " 3"),
    FOUR(4, " 4"),
    FIVE(5, " 5"),
    SIX(6, " 6"),
    SEVEN(7, " 7"),
    HEIGHT(8, " 8"),
    NINE(9, " 9"),
    TEN(10, "10"),
    JACK(11, " J"),
    QUEEN(12, " Q"),
    KING(13, " K");

    final int order;

    final String label;

}
