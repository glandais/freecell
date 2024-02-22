package io.github.glandais.solitaire.klondike.cards.enums;

import io.github.glandais.solitaire.klondike.Logger;

public class GenerateCards {

    public static void main(String[] args) {
        for (OrderEnum orderEnum : OrderEnum.values()) {
            for (ColorEnum colorEnum : ColorEnum.values()) {
                Logger.infoln(orderEnum.name() + "_" + colorEnum.name() + "(" +
                        "OrderEnum." + orderEnum.name() + "," +
                        "ColorEnum." + colorEnum.name() +
                        "),");

            }
        }

        Logger.infoln();
        Logger.infoln();

        for (OrderEnum orderEnum : OrderEnum.values()) {
            for (SuiteEnum suiteEnum : SuiteEnum.values()) {
                Logger.infoln(orderEnum.name() + "_" + suiteEnum.name() + "(" +
                        "OrderEnum." + orderEnum.name() + "," +
                        "SuiteEnum." + suiteEnum.name() + "," +
                        "ColorEnum." + suiteEnum.getColorEnum().name() + "," +
                        "StateCardEnum." + orderEnum.name() + "_" + suiteEnum.getColorEnum().name() +
                        "),");
            }
        }
    }

}
