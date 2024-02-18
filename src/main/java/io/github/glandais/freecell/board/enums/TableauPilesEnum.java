package io.github.glandais.freecell.board.enums;

import lombok.Getter;

@Getter
public enum TableauPilesEnum {
    _1(PilesEnum.TABLEAU_1, 0),
    _2(PilesEnum.TABLEAU_2, 1),
    _3(PilesEnum.TABLEAU_3, 2),
    _4(PilesEnum.TABLEAU_4, 3),
    _5(PilesEnum.TABLEAU_5, 4),
    _6(PilesEnum.TABLEAU_6, 5),
    _7(PilesEnum.TABLEAU_7, 6);

    private final PilesEnum pilesEnum;
    private final int hiddenCards;

    TableauPilesEnum(PilesEnum pilesEnum, int hiddenCards) {
        this.pilesEnum = pilesEnum;
        this.hiddenCards = hiddenCards;
    }
}
