package io.github.glandais.freecell.cards.enums;

public class GenerateCards {

    public static void main(String[] args) {
        for (CardOrderEnum cardOrderEnum : CardOrderEnum.values()) {
            for (CardColorEnum cardColorEnum : CardColorEnum.values()) {
                System.out.println(cardOrderEnum.name() + "_" + cardColorEnum.name() + "(" +
                        "CardOrderEnum." + cardOrderEnum.name() + "," +
                        "CardColorEnum." + cardColorEnum.name() +
                        "),");

            }
        }

        System.out.println();
        System.out.println();

        for (CardOrderEnum cardOrderEnum : CardOrderEnum.values()) {
            for (CardSuiteEnum cardSuiteEnum : CardSuiteEnum.values()) {
                System.out.println(cardOrderEnum.name() + "_" + cardSuiteEnum.name() + "(" +
                        "CardOrderEnum." + cardOrderEnum.name() + "," +
                        "CardSuiteEnum." + cardSuiteEnum.name() + "," +
                        "CardColorEnum." + cardSuiteEnum.getCardColorEnum().name() + "," +
                        "StateCardEnum." + cardOrderEnum.name() + "_" + cardSuiteEnum.getCardColorEnum().name() +
                        "),");
            }
        }
    }

}
