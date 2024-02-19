package io.github.glandais.freecell.cards.enums;

import lombok.Getter;

@Getter
public enum CardEnum {
    ACE_HEART(CardOrderEnum.ACE, CardSuiteEnum.HEART, CardColorEnum.RED),
    ACE_DIAMOND(CardOrderEnum.ACE, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    ACE_CLUB(CardOrderEnum.ACE, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    ACE_SPADE(CardOrderEnum.ACE, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    TWO_HEART(CardOrderEnum.TWO, CardSuiteEnum.HEART, CardColorEnum.RED),
    TWO_DIAMOND(CardOrderEnum.TWO, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    TWO_CLUB(CardOrderEnum.TWO, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    TWO_SPADE(CardOrderEnum.TWO, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    THREE_HEART(CardOrderEnum.THREE, CardSuiteEnum.HEART, CardColorEnum.RED),
    THREE_DIAMOND(CardOrderEnum.THREE, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    THREE_CLUB(CardOrderEnum.THREE, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    THREE_SPADE(CardOrderEnum.THREE, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    FOUR_HEART(CardOrderEnum.FOUR, CardSuiteEnum.HEART, CardColorEnum.RED),
    FOUR_DIAMOND(CardOrderEnum.FOUR, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    FOUR_CLUB(CardOrderEnum.FOUR, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    FOUR_SPADE(CardOrderEnum.FOUR, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    FIVE_HEART(CardOrderEnum.FIVE, CardSuiteEnum.HEART, CardColorEnum.RED),
    FIVE_DIAMOND(CardOrderEnum.FIVE, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    FIVE_CLUB(CardOrderEnum.FIVE, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    FIVE_SPADE(CardOrderEnum.FIVE, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    SIX_HEART(CardOrderEnum.SIX, CardSuiteEnum.HEART, CardColorEnum.RED),
    SIX_DIAMOND(CardOrderEnum.SIX, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    SIX_CLUB(CardOrderEnum.SIX, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    SIX_SPADE(CardOrderEnum.SIX, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    SEVEN_HEART(CardOrderEnum.SEVEN, CardSuiteEnum.HEART, CardColorEnum.RED),
    SEVEN_DIAMOND(CardOrderEnum.SEVEN, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    SEVEN_CLUB(CardOrderEnum.SEVEN, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    SEVEN_SPADE(CardOrderEnum.SEVEN, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    HEIGHT_HEART(CardOrderEnum.HEIGHT, CardSuiteEnum.HEART, CardColorEnum.RED),
    HEIGHT_DIAMOND(CardOrderEnum.HEIGHT, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    HEIGHT_CLUB(CardOrderEnum.HEIGHT, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    HEIGHT_SPADE(CardOrderEnum.HEIGHT, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    NINE_HEART(CardOrderEnum.NINE, CardSuiteEnum.HEART, CardColorEnum.RED),
    NINE_DIAMOND(CardOrderEnum.NINE, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    NINE_CLUB(CardOrderEnum.NINE, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    NINE_SPADE(CardOrderEnum.NINE, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    TEN_HEART(CardOrderEnum.TEN, CardSuiteEnum.HEART, CardColorEnum.RED),
    TEN_DIAMOND(CardOrderEnum.TEN, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    TEN_CLUB(CardOrderEnum.TEN, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    TEN_SPADE(CardOrderEnum.TEN, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    JACK_HEART(CardOrderEnum.JACK, CardSuiteEnum.HEART, CardColorEnum.RED),
    JACK_DIAMOND(CardOrderEnum.JACK, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    JACK_CLUB(CardOrderEnum.JACK, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    JACK_SPADE(CardOrderEnum.JACK, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    QUEEN_HEART(CardOrderEnum.QUEEN, CardSuiteEnum.HEART, CardColorEnum.RED),
    QUEEN_DIAMOND(CardOrderEnum.QUEEN, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    QUEEN_CLUB(CardOrderEnum.QUEEN, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    QUEEN_SPADE(CardOrderEnum.QUEEN, CardSuiteEnum.SPADE, CardColorEnum.BLACK),
    KING_HEART(CardOrderEnum.KING, CardSuiteEnum.HEART, CardColorEnum.RED),
    KING_DIAMOND(CardOrderEnum.KING, CardSuiteEnum.DIAMOND, CardColorEnum.RED),
    KING_CLUB(CardOrderEnum.KING, CardSuiteEnum.CLUB, CardColorEnum.BLACK),
    KING_SPADE(CardOrderEnum.KING, CardSuiteEnum.SPADE, CardColorEnum.BLACK);

    final CardOrderEnum cardOrderEnum;

    final CardSuiteEnum cardSuiteEnum;

    final CardColorEnum cardColorEnum;

    final String label;

    CardEnum(CardOrderEnum cardOrderEnum, CardSuiteEnum cardSuiteEnum, CardColorEnum cardColorEnum) {
        this.cardOrderEnum = cardOrderEnum;
        this.cardSuiteEnum = cardSuiteEnum;
        this.cardColorEnum = cardColorEnum;
        this.label = cardSuiteEnum.getLabel() + " " + cardOrderEnum.getLabel();
    }

    @Override
    public String toString() {
        return label;
    }
}
