package io.github.glandais.solitaire.klondike.enums;

import lombok.Getter;

@Getter
public enum TableauPilesEnum {
    TABLEAU_PILE_1(KlondikePilesEnum.TABLEAU_1, 0),
    TABLEAU_PILE_2(KlondikePilesEnum.TABLEAU_2, 1),
    TABLEAU_PILE_3(KlondikePilesEnum.TABLEAU_3, 2),
    TABLEAU_PILE_4(KlondikePilesEnum.TABLEAU_4, 3),
    TABLEAU_PILE_5(KlondikePilesEnum.TABLEAU_5, 4),
    TABLEAU_PILE_6(KlondikePilesEnum.TABLEAU_6, 5),
    TABLEAU_PILE_7(KlondikePilesEnum.TABLEAU_7, 6);

    private final KlondikePilesEnum klondikePilesEnum;
    private final int hiddenCards;

    TableauPilesEnum(KlondikePilesEnum klondikePilesEnum, int hiddenCards) {
        this.klondikePilesEnum = klondikePilesEnum;
        this.hiddenCards = hiddenCards;
    }
}
