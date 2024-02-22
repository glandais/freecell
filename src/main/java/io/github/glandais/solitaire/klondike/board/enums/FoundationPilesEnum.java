package io.github.glandais.solitaire.klondike.board.enums;

import io.github.glandais.solitaire.klondike.cards.enums.SuiteEnum;
import lombok.Getter;

@Getter
public enum FoundationPilesEnum {
    HEART(PilesEnum.FOUNDATION_HEART, SuiteEnum.HEART),
    DIAMOND(PilesEnum.FOUNDATION_DIAMOND, SuiteEnum.DIAMOND),
    CLUB(PilesEnum.FOUNDATION_CLUB, SuiteEnum.CLUB),
    SPADE(PilesEnum.FOUNDATION_SPADE, SuiteEnum.SPADE);

    private final PilesEnum pilesEnum;
    private final SuiteEnum suiteEnum;

    FoundationPilesEnum(PilesEnum pilesEnum, SuiteEnum suiteEnum) {
        this.pilesEnum = pilesEnum;
        this.suiteEnum = suiteEnum;
    }
}
