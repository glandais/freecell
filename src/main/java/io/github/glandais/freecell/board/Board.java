package io.github.glandais.freecell.board;

import io.github.glandais.freecell.board.enums.PilesEnum;
import io.github.glandais.freecell.board.enums.SuitePilesEnum;
import io.github.glandais.freecell.board.enums.TableauPilesEnum;
import io.github.glandais.freecell.board.execution.CardAction;
import io.github.glandais.freecell.board.piles.FoundationPile;
import io.github.glandais.freecell.board.piles.Pile;
import io.github.glandais.freecell.board.piles.StockPile;
import io.github.glandais.freecell.board.piles.TableauPile;
import io.github.glandais.freecell.cards.enums.CardEnum;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.*;

@Getter
public class Board {

    private final long seed;
    private final Map<PilesEnum, Pile> piles;

    public Board() {
        this(new SecureRandom().nextInt());
    }

    public Board(Board board) {
        this(board.seed);
    }

    public Board(long seed) {
        this.seed = seed;
        this.piles = new EnumMap<>(PilesEnum.class);
        this.piles.put(PilesEnum.STOCK, new StockPile());
        for (SuitePilesEnum suitePilesEnum : SuitePilesEnum.values()) {
            this.piles.put(suitePilesEnum.getPilesEnum(), new FoundationPile(suitePilesEnum));
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            this.piles.put(tableauPilesEnum.getPilesEnum(), new TableauPile(tableauPilesEnum));
        }
        init(seed);
    }

    protected void init(long seed) {
        Random random = new Random(seed);
        List<CardEnum> cardEnumList = new ArrayList<>(List.of(CardEnum.values()));
        Collections.shuffle(cardEnumList, random);
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile pile = getPile(tableauPilesEnum.getPilesEnum());
            for (int i = 0; i < tableauPilesEnum.getHiddenCards(); i++) {
                CardEnum cardEnum = cardEnumList.removeLast();
                pile.getHidden().add(cardEnum);
            }
            CardEnum cardEnum = cardEnumList.removeLast();
            pile.getVisible().add(cardEnum);
        }
        Pile pile = getPile(PilesEnum.STOCK);
        pile.getVisible().addAll(cardEnumList);
    }

    public Pile getPile(PilesEnum pilesEnum) {
        return piles.get(pilesEnum);
    }

    public List<Movement> getPossibleMovements() {
        List<Movement> movements = new ArrayList<>();
        for (Pile from : piles.values()) {
            List<MovableStack> movableStacks = from.getMovableStacks();
            for (MovableStack movableStack : movableStacks) {
                for (Pile to : piles.values()) {
                    to.accept(this, movableStack).ifPresent(movements::add);
                }
            }
        }
        movements.sort(Comparator.comparing(Movement::score));
        return movements;
    }

    public List<CardAction> applyMovement(Movement movement) {
        PilesEnum fromPilesEnum = movement.movableStack().from();
        Pile from = getPile(fromPilesEnum);
        PilesEnum toPilesEnum = movement.to();
        Pile to = getPile(toPilesEnum);

        List<CardAction> actions = new ArrayList<>(from.performMovement(movement));
        if (from.getPilesEnum() != to.getPilesEnum()) {
            actions.addAll(to.performMovement(movement));
        }
        applyActions(actions);
        return actions;
    }

    private void applyActions(List<CardAction> actions) {
        for (CardAction action : actions) {
            Pile pile = getPile(action.pilesEnum());
            action.perform(pile);
        }
    }

    public void revertMovement(List<CardAction> actions) {
        for (CardAction action : actions.reversed()) {
            Pile pile = getPile(action.pilesEnum());
            action.revert(pile);
        }
    }

    public List<List<CardEnum>> getState() {
        List<List<CardEnum>> state = new ArrayList<>(24);
        for (PilesEnum pilesEnum : PilesEnum.values()) {
            Pile pile = getPiles().get(pilesEnum);
            state.add(new ArrayList<>(pile.getHidden()));
            state.add(new ArrayList<>(pile.getVisible()));
        }
        return state;
    }

    public boolean isFinished() {
        Pile pile = getPile(PilesEnum.STOCK);
        if (pile.getVisible().size() > 1) {
            return false;
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            pile = getPile(tableauPilesEnum.getPilesEnum());
            if (!pile.getHidden().isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
