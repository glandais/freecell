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
            Pile stock = board.getPile(KlondikePilesEnum.STOCK);
            if (!stock.hidden().isEmpty()) {
                return UNSOLVED;
            }
            if (!stock.visible().isEmpty()) {
                return UNSOLVED;
            }
            for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
                stock = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
                if (!stock.hidden().isEmpty()) {
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
    public List<MovementScore<KlondikePilesEnum>> getFinishMovements(Board<KlondikePilesEnum> boardParam) {
        Board<KlondikePilesEnum> board = boardParam.copy();
        List<MovementScore<KlondikePilesEnum>> movements = new ArrayList<>();
        List<MovementScore<KlondikePilesEnum>> possibleMovements;
        do {
            possibleMovements = getOrderedMovements(board);
            if (!possibleMovements.isEmpty()) {
                MovementScore<KlondikePilesEnum> movement = possibleMovements.getFirst();
                movements.add(movement);
                board.applyMovement(movement);
            }
        } while (!possibleMovements.isEmpty());
        return movements;
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
            return possibleMovements.stream().map(m -> new MovementScore<>(m, 0, null)).collect(Collectors.toList());
        }
        return possibleMovements.stream().map(m -> getMovementScoreWithBoardScore(board, m)).collect(Collectors.toList());
//        return getMovementScores2(board, possibleMovements);
    }

    private MovementScore<KlondikePilesEnum> getMovementScoreWithBoardScore(Board<KlondikePilesEnum> board, Movement<KlondikePilesEnum> movement) {
        List<CardAction<KlondikePilesEnum>> actions = board.applyMovement(movement);
        if (Logger.DEBUG) {
            Logger.infoln(movement);
        }
        MovementScore<KlondikePilesEnum> movementScore = getScore(movement, board);
        board.revertMovement(actions);
        return movementScore;
    }

    public MovementScore<KlondikePilesEnum> getScore(Movement<KlondikePilesEnum> movement, Board<KlondikePilesEnum> board) {
        ScoreCard scoreCard = new ScoreCard(this, board, movement);

        int score = scoreCard.getScore();
        if (Logger.DEBUG) {
            Logger.infoln(score + " " + scoreCard.getDebug());
        }
        return new MovementScore<>(movement, score, scoreCard.getDebug());
    }

    @Override
    public Object getBoardMoves(Board<KlondikePilesEnum> board, List<MovementScore<KlondikePilesEnum>> moves) {
        return new BoardMoves(board, moves);
    }

}
