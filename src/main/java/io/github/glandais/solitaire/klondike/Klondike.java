package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.board.Solitaire;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.cards.OrderEnum;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.PileTypeEnum;
import io.github.glandais.solitaire.klondike.enums.TableauPilesEnum;

import java.util.*;

public class Klondike implements Solitaire<KlondikePilesEnum> {

    public static final Klondike INSTANCE = new Klondike();

    @Override
    public Board<KlondikePilesEnum> getBoard(long seed) {
        SequencedMap<KlondikePilesEnum, Pile<KlondikePilesEnum>> piles = new LinkedHashMap<>();
        piles.put(KlondikePilesEnum.STOCK, new Pile<>(KlondikePilesEnum.STOCK));
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            piles.put(foundationPilesEnum.getKlondikePilesEnum(), new Pile<>(foundationPilesEnum.getKlondikePilesEnum()));
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            piles.put(tableauPilesEnum.getKlondikePilesEnum(), new Pile<>(tableauPilesEnum.getKlondikePilesEnum()));
        }
        Random random = new Random(seed);
        List<CardEnum> cardEnumList = new ArrayList<>(List.of(CardEnum.values()));
        Collections.shuffle(cardEnumList, random);
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile<KlondikePilesEnum> pile = piles.get(tableauPilesEnum.getKlondikePilesEnum());
            for (int i = 0; i < tableauPilesEnum.getHiddenCards(); i++) {
                CardEnum cardEnum = cardEnumList.removeLast();
                pile.hidden().add(cardEnum);
            }
            CardEnum cardEnum = cardEnumList.removeLast();
            pile.visible().add(cardEnum);
        }
        Pile<KlondikePilesEnum> pile = piles.get(KlondikePilesEnum.STOCK);
        pile.hidden().addAll(cardEnumList);
        return new Board<>(piles);
    }

    @Override
    public boolean isFinished(Board<KlondikePilesEnum> board) {
        synchronized (this) {
            Pile pile = board.getPile(KlondikePilesEnum.STOCK);
            if (!pile.hidden().isEmpty()) {
                return false;
            }
            for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
                pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
                if (!pile.hidden().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

    public int getScore(Board<KlondikePilesEnum> board) {
        int score = 0;
        if (!board.getPile(KlondikePilesEnum.STOCK).visible().isEmpty()) {
            score = score - 1_000_000;
        }
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            Pile<?> pile = board.getPile(foundationPilesEnum.getKlondikePilesEnum());
            score = score - pile.visible().size() * 10_000;
        }
        int kingsAtTop = 0;
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile<?> pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
            if (pile.hidden().isEmpty() && !pile.visible().isEmpty() && pile.visible().getFirst().getOrderEnum() == OrderEnum.KING) {
                kingsAtTop++;
            }
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile<?> pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
            List<CardEnum> hidden = pile.hidden();
            List<CardEnum> visible = pile.visible();
            int hiddenCount = hidden.size();
            int visibleCount = visible.size();
            if (hiddenCount == 0 && visibleCount > 0 && visible.getFirst().getOrderEnum() == OrderEnum.KING) {
                score = score - 100_000;
            }
            if (hiddenCount == 0 && visibleCount == 0 && kingsAtTop < 4) {
                score = score - 30_000;
            }
            score = score + hiddenCount;
            for (CardEnum cardEnum : visible) {
                score = score - cardEnum.getOrderEnum().getOrder();
            }
        }
        return score;
    }

    @Override
    public int getMovementScore(Movement<KlondikePilesEnum> movement, Board<KlondikePilesEnum> newBoard, Board<KlondikePilesEnum> oldBoard) {
        int score = 0;
        KlondikePilesEnum from = movement.from();
        KlondikePilesEnum to = movement.to();
        if (from == KlondikePilesEnum.STOCK && to == KlondikePilesEnum.STOCK) {
            // boring
            return 10;
        }
        if (from.getPileTypeEnum() == PileTypeEnum.TABLEAU &&
                newBoard.getPile(from).hidden().size() < oldBoard.getPile(from).hidden().size()) {
            // new card shown !
            score = -10000 - oldBoard.getPile(from).hidden().size();
        }
        return score;
    }

}
