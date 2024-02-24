package io.github.glandais.solitaire.klondike.enums;

import io.github.glandais.solitaire.common.cards.SuiteEnum;
import lombok.Getter;

@Getter
public enum FoundationPilesEnum {
    SPADE(KlondikePilesEnum.FOUNDATION_SPADE, SuiteEnum.SPADE),
    HEART(KlondikePilesEnum.FOUNDATION_HEART, SuiteEnum.HEART),
    DIAMOND(KlondikePilesEnum.FOUNDATION_DIAMOND, SuiteEnum.DIAMOND),
    CLUB(KlondikePilesEnum.FOUNDATION_CLUB, SuiteEnum.CLUB);

    private final KlondikePilesEnum klondikePilesEnum;
    private final SuiteEnum suiteEnum;

    FoundationPilesEnum(KlondikePilesEnum klondikePilesEnum, SuiteEnum suiteEnum) {
        this.klondikePilesEnum = klondikePilesEnum;
        this.suiteEnum = suiteEnum;
    }
}
