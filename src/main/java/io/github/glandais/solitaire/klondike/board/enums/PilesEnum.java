package io.github.glandais.solitaire.klondike.board.enums;

import lombok.Getter;

public enum PilesEnum {
    STOCK(PileTypeEnum.STOCK, "Stock"),
    FOUNDATION_HEART(PileTypeEnum.FOUNDATION, "♡"),
    FOUNDATION_DIAMOND(PileTypeEnum.FOUNDATION, "♢"),
    FOUNDATION_CLUB(PileTypeEnum.FOUNDATION, "♧"),
    FOUNDATION_SPADE(PileTypeEnum.FOUNDATION, "♤"),
    TABLEAU_1(PileTypeEnum.TABLEAU, "T1"),
    TABLEAU_2(PileTypeEnum.TABLEAU, "T2"),
    TABLEAU_3(PileTypeEnum.TABLEAU, "T3"),
    TABLEAU_4(PileTypeEnum.TABLEAU, "T4"),
    TABLEAU_5(PileTypeEnum.TABLEAU, "T5"),
    TABLEAU_6(PileTypeEnum.TABLEAU, "T6"),
    TABLEAU_7(PileTypeEnum.TABLEAU, "T7");

    @Getter
    final PileTypeEnum pileTypeEnum;
    final String label;

    PilesEnum(PileTypeEnum pileTypeEnum, String label) {
        this.pileTypeEnum = pileTypeEnum;
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
