package io.github.glandais.solitaire.klondike.board.enums;

import lombok.Getter;

@Getter
public enum TableauPilesEnum {
    TABLEAU_PILE_1(PilesEnum.TABLEAU_1, 0),
    TABLEAU_PILE_2(PilesEnum.TABLEAU_2, 1),
    TABLEAU_PILE_3(PilesEnum.TABLEAU_3, 2),
    TABLEAU_PILE_4(PilesEnum.TABLEAU_4, 3),
    TABLEAU_PILE_5(PilesEnum.TABLEAU_5, 4),
    TABLEAU_PILE_6(PilesEnum.TABLEAU_6, 5),
    TABLEAU_PILE_7(PilesEnum.TABLEAU_7, 6);

    private final PilesEnum pilesEnum;
    private final int hiddenCards;

    TableauPilesEnum(PilesEnum pilesEnum, int hiddenCards) {
        this.pilesEnum = pilesEnum;
        this.hiddenCards = hiddenCards;
    }
}
