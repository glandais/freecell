package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.board.Solitaire;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.cards.ColorEnum;
import io.github.glandais.solitaire.common.cards.OrderEnum;
import io.github.glandais.solitaire.common.cards.SuiteEnum;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.klondike.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.PileTypeEnum;
import io.github.glandais.solitaire.klondike.enums.TableauPilesEnum;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;

import java.util.*;
import java.util.stream.Collectors;

public class Klondike implements Solitaire<KlondikePilesEnum> {

    public static final Klondike INSTANCE = new Klondike();

    @Override
    public Board<KlondikePilesEnum> getBoard(long seed) {
        Logger.infoln("Board seed : " + seed);
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
        // filter useless movements
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        if (stock.visible().isEmpty() && !stock.hidden().isEmpty()) {
            possibleMovements = possibleMovements.stream()
                    .filter(m -> m.getFrom() == KlondikePilesEnum.STOCK && m.getTo() == KlondikePilesEnum.STOCK)
                    .collect(Collectors.toList());
        }
        if (possibleMovements.size() <= 1) {
            return possibleMovements.stream().map(m -> new MovementScore<>(m, 0)).collect(Collectors.toList());
        }
        return possibleMovements.stream().map(m -> getMovementScoreWithBoardScore(board, m)).collect(Collectors.toList());
//        return getMovementScores2(board, possibleMovements);
    }

    private MovementScore<KlondikePilesEnum> getMovementScoreWithBoardScore(Board<KlondikePilesEnum> board, Movement<KlondikePilesEnum> movement) {
        List<CardAction<KlondikePilesEnum>> actions = board.applyMovement(movement);
        if (Logger.DEBUG) {
            Logger.infoln(movement);
        }
        int score = getScore(movement, board);
        board.revertMovement(actions);
        return new MovementScore<>(movement, score);
    }

    @Override
    public int getScore(Movement<KlondikePilesEnum> movement, Board<KlondikePilesEnum> board) {
        ScoreCard scoreCard = new ScoreCard();
        scoreCard.setFinished(movesToFinish(board) < UNSOLVED);
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        scoreCard.setNoStockVisibleAndCanPick(
                stock.visible().isEmpty() && !stock.hidden().isEmpty()
        );

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
        scoreCard.setMinFoundation(minFoundation);
        scoreCard.setMaxFoundation(maxFoundation);

        int kingSuiteNoHidden = 0;
        int kingSuiteOnHidden = 0;
        int emptyTableau = 0;
        int[] hiddenCount = new int[7];
        int[] visibleCount = new int[7];
        CardEnum[] suiteStart = new CardEnum[7];
        CardEnum[] suiteEnd = new CardEnum[7];
        int[] suiteColor = new int[7];
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
        scoreCard.setKingSuiteNoHidden(kingSuiteNoHidden);
        scoreCard.setKingSuiteOnHidden(kingSuiteOnHidden);
        scoreCard.setEmptyTableau(emptyTableau);
        scoreCard.setHiddenCount(hiddenCount);
        scoreCard.setVisibleCount(visibleCount);
        scoreCard.setSuiteStart(suiteStart);
        scoreCard.setSuiteEnd(suiteEnd);
        scoreCard.setSuiteColor(suiteColor);

        int score = scoreCard.getScore(movement);
        if (Logger.DEBUG) {
            Logger.infoln(score + " " + scoreCard);
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
        KlondikePilesEnum from = possibleMovement.getFrom();
        KlondikePilesEnum to = possibleMovement.getTo();
        if (from == KlondikePilesEnum.STOCK && to == KlondikePilesEnum.STOCK) {
            if (possibleMovement.getCards().isEmpty()) {
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
            int order = possibleMovement.getCards().getFirst().getOrderEnum().getOrder();
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
        int firstOrder = possibleMovement.getCards().getFirst().getOrderEnum().getOrder();
        // FIXME boost same colors
        return -300_000 - firstOrder + stockMalus;
    }

    @Override
    public List<MovementScore<KlondikePilesEnum>> getFinishMovements(Board<KlondikePilesEnum> boardParam) {
        Board<KlondikePilesEnum> board = boardParam.copy();
        List<MovementScore<KlondikePilesEnum>> movements = new ArrayList<>();
        List<Movement<KlondikePilesEnum>> possibleMovements;
        do {
            possibleMovements = board.computePossibleMovements();
            if (!possibleMovements.isEmpty()) {
                Movement<KlondikePilesEnum> movement = possibleMovements.getFirst();
                movements.add(new MovementScore<>(movement, 0));
                board.applyMovement(movement);
            }
        } while (!possibleMovements.isEmpty());
        return movements;
    }

    @Override
    public Object getBoardMoves(Board<KlondikePilesEnum> board, List<MovementScore<KlondikePilesEnum>> moves) {
        return new BoardMoves(board, moves);
    }

}
