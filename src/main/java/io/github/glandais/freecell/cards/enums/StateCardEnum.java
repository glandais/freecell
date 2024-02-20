package io.github.glandais.freecell.cards.enums;

public enum StateCardEnum {
    ACE_BLACK(CardOrderEnum.ACE,CardColorEnum.BLACK),
    ACE_RED(CardOrderEnum.ACE,CardColorEnum.RED),
    TWO_BLACK(CardOrderEnum.TWO,CardColorEnum.BLACK),
    TWO_RED(CardOrderEnum.TWO,CardColorEnum.RED),
    THREE_BLACK(CardOrderEnum.THREE,CardColorEnum.BLACK),
    THREE_RED(CardOrderEnum.THREE,CardColorEnum.RED),
    FOUR_BLACK(CardOrderEnum.FOUR,CardColorEnum.BLACK),
    FOUR_RED(CardOrderEnum.FOUR,CardColorEnum.RED),
    FIVE_BLACK(CardOrderEnum.FIVE,CardColorEnum.BLACK),
    FIVE_RED(CardOrderEnum.FIVE,CardColorEnum.RED),
    SIX_BLACK(CardOrderEnum.SIX,CardColorEnum.BLACK),
    SIX_RED(CardOrderEnum.SIX,CardColorEnum.RED),
    SEVEN_BLACK(CardOrderEnum.SEVEN,CardColorEnum.BLACK),
    SEVEN_RED(CardOrderEnum.SEVEN,CardColorEnum.RED),
    HEIGHT_BLACK(CardOrderEnum.HEIGHT,CardColorEnum.BLACK),
    HEIGHT_RED(CardOrderEnum.HEIGHT,CardColorEnum.RED),
    NINE_BLACK(CardOrderEnum.NINE,CardColorEnum.BLACK),
    NINE_RED(CardOrderEnum.NINE,CardColorEnum.RED),
    TEN_BLACK(CardOrderEnum.TEN,CardColorEnum.BLACK),
    TEN_RED(CardOrderEnum.TEN,CardColorEnum.RED),
    JACK_BLACK(CardOrderEnum.JACK,CardColorEnum.BLACK),
    JACK_RED(CardOrderEnum.JACK,CardColorEnum.RED),
    QUEEN_BLACK(CardOrderEnum.QUEEN,CardColorEnum.BLACK),
    QUEEN_RED(CardOrderEnum.QUEEN,CardColorEnum.RED),
    KING_BLACK(CardOrderEnum.KING,CardColorEnum.BLACK),
    KING_RED(CardOrderEnum.KING,CardColorEnum.RED);

    final CardOrderEnum order;

    final CardColorEnum color;

    StateCardEnum(CardOrderEnum order, CardColorEnum color) {
        this.order = order;
        this.color = color;
    }
}
