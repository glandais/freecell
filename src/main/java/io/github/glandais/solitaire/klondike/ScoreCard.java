package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.cards.ColorEnum;
import io.github.glandais.solitaire.common.cards.OrderEnum;
import io.github.glandais.solitaire.common.cards.SuiteEnum;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.PileTypeEnum;
import io.github.glandais.solitaire.klondike.enums.TableauPilesEnum;
import lombok.Getter;

import java.util.List;

import static io.github.glandais.solitaire.common.board.Solitaire.ERASE_OTHER_MOVEMENTS;
import static io.github.glandais.solitaire.common.board.Solitaire.UNSOLVED;

public class ScoreCard {

    public static final boolean DEBUG = false;

    final Movement<KlondikePilesEnum> movement;

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

    @Getter
    int score;
    @Getter
    String debug;

    public ScoreCard(Klondike klondike, Board<KlondikePilesEnum> board, Movement<KlondikePilesEnum> movement) {
        this.movement = movement;
        finished = klondike.movesToFinish(board) < UNSOLVED;
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        noStockVisibleAndCanPick = stock.visible().isEmpty() && !stock.hidden().isEmpty();

        minFoundation = 100;
        maxFoundation = 0;
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            Pile<KlondikePilesEnum> pile = board.getPile(foundationPilesEnum.getKlondikePilesEnum());
            if (!pile.visible().isEmpty()) {
                int order = pile.visible().getLast().getOrderEnum().getOrder();
                minFoundation = Math.min(minFoundation, order);
                maxFoundation = Math.max(maxFoundation, order);
            } else {
                minFoundation = 0;
            }
        }
        if (minFoundation == 100) {
            minFoundation = 0;
        }

        kingSuiteNoHidden = 0;
        kingSuiteOnHidden = 0;
        emptyTableau = 0;
        hiddenCount = new int[7];
        visibleCount = new int[7];
        suiteStart = new CardEnum[7];
        suiteEnd = new CardEnum[7];
        suiteColor = new int[7];
        int i = 0;
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile<?> pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
            List<CardEnum> visible = pile.visible();
            List<CardEnum> hidden = pile.hidden();
            boolean visibleEmpty = visible.isEmpty();
            boolean hiddenEmpty = hidden.isEmpty();
            if (!visibleEmpty && visible.getFirst().getOrderEnum() == OrderEnum.KING) {
                if (hiddenEmpty) {
                    kingSuiteNoHidden++;
                } else {
                    kingSuiteOnHidden++;
                }
            }
            if (hiddenEmpty && visibleEmpty) {
                emptyTableau++;
            }
            hiddenCount[i] = hidden.size();
            visibleCount[i] = visible.size();
            if (!visibleEmpty) {
                suiteStart[i] = visible.getFirst();
                suiteEnd[i] = visible.getLast();
            }

            SuiteEnum lastBlack = null;
            SuiteEnum lastRed = null;
            int tableauSuiteColor = 0;
            for (CardEnum cardEnum : visible.reversed()) {
                SuiteEnum suiteEnum = cardEnum.getSuiteEnum();
                if (suiteEnum.getColorEnum() == ColorEnum.BLACK) {
                    if (lastBlack == null) {
                        lastBlack = suiteEnum;
                    } else if (lastBlack != suiteEnum) {
                        break;
                    }
                } else {
                    if (lastRed == null) {
                        lastRed = suiteEnum;
                    } else if (lastRed != suiteEnum) {
                        break;
                    }
                }
                tableauSuiteColor++;
            }
            suiteColor[i] = tableauSuiteColor;

            i++;
        }

        computeScore();
    }

    private void computeScore() {
        debug = "";
        if (finished) {
            if (DEBUG) debug = debug + "\n" + "-100_000_000 as finised";
            score = -100_000_000;
            return;
        }
        score = 0;
        if (noStockVisibleAndCanPick) {
            if (DEBUG) debug = debug + "\n" + "noStockVisibleAndCanPick : +10_000_000";
            // don't do that !
            score = score + 10_000_000;
        }

        // 750_000 for foundations

        if (maxFoundation > 0) {
            if (maxFoundation - minFoundation <= 2) {
                if (movement != null && movement.getTo().getPileTypeEnum() == PileTypeEnum.FOUNDATION) {
                    score = ERASE_OTHER_MOVEMENTS;
                    return;
                }
                if (DEBUG) debug = debug + "\n" + "foundations : -50_000";
                score = score - 50_000;
            } else {
                if (DEBUG)
                    debug = debug + "\n" + "foundations : -" + 10_000 + " (" + minFoundation + "/" + maxFoundation + ")";
                score = score - 10_000;
            }
        }

        // 400_000 for kings with no hidden

        // I love kings at top of a tableau
        if (DEBUG) debug = debug + "\n" + "kingSuiteNoHidden : -" + kingSuiteNoHidden + " * 100_000";
        score = score - 100_000 * kingSuiteNoHidden;
        int movableKings = Math.min(emptyTableau, kingSuiteOnHidden);
        // I love to move kings at top of a tableau
        if (DEBUG) debug = debug + "\n" + "movableKings : -" + movableKings + " * 90_000";
        score = score - 90_000 * movableKings;

        int hiddenTotal = 0;
        int visibleTotal = 0;
        int suiteColorTotal = 0;
        for (int i = 0; i < 7; i++) {
            hiddenTotal = hiddenTotal + hiddenCount[i];
            visibleTotal = visibleTotal + visibleCount[i];
            suiteColorTotal = suiteColorTotal + (suiteColor[i] > 2 ? suiteColor[i] : 0);
        }
        if (DEBUG) debug = debug + "\n" + "hiddenTotal : +" + hiddenTotal + " * 50_000";
        score = score + hiddenTotal * 50_000;
        if (movement != null && movement.getTo().getPileTypeEnum() == PileTypeEnum.TABLEAU) {
            int firstStackOrder = movement.getCards().getFirst().getOrderEnum().getOrder();
            if (DEBUG) debug = debug + "\n" + "firstStackOrder : -" + firstStackOrder + " * 1000";
            score = score - firstStackOrder * 1000;
        }
        if (DEBUG) debug = debug + "\n" + "visibleTotal : -" + visibleTotal + " * 100";
        score = score - visibleTotal * 100;
        if (DEBUG) debug = debug + "\n" + "suiteColorTotal : -" + suiteColorTotal + " * 10";
        score = score - suiteColorTotal * 100;
        if (DEBUG) debug = debug + "\n" + "total : " + score;
    }

}
