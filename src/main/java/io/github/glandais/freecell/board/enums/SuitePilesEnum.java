package io.github.glandais.freecell.board.enums;

import io.github.glandais.freecell.cards.enums.CardSuiteEnum;
import lombok.Getter;

import static io.github.glandais.freecell.board.enums.PilesEnum.*;

@Getter
public enum SuitePilesEnum {
    HEART(SUITE_HEART, CardSuiteEnum.HEART),
    DIAMOND(SUITE_DIAMOND, CardSuiteEnum.DIAMOND),
    CLUB(SUITE_CLUB, CardSuiteEnum.CLUB),
    SPADE(SUITE_SPADE, CardSuiteEnum.SPADE);

    private final PilesEnum pilesEnum;
    private final CardSuiteEnum cardSuiteEnum;

    SuitePilesEnum(PilesEnum pilesEnum, CardSuiteEnum cardSuiteEnum) {
        this.pilesEnum = pilesEnum;
        this.cardSuiteEnum = cardSuiteEnum;
    }
}
