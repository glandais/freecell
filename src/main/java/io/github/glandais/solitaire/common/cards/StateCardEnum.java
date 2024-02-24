package io.github.glandais.solitaire.common.cards;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StateCardEnum {
    ACE_BLACK(OrderEnum.ACE, ColorEnum.BLACK),
    ACE_RED(OrderEnum.ACE, ColorEnum.RED),
    TWO_BLACK(OrderEnum.TWO, ColorEnum.BLACK),
    TWO_RED(OrderEnum.TWO, ColorEnum.RED),
    THREE_BLACK(OrderEnum.THREE, ColorEnum.BLACK),
    THREE_RED(OrderEnum.THREE, ColorEnum.RED),
    FOUR_BLACK(OrderEnum.FOUR, ColorEnum.BLACK),
    FOUR_RED(OrderEnum.FOUR, ColorEnum.RED),
    FIVE_BLACK(OrderEnum.FIVE, ColorEnum.BLACK),
    FIVE_RED(OrderEnum.FIVE, ColorEnum.RED),
    SIX_BLACK(OrderEnum.SIX, ColorEnum.BLACK),
    SIX_RED(OrderEnum.SIX, ColorEnum.RED),
    SEVEN_BLACK(OrderEnum.SEVEN, ColorEnum.BLACK),
    SEVEN_RED(OrderEnum.SEVEN, ColorEnum.RED),
    HEIGHT_BLACK(OrderEnum.HEIGHT, ColorEnum.BLACK),
    HEIGHT_RED(OrderEnum.HEIGHT, ColorEnum.RED),
    NINE_BLACK(OrderEnum.NINE, ColorEnum.BLACK),
    NINE_RED(OrderEnum.NINE, ColorEnum.RED),
    TEN_BLACK(OrderEnum.TEN, ColorEnum.BLACK),
    TEN_RED(OrderEnum.TEN, ColorEnum.RED),
    JACK_BLACK(OrderEnum.JACK, ColorEnum.BLACK),
    JACK_RED(OrderEnum.JACK, ColorEnum.RED),
    QUEEN_BLACK(OrderEnum.QUEEN, ColorEnum.BLACK),
    QUEEN_RED(OrderEnum.QUEEN, ColorEnum.RED),
    KING_BLACK(OrderEnum.KING, ColorEnum.BLACK),
    KING_RED(OrderEnum.KING, ColorEnum.RED);

    final OrderEnum order;

    final ColorEnum color;

}
