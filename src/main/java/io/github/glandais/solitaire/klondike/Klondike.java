package io.github.glandais.solitaire.klondike;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.board.Solitaire;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.move.MovementScore;
import io.github.glandais.solitaire.klondike.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.TableauPilesEnum;
import io.github.glandais.solitaire.klondike.serde.BoardMoves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Klondike implements Solitaire<KlondikePilesEnum> {

    public static final Klondike INSTANCE = new Klondike();

    @Override
    public Board<KlondikePilesEnum> getBoard(long seed) {
        Logger.infoln("Board seed : " + seed);
        List<Pile<KlondikePilesEnum>> piles = new ArrayList<>();
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            piles.add(new Pile<>(foundationPilesEnum.getKlondikePilesEnum()));
        }
        Pile<KlondikePilesEnum> stock = new Pile<>(KlondikePilesEnum.STOCK);
        piles.add(stock);
        Random random = new Random(seed);
        List<CardEnum> cardEnumList = new ArrayList<>(List.of(CardEnum.values()));
        Collections.shuffle(cardEnumList, random);
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile<KlondikePilesEnum> pile = new Pile<>(tableauPilesEnum.getKlondikePilesEnum());
            piles.add(pile);
            for (int i = 0; i < tableauPilesEnum.getHiddenCards(); i++) {
                CardEnum cardEnum = cardEnumList.removeLast();
                pile.hidden().add(cardEnum);
            }
            CardEnum cardEnum = cardEnumList.removeLast();
            pile.visible().add(cardEnum);
        }
        stock.hidden().addAll(cardEnumList);
        return new Board<>(piles);
    }

    @Override
    public int movesToFinish(Board<KlondikePilesEnum> board) {
        Pile<KlondikePilesEnum> pile = board.getPile(KlondikePilesEnum.STOCK);
        if (!pile.hidden().isEmpty()) {
            return UNSOLVED;
        }
        if (!pile.visible().isEmpty()) {
            return UNSOLVED;
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
            if (!pile.hidden().isEmpty()) {
                return UNSOLVED;
            }
        }
        int s = 0;
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
            s = s + pile.visible().size();
        }
        return s;
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
        if (possibleMovements.isEmpty()) {
            return List.of();
        }
        // filter useless movements
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        if (stock.visible().isEmpty() && !stock.hidden().isEmpty()) {
            for (Movement<KlondikePilesEnum> m : possibleMovements) {
                if (m.getFrom() == KlondikePilesEnum.STOCK && m.getTo() == KlondikePilesEnum.STOCK) {
                    return List.of(new MovementScore<>(m, 0, null));
                }
            }
        }
        if (possibleMovements.size() == 1) {
            return List.of(new MovementScore<>(possibleMovements.getFirst(), 0, null));
        }
        List<MovementScore<KlondikePilesEnum>> result = new ArrayList<>(possibleMovements.size());
        for (Movement<KlondikePilesEnum> m : possibleMovements) {
            result.add(getMovementScoreWithBoardScore(board, m));
        }
        return result;
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
        ScoreCardResult scoreCardResult = ScoreCard.getScore(this, board, movement);

        int score = scoreCardResult.score();
        if (Logger.DEBUG) {
            Logger.infoln(score + " " + scoreCardResult.debug());
        }
        return new MovementScore<>(movement, score, scoreCardResult.debug());
    }

    @Override
    public Object getBoardMoves(Board<KlondikePilesEnum> board, List<MovementScore<KlondikePilesEnum>> moves) {
        return new BoardMoves(board, moves);
    }

}
