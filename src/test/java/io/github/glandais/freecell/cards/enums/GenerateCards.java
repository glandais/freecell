package io.github.glandais.freecell.cards.enums;

import io.github.glandais.freecell.Logger;

public class GenerateCards {

    public static void main(String[] args) {
        for (CardOrderEnum cardOrderEnum : CardOrderEnum.values()) {
            for (CardColorEnum cardColorEnum : CardColorEnum.values()) {
                Logger.infoln(cardOrderEnum.name() + "_" + cardColorEnum.name() + "(" +
                        "CardOrderEnum." + cardOrderEnum.name() + "," +
                        "CardColorEnum." + cardColorEnum.name() +
                        "),");

            }
        }

        Logger.infoln();
        Logger.infoln();

        for (CardOrderEnum cardOrderEnum : CardOrderEnum.values()) {
            for (CardSuiteEnum cardSuiteEnum : CardSuiteEnum.values()) {
                Logger.infoln(cardOrderEnum.name() + "_" + cardSuiteEnum.name() + "(" +
                        "CardOrderEnum." + cardOrderEnum.name() + "," +
                        "CardSuiteEnum." + cardSuiteEnum.name() + "," +
                        "CardColorEnum." + cardSuiteEnum.getCardColorEnum().name() + "," +
                        "StateCardEnum." + cardOrderEnum.name() + "_" + cardSuiteEnum.getCardColorEnum().name() +
                        "),");
            }
        }
    }

}
