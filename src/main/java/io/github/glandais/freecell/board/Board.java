package io.github.glandais.freecell.board;

import io.github.glandais.freecell.board.enums.PileTypeEnum;
import io.github.glandais.freecell.board.enums.PilesEnum;
import io.github.glandais.freecell.board.enums.SuitePilesEnum;
import io.github.glandais.freecell.board.enums.TableauPilesEnum;
import io.github.glandais.freecell.board.execution.CardAction;
import io.github.glandais.freecell.board.piles.FoundationPile;
import io.github.glandais.freecell.board.piles.Pile;
import io.github.glandais.freecell.board.piles.StockPile;
import io.github.glandais.freecell.board.piles.TableauPile;
import io.github.glandais.freecell.cards.enums.CardEnum;
import io.github.glandais.freecell.cards.enums.CardOrderEnum;
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
        init();
    }

    public void init() {
        this.piles.clear();
        this.piles.put(PilesEnum.STOCK, new StockPile());
        for (SuitePilesEnum suitePilesEnum : SuitePilesEnum.values()) {
            this.piles.put(suitePilesEnum.getPilesEnum(), new FoundationPile(suitePilesEnum, false));
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            this.piles.put(tableauPilesEnum.getPilesEnum(), new TableauPile(tableauPilesEnum, false));
        }
        Random random = new Random(this.seed);
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

    public Movements getPossibleMovements() {
        synchronized (this) {
            Movements movements = new Movements();
            for (Pile from : piles.values()) {
                List<MovableStack> movableStacks = from.getMovableStacks();
                for (MovableStack movableStack : movableStacks) {
                    for (Pile to : piles.values()) {
                        to.accept(this, movableStack).ifPresent(movements::add);
                    }
                }
            }
            movements.sort(Comparator.comparing(Movement::score).thenComparing(Movement::toString));
            Optional<Movement> kingMovement = movements.stream()
                    .filter(m -> m.movableStack().from() == PilesEnum.STOCK && m.to().getPileTypeEnum() == PileTypeEnum.TABLEAU && m.movableStack().cards().getLast().getCardOrderEnum() == CardOrderEnum.KING)
                    .findAny();
            if (kingMovement.isPresent()) {
                return new Movements(List.of(kingMovement.get()));
            } else {
            return movements;
            }
        }
    }

    public List<CardAction> applyMovement(Movement movement) {
        synchronized (this) {
            Pile from = getPile(movement.movableStack().from());
            Pile to = getPile(movement.to());

            List<CardAction> actions = new ArrayList<>(from.getActions(movement));
            if (from.getPilesEnum() != to.getPilesEnum()) {
                actions.addAll(to.getActions(movement));
            }
            applyActions(actions);
            return actions;
        }
    }

    private void applyActions(List<CardAction> actions) {
        for (CardAction action : actions) {
            Pile pile = getPile(action.pilesEnum());
            action.perform(pile);
        }
    }

    public void revertMovement(List<CardAction> actions) {
        synchronized (this) {
            for (CardAction action : actions.reversed()) {
                Pile pile = getPile(action.pilesEnum());
                action.revert(pile);
            }
        }
    }

    public State getState() {
        synchronized (this) {
            Integer[] result = new Integer[76];
            int i = 0;
            for (Pile pile : piles.values()) {
                for (CardEnum cardEnum : pile.getHidden()) {
                    result[i++] = cardEnum.ordinal();
                }
                result[i++] = 255;
                for (CardEnum cardEnum : pile.getVisible()) {
                    result[i++] = cardEnum.ordinal();
                }
                result[i++] = 255;
            }
            return new State(Arrays.asList(result));
        }
    }

    public boolean isFinished() {
        synchronized (this) {
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

}
