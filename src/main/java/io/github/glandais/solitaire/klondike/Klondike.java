package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.board.Solitaire;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.cards.OrderEnum;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.klondike.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.PileTypeEnum;
import io.github.glandais.solitaire.klondike.enums.TableauPilesEnum;

import java.util.*;
import java.util.stream.Collectors;

public class Klondike implements Solitaire<KlondikePilesEnum> {

    public static final Klondike INSTANCE = new Klondike();
    public static final int UNSOLVED = 10000;

    @Override
    public Board<KlondikePilesEnum> getBoard(long seed) {
        SequencedMap<KlondikePilesEnum, Pile<KlondikePilesEnum>> piles = new LinkedHashMap<>();
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            piles.put(foundationPilesEnum.getKlondikePilesEnum(), new Pile<>(foundationPilesEnum.getKlondikePilesEnum()));
        }
        piles.put(KlondikePilesEnum.STOCK, new Pile<>(KlondikePilesEnum.STOCK));
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
    public int movesToFinish(Board<KlondikePilesEnum> board) {
        synchronized (this) {
            Pile pile = board.getPile(KlondikePilesEnum.STOCK);
            if (!pile.hidden().isEmpty()) {
                return UNSOLVED;
            }
            for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
                pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
                if (!pile.hidden().isEmpty()) {
                    return UNSOLVED;
                }
            }
            return Arrays.stream(TableauPilesEnum.values())
                    .map(TableauPilesEnum::getKlondikePilesEnum)
                    .map(board::getPile)
                    .map(Pile::visible)
                    .mapToInt(List::size)
                    .sum();
        }
    }

    @Override
    public List<MovementScore<KlondikePilesEnum>> getMovementScores(Board<KlondikePilesEnum> board, List<Movement<KlondikePilesEnum>> possibleMovements) {
        return possibleMovements.stream().map(m -> getMovementScoreWithBoardScore(board, m)).collect(Collectors.toList());
//        return getMovementScores2(board, possibleMovements);
    }

    private MovementScore<KlondikePilesEnum> getMovementScoreWithBoardScore(Board<KlondikePilesEnum> board, Movement<KlondikePilesEnum> movement) {
        List<CardAction<KlondikePilesEnum>> actions = board.applyMovement(movement);
        int score = getScore(board);
        board.revertMovement(actions);
        return new MovementScore<>(movement, score);
    }

    @Override
    public int getScore(Board<KlondikePilesEnum> board) {
        if (movesToFinish(board) < UNSOLVED) {
            return -100_000_000;
        }
        int score = 0;
        if (!board.getPile(KlondikePilesEnum.STOCK).visible().isEmpty()) {
            score = score - 1_000_000;
        }

        int minFoundation = 100;
        int maxFoundation = 0;
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            Pile<KlondikePilesEnum> pile = board.getPile(foundationPilesEnum.getKlondikePilesEnum());
            if (!pile.visible().isEmpty()) {
                int order = pile.visible().getLast().getOrderEnum().getOrder();
                minFoundation = Math.min(minFoundation, order);
                maxFoundation = Math.max(maxFoundation, order);
            }
        }
        if (maxFoundation > 0) {
            if (maxFoundation - minFoundation <= 2) {
                score = score - maxFoundation * 50_000;
            } else {
                score = score - maxFoundation * 1_000;
            }
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


    private List<MovementScore<KlondikePilesEnum>> getMovementScores2(Board<KlondikePilesEnum> board, List<Movement<KlondikePilesEnum>> possibleMovements) {
        List<MovementScore<KlondikePilesEnum>> movementScores = new ArrayList<>();
        for (Movement<KlondikePilesEnum> possibleMovement : possibleMovements) {
            movementScores.add(new MovementScore<>(possibleMovement, getMovementScore(board, possibleMovement, possibleMovements)));
        }
        return movementScores;
    }


    public int getMovementScore(Board<KlondikePilesEnum> board, Movement<KlondikePilesEnum> possibleMovement, List<Movement<KlondikePilesEnum>> possibleMovements) {
        KlondikePilesEnum from = possibleMovement.from();
        KlondikePilesEnum to = possibleMovement.to();
        if (from == KlondikePilesEnum.STOCK && to == KlondikePilesEnum.STOCK) {
            if (possibleMovement.cards().isEmpty()) {
                // always pick up card
                return -1_000_000;
            }
            // opening action if nothing better
            return -1;
        }
        if (to.getPileTypeEnum() == PileTypeEnum.FOUNDATION) {
            int maxFoundationOther = 0;
            for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
                if (foundationPilesEnum.getKlondikePilesEnum() != to) {
                    Pile<KlondikePilesEnum> pile = board.getPile(foundationPilesEnum.getKlondikePilesEnum());
                    if (!pile.visible().isEmpty()) {
                        maxFoundationOther = Math.max(maxFoundationOther, pile.visible().getLast().getOrderEnum().getOrder());
                    }
                }
            }
            int order = possibleMovement.cards().getFirst().getOrderEnum().getOrder();
            if (order <= maxFoundationOther + 2) {
                // move to foundation if max - min <= 2 in foundations
                return -500_000;
            } else {
                // do it later
                return 1000;
            }
        }
        int stockMalus = 0;
        // to == TABLEAU_X
        if (from == KlondikePilesEnum.STOCK) {
            stockMalus = 500;
        } else {
            if (board.getPile(from).hidden().isEmpty()) {
                // should we free the space ?
                // only if a king is ready !
                for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
                    KlondikePilesEnum otherPile = tableauPilesEnum.getKlondikePilesEnum();
                    if (!board.getPile(otherPile).hidden().isEmpty() &&
                            !board.getPile(otherPile).visible().isEmpty() &&
                            board.getPile(otherPile).visible().getFirst().getOrderEnum() == OrderEnum.KING) {
                        return -400_000;
                    }
                }
                // do it later
                return 999;
            }
        }
        int firstOrder = possibleMovement.cards().getFirst().getOrderEnum().getOrder();
        // FIXME boost same colors
        return -300_000 - firstOrder + stockMalus;
    }

}
