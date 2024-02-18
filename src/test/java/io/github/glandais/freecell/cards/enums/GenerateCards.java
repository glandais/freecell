package io.github.glandais.freecell.cards.enums;

public class GenerateCards {

    public static void main(String[] args) {
        for (CardOrderEnum cardOrderEnum : CardOrderEnum.values()) {
            for (CardSuiteEnum cardSuiteEnum : CardSuiteEnum.values()) {
                System.out.println(cardOrderEnum.name() + "_" + cardSuiteEnum.name() + "(" +
                        "CardType." + cardOrderEnum.name() + "," +
                        "CardColor." + cardSuiteEnum.name() + "," +
                        "Color." + cardSuiteEnum.getCardColorEnum().name() +
                        "),");
            }
        }
    }

}
