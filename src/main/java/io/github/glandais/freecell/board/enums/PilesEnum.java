package io.github.glandais.freecell.board.enums;

import lombok.Getter;

public enum PilesEnum {
    STOCK(PileTypeEnum.STOCK, "Stock"),
    SUITE_HEART(PileTypeEnum.SUITE, "♡ suite"),
    SUITE_DIAMOND(PileTypeEnum.SUITE, "♢ suite"),
    SUITE_CLUB(PileTypeEnum.SUITE, "♧ suite"),
    SUITE_SPADE(PileTypeEnum.SUITE, "♤ suite"),
    TABLEAU_1(PileTypeEnum.TABLEAU, "Tableau 1"),
    TABLEAU_2(PileTypeEnum.TABLEAU, "Tableau 2"),
    TABLEAU_3(PileTypeEnum.TABLEAU, "Tableau 3"),
    TABLEAU_4(PileTypeEnum.TABLEAU, "Tableau 4"),
    TABLEAU_5(PileTypeEnum.TABLEAU, "Tableau 5"),
    TABLEAU_6(PileTypeEnum.TABLEAU, "Tableau 6"),
    TABLEAU_7(PileTypeEnum.TABLEAU, "Tableau 7");

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
