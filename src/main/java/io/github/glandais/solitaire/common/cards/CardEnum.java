package io.github.glandais.solitaire.common.cards;

import lombok.Getter;

@Getter
public enum CardEnum {
    ACE_HEART(OrderEnum.ACE, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.ACE_RED),
    ACE_DIAMOND(OrderEnum.ACE, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.ACE_RED),
    ACE_CLUB(OrderEnum.ACE, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.ACE_BLACK),
    ACE_SPADE(OrderEnum.ACE, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.ACE_BLACK),
    TWO_HEART(OrderEnum.TWO, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.TWO_RED),
    TWO_DIAMOND(OrderEnum.TWO, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.TWO_RED),
    TWO_CLUB(OrderEnum.TWO, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.TWO_BLACK),
    TWO_SPADE(OrderEnum.TWO, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.TWO_BLACK),
    THREE_HEART(OrderEnum.THREE, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.THREE_RED),
    THREE_DIAMOND(OrderEnum.THREE, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.THREE_RED),
    THREE_CLUB(OrderEnum.THREE, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.THREE_BLACK),
    THREE_SPADE(OrderEnum.THREE, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.THREE_BLACK),
    FOUR_HEART(OrderEnum.FOUR, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.FOUR_RED),
    FOUR_DIAMOND(OrderEnum.FOUR, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.FOUR_RED),
    FOUR_CLUB(OrderEnum.FOUR, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.FOUR_BLACK),
    FOUR_SPADE(OrderEnum.FOUR, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.FOUR_BLACK),
    FIVE_HEART(OrderEnum.FIVE, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.FIVE_RED),
    FIVE_DIAMOND(OrderEnum.FIVE, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.FIVE_RED),
    FIVE_CLUB(OrderEnum.FIVE, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.FIVE_BLACK),
    FIVE_SPADE(OrderEnum.FIVE, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.FIVE_BLACK),
    SIX_HEART(OrderEnum.SIX, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.SIX_RED),
    SIX_DIAMOND(OrderEnum.SIX, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.SIX_RED),
    SIX_CLUB(OrderEnum.SIX, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.SIX_BLACK),
    SIX_SPADE(OrderEnum.SIX, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.SIX_BLACK),
    SEVEN_HEART(OrderEnum.SEVEN, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.SEVEN_RED),
    SEVEN_DIAMOND(OrderEnum.SEVEN, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.SEVEN_RED),
    SEVEN_CLUB(OrderEnum.SEVEN, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.SEVEN_BLACK),
    SEVEN_SPADE(OrderEnum.SEVEN, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.SEVEN_BLACK),
    HEIGHT_HEART(OrderEnum.HEIGHT, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.HEIGHT_RED),
    HEIGHT_DIAMOND(OrderEnum.HEIGHT, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.HEIGHT_RED),
    HEIGHT_CLUB(OrderEnum.HEIGHT, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.HEIGHT_BLACK),
    HEIGHT_SPADE(OrderEnum.HEIGHT, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.HEIGHT_BLACK),
    NINE_HEART(OrderEnum.NINE, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.NINE_RED),
    NINE_DIAMOND(OrderEnum.NINE, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.NINE_RED),
    NINE_CLUB(OrderEnum.NINE, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.NINE_BLACK),
    NINE_SPADE(OrderEnum.NINE, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.NINE_BLACK),
    TEN_HEART(OrderEnum.TEN, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.TEN_RED),
    TEN_DIAMOND(OrderEnum.TEN, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.TEN_RED),
    TEN_CLUB(OrderEnum.TEN, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.TEN_BLACK),
    TEN_SPADE(OrderEnum.TEN, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.TEN_BLACK),
    JACK_HEART(OrderEnum.JACK, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.JACK_RED),
    JACK_DIAMOND(OrderEnum.JACK, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.JACK_RED),
    JACK_CLUB(OrderEnum.JACK, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.JACK_BLACK),
    JACK_SPADE(OrderEnum.JACK, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.JACK_BLACK),
    QUEEN_HEART(OrderEnum.QUEEN, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.QUEEN_RED),
    QUEEN_DIAMOND(OrderEnum.QUEEN, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.QUEEN_RED),
    QUEEN_CLUB(OrderEnum.QUEEN, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.QUEEN_BLACK),
    QUEEN_SPADE(OrderEnum.QUEEN, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.QUEEN_BLACK),
    KING_HEART(OrderEnum.KING, SuiteEnum.HEART, ColorEnum.RED, StateCardEnum.KING_RED),
    KING_DIAMOND(OrderEnum.KING, SuiteEnum.DIAMOND, ColorEnum.RED, StateCardEnum.KING_RED),
    KING_CLUB(OrderEnum.KING, SuiteEnum.CLUB, ColorEnum.BLACK, StateCardEnum.KING_BLACK),
    KING_SPADE(OrderEnum.KING, SuiteEnum.SPADE, ColorEnum.BLACK, StateCardEnum.KING_BLACK);

    final OrderEnum orderEnum;

    final SuiteEnum suiteEnum;

    final ColorEnum colorEnum;

    final StateCardEnum stateCardEnum;

    final String longLabel;

    final String label;

    CardEnum(OrderEnum orderEnum, SuiteEnum suiteEnum, ColorEnum colorEnum, StateCardEnum stateCardEnum) {
        this.orderEnum = orderEnum;
        this.suiteEnum = suiteEnum;
        this.colorEnum = colorEnum;
        this.stateCardEnum = stateCardEnum;
        this.longLabel = suiteEnum.getLabel() + orderEnum.getLabel();
        int suiteOrdinal = suiteEnum.ordinal();
        int orderOrdinal = orderEnum.ordinal();
        if (orderOrdinal > 10) {
            orderOrdinal = orderOrdinal + 1;
        }
        int unicode = 0x1F0A1 + suiteOrdinal * 16 + orderOrdinal;
        this.label = new String(Character.toChars(unicode));
    }

    @Override
    public String toString() {
        return label;
    }

}
