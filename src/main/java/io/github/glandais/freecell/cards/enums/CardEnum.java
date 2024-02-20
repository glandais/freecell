package io.github.glandais.freecell.cards.enums;

import lombok.Getter;

@Getter
public enum CardEnum {
    ACE_HEART(CardOrderEnum.ACE, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.ACE_RED),
    ACE_DIAMOND(CardOrderEnum.ACE, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.ACE_RED),
    ACE_CLUB(CardOrderEnum.ACE, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.ACE_BLACK),
    ACE_SPADE(CardOrderEnum.ACE, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.ACE_BLACK),
    TWO_HEART(CardOrderEnum.TWO, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.TWO_RED),
    TWO_DIAMOND(CardOrderEnum.TWO, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.TWO_RED),
    TWO_CLUB(CardOrderEnum.TWO, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.TWO_BLACK),
    TWO_SPADE(CardOrderEnum.TWO, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.TWO_BLACK),
    THREE_HEART(CardOrderEnum.THREE, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.THREE_RED),
    THREE_DIAMOND(CardOrderEnum.THREE, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.THREE_RED),
    THREE_CLUB(CardOrderEnum.THREE, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.THREE_BLACK),
    THREE_SPADE(CardOrderEnum.THREE, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.THREE_BLACK),
    FOUR_HEART(CardOrderEnum.FOUR, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.FOUR_RED),
    FOUR_DIAMOND(CardOrderEnum.FOUR, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.FOUR_RED),
    FOUR_CLUB(CardOrderEnum.FOUR, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.FOUR_BLACK),
    FOUR_SPADE(CardOrderEnum.FOUR, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.FOUR_BLACK),
    FIVE_HEART(CardOrderEnum.FIVE, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.FIVE_RED),
    FIVE_DIAMOND(CardOrderEnum.FIVE, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.FIVE_RED),
    FIVE_CLUB(CardOrderEnum.FIVE, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.FIVE_BLACK),
    FIVE_SPADE(CardOrderEnum.FIVE, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.FIVE_BLACK),
    SIX_HEART(CardOrderEnum.SIX, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.SIX_RED),
    SIX_DIAMOND(CardOrderEnum.SIX, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.SIX_RED),
    SIX_CLUB(CardOrderEnum.SIX, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.SIX_BLACK),
    SIX_SPADE(CardOrderEnum.SIX, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.SIX_BLACK),
    SEVEN_HEART(CardOrderEnum.SEVEN, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.SEVEN_RED),
    SEVEN_DIAMOND(CardOrderEnum.SEVEN, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.SEVEN_RED),
    SEVEN_CLUB(CardOrderEnum.SEVEN, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.SEVEN_BLACK),
    SEVEN_SPADE(CardOrderEnum.SEVEN, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.SEVEN_BLACK),
    HEIGHT_HEART(CardOrderEnum.HEIGHT, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.HEIGHT_RED),
    HEIGHT_DIAMOND(CardOrderEnum.HEIGHT, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.HEIGHT_RED),
    HEIGHT_CLUB(CardOrderEnum.HEIGHT, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.HEIGHT_BLACK),
    HEIGHT_SPADE(CardOrderEnum.HEIGHT, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.HEIGHT_BLACK),
    NINE_HEART(CardOrderEnum.NINE, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.NINE_RED),
    NINE_DIAMOND(CardOrderEnum.NINE, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.NINE_RED),
    NINE_CLUB(CardOrderEnum.NINE, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.NINE_BLACK),
    NINE_SPADE(CardOrderEnum.NINE, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.NINE_BLACK),
    TEN_HEART(CardOrderEnum.TEN, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.TEN_RED),
    TEN_DIAMOND(CardOrderEnum.TEN, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.TEN_RED),
    TEN_CLUB(CardOrderEnum.TEN, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.TEN_BLACK),
    TEN_SPADE(CardOrderEnum.TEN, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.TEN_BLACK),
    JACK_HEART(CardOrderEnum.JACK, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.JACK_RED),
    JACK_DIAMOND(CardOrderEnum.JACK, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.JACK_RED),
    JACK_CLUB(CardOrderEnum.JACK, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.JACK_BLACK),
    JACK_SPADE(CardOrderEnum.JACK, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.JACK_BLACK),
    QUEEN_HEART(CardOrderEnum.QUEEN, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.QUEEN_RED),
    QUEEN_DIAMOND(CardOrderEnum.QUEEN, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.QUEEN_RED),
    QUEEN_CLUB(CardOrderEnum.QUEEN, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.QUEEN_BLACK),
    QUEEN_SPADE(CardOrderEnum.QUEEN, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.QUEEN_BLACK),
    KING_HEART(CardOrderEnum.KING, CardSuiteEnum.HEART, CardColorEnum.RED, StateCardEnum.KING_RED),
    KING_DIAMOND(CardOrderEnum.KING, CardSuiteEnum.DIAMOND, CardColorEnum.RED, StateCardEnum.KING_RED),
    KING_CLUB(CardOrderEnum.KING, CardSuiteEnum.CLUB, CardColorEnum.BLACK, StateCardEnum.KING_BLACK),
    KING_SPADE(CardOrderEnum.KING, CardSuiteEnum.SPADE, CardColorEnum.BLACK, StateCardEnum.KING_BLACK);

    final CardOrderEnum cardOrderEnum;

    final CardSuiteEnum cardSuiteEnum;

    final CardColorEnum cardColorEnum;

    final StateCardEnum stateCardEnum;

    final String label;

    CardEnum(CardOrderEnum cardOrderEnum, CardSuiteEnum cardSuiteEnum, CardColorEnum cardColorEnum, StateCardEnum stateCardEnum) {
        this.cardOrderEnum = cardOrderEnum;
        this.cardSuiteEnum = cardSuiteEnum;
        this.cardColorEnum = cardColorEnum;
        this.stateCardEnum = stateCardEnum;
        this.label = cardSuiteEnum.getLabel() + " " + cardOrderEnum.getLabel();
    }

    @Override
    public String toString() {
        return label;
    }

}
