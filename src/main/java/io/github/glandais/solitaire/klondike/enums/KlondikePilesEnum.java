package io.github.glandais.solitaire.klondike.enums;

import io.github.glandais.solitaire.common.board.PileType;
import io.github.glandais.solitaire.common.board.PlayablePile;
import io.github.glandais.solitaire.common.cards.SuiteEnum;
import io.github.glandais.solitaire.klondike.FoundationPile;
import io.github.glandais.solitaire.klondike.StockPile;
import io.github.glandais.solitaire.klondike.TableauPile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KlondikePilesEnum implements PileType<KlondikePilesEnum> {
    STOCK(PileTypeEnum.STOCK, new StockPile(), false, "Stock"),
    FOUNDATION_HEART(PileTypeEnum.FOUNDATION, new FoundationPile(SuiteEnum.HEART), false, "♡"),
    FOUNDATION_DIAMOND(PileTypeEnum.FOUNDATION, new FoundationPile(SuiteEnum.DIAMOND), false, "♢"),
    FOUNDATION_CLUB(PileTypeEnum.FOUNDATION, new FoundationPile(SuiteEnum.CLUB), false, "♧"),
    FOUNDATION_SPADE(PileTypeEnum.FOUNDATION, new FoundationPile(SuiteEnum.SPADE), false, "♤"),
    TABLEAU_1(PileTypeEnum.TABLEAU, TableauPile.INSTANCE, true, "T1"),
    TABLEAU_2(PileTypeEnum.TABLEAU, TableauPile.INSTANCE, true, "T2"),
    TABLEAU_3(PileTypeEnum.TABLEAU, TableauPile.INSTANCE, true, "T3"),
    TABLEAU_4(PileTypeEnum.TABLEAU, TableauPile.INSTANCE, true, "T4"),
    TABLEAU_5(PileTypeEnum.TABLEAU, TableauPile.INSTANCE, true, "T5"),
    TABLEAU_6(PileTypeEnum.TABLEAU, TableauPile.INSTANCE, true, "T6"),
    TABLEAU_7(PileTypeEnum.TABLEAU, TableauPile.INSTANCE, true, "T7");

    @Getter
    final PileTypeEnum pileTypeEnum;
    final PlayablePile<KlondikePilesEnum> playablePile;
    final boolean swappable;
    final String label;

    @Override
    public String toString() {
        return label;
    }

    @Override
    public PlayablePile<KlondikePilesEnum> playablePile() {
        return playablePile;
    }

    @Override
    public boolean isSwappable() {
        return swappable;
    }

}
