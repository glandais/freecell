package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Cards;
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
import lombok.experimental.UtilityClass;

import static io.github.glandais.solitaire.common.board.Solitaire.ERASE_OTHER_MOVEMENTS;
import static io.github.glandais.solitaire.common.board.Solitaire.UNSOLVED;

@UtilityClass
public class ScoreCard {

    public static final boolean DEBUG = false;

    public ScoreCardResult getScore(Klondike klondike, Board<KlondikePilesEnum> board, Movement<KlondikePilesEnum> movement) {
        boolean finished = klondike.movesToFinish(board) < UNSOLVED;
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        boolean noStockVisibleAndCanPick = stock.visible().isEmpty() && !stock.hidden().isEmpty();

        int minFoundation = 100;
        int maxFoundation = 0;
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

        int kingSuiteNoHidden = 0;
        int kingSuiteOnHidden = 0;
        int emptyTableau = 0;
        int[] hiddenCount = new int[7];
        int[] visibleCount = new int[7];
//        CardEnum[] suiteStart = new CardEnum[7];
//        CardEnum[] suiteEnd = new CardEnum[7];
        int[] suiteColor = new int[7];
        int i = 0;
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile<?> pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
            Cards visible = pile.visible();
            Cards hidden = pile.hidden();
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
//            if (!visibleEmpty) {
//                suiteStart[i] = visible.getFirst();
//                suiteEnd[i] = visible.getLast();
//            }

            SuiteEnum lastBlack = null;
            SuiteEnum lastRed = null;
            int tableauSuiteColor = 0;
            for (int j = visible.size() - 1; j >= 0; j--) {
                CardEnum cardEnum = visible.get(j);
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

        String debug = "";
        int score = 0;
        if (finished) {
            if (DEBUG) {
                debug = debug + "\n" + "-100_000_000 as finised";
            }
            return new ScoreCardResult(-100_000_000, debug);
        }
        if (noStockVisibleAndCanPick) {
            if (DEBUG) {
                debug = debug + "\n" + "noStockVisibleAndCanPick : +10_000_000";
            }
            // don't do that !
            score = score + 10_000_000;
        }

        // 750_000 for foundations

        if (maxFoundation > 0) {
            if (maxFoundation - minFoundation <= 2) {
                if (movement.getTo().getPileTypeEnum() == PileTypeEnum.FOUNDATION) {
                    return new ScoreCardResult(ERASE_OTHER_MOVEMENTS, debug);
                }
                if (DEBUG) {
                    debug = debug + "\n" + "foundations : -50_000";
                }
                score = score - 50_000;
            } else {
                if (DEBUG) {
                    debug = debug + "\n" + "foundations : -" + 10_000 + " (" + minFoundation + "/" + maxFoundation + ")";
                }
                score = score - 10_000;
            }
        }

        // 400_000 for kings with no hidden

        // I love kings at top of a tableau
        if (DEBUG) {
            debug = debug + "\n" + "kingSuiteNoHidden : -" + kingSuiteNoHidden + " * 100_000";
        }
        score = score - 100_000 * kingSuiteNoHidden;
        int movableKings = Math.min(emptyTableau, kingSuiteOnHidden);
        // I love to move kings at top of a tableau
        if (DEBUG) {
            debug = debug + "\n" + "movableKings : -" + movableKings + " * 90_000";
        }
        score = score - 90_000 * movableKings;

        int hiddenTotal = 0;
        int visibleTotal = 0;
        int suiteColorTotal = 0;
        for (int j = 0; j < 7; j++) {
            hiddenTotal = hiddenTotal + hiddenCount[j];
            visibleTotal = visibleTotal + visibleCount[j];
            suiteColorTotal = suiteColorTotal + (suiteColor[j] > 2 ? suiteColor[j] : 0);
        }
        if (DEBUG) {
            debug = debug + "\n" + "hiddenTotal : +" + hiddenTotal + " * 50_000";
        }
        score = score + hiddenTotal * 50_000;
        if (movement.getTo().getPileTypeEnum() == PileTypeEnum.TABLEAU) {
            int firstStackOrder = movement.getCards().getFirst().getOrderEnum().getOrder();
            if (DEBUG) {
                debug = debug + "\n" + "firstStackOrder : -" + firstStackOrder + " * 1000";
            }
            score = score - firstStackOrder * 1000;
        }
        if (DEBUG) {
            debug = debug + "\n" + "visibleTotal : -" + visibleTotal + " * 100";
        }
        score = score - visibleTotal * 100;
        if (DEBUG) {
            debug = debug + "\n" + "suiteColorTotal : -" + suiteColorTotal + " * 10";
        }
        score = score - suiteColorTotal * 100;
        if (DEBUG) {
            debug = debug + "\n" + "total : " + score;
        }
        return new ScoreCardResult(score, debug);
    }

}
