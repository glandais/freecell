package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.PileTypeEnum;
import lombok.Data;

import static io.github.glandais.solitaire.common.board.Solitaire.ERASE_OTHER_MOVEMENTS;

@Data
public class ScoreCard {

    boolean finished;

    boolean noStockVisibleAndCanPick;

    int minFoundation;
    int maxFoundation;

    int kingSuiteNoHidden;
    int kingSuiteOnHidden;
    int emptyTableau;

    int[] hiddenCount;
    int[] visibleCount;
    CardEnum[] suiteStart;
    CardEnum[] suiteEnd;
    int[] suiteColor;

    public int getScore(Movement<KlondikePilesEnum> movement) {
        if (finished) {
            if (Logger.DEBUG) Logger.infoln("-100_000_000 as finised");
            return -100_000_000;
        }
        int score = 0;
        if (noStockVisibleAndCanPick) {
            if (Logger.DEBUG) Logger.infoln("noStockVisibleAndCanPick : +10_000_000");
            // don't do that !
            score = score + 10_000_000;
        }

        // 750_000 for foundations

        if (maxFoundation > 0) {
            if (maxFoundation - minFoundation <= 2) {
                if (movement != null && movement.getTo().getPileTypeEnum() == PileTypeEnum.FOUNDATION) {
                    return ERASE_OTHER_MOVEMENTS;
                }
                if (Logger.DEBUG) Logger.infoln("foundations : -50_000");
                score = score - 50_000;
            } else {
                if (Logger.DEBUG)
                    Logger.infoln("foundations : -" + 10_000 + " (" + minFoundation + "/" + maxFoundation + ")");
                score = score - 10_000;
            }
        }

        // 400_000 for kings with no hidden

        // I love kings at top of a tableau
        if (Logger.DEBUG) Logger.infoln("kingSuiteNoHidden : -" + kingSuiteNoHidden + " * 100_000");
        score = score - 100_000 * kingSuiteNoHidden;
        int movableKings = Math.min(emptyTableau, kingSuiteOnHidden);
        // I love to move kings at top of a tableau
        if (Logger.DEBUG) Logger.infoln("movableKings : -" + movableKings + " * 90_000");
        score = score - 90_000 * movableKings;

        int hiddenTotal = 0;
        int visibleTotal = 0;
        int suiteColorTotal = 0;
        for (int i = 0; i < 7; i++) {
            hiddenTotal = hiddenTotal + hiddenCount[i];
            visibleTotal = visibleTotal + visibleCount[i];
            suiteColorTotal = suiteColorTotal + (suiteColor[i] > 2 ? suiteColor[i] : 0);
        }
        if (Logger.DEBUG) Logger.infoln("hiddenTotal : +" + hiddenTotal + " * 50_000");
        score = score + hiddenTotal * 50_000;
        if (movement != null) {
            int firstStackOrder = movement.getCards().getFirst().getOrderEnum().getOrder();
            if (Logger.DEBUG) Logger.infoln("firstStackOrder : -" + firstStackOrder + " * 1000");
            score = score - firstStackOrder * 1000;
        }
        if (Logger.DEBUG) Logger.infoln("visibleTotal : -" + visibleTotal + " * 100");
        score = score - visibleTotal * 100;
        if (Logger.DEBUG) Logger.infoln("suiteColorTotal : -" + suiteColorTotal + " * 10");
        score = score - suiteColorTotal * 100;
        if (Logger.DEBUG) Logger.infoln("total : " + score);
        return score;
    }

}
