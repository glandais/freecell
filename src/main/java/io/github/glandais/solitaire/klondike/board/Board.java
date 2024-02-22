package io.github.glandais.solitaire.klondike.board;

import io.github.glandais.solitaire.klondike.Logger;
import io.github.glandais.solitaire.klondike.board.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.board.enums.PilesEnum;
import io.github.glandais.solitaire.klondike.board.enums.TableauPilesEnum;
import io.github.glandais.solitaire.klondike.board.execution.CardAction;
import io.github.glandais.solitaire.klondike.board.piles.FoundationPile;
import io.github.glandais.solitaire.klondike.board.piles.Pile;
import io.github.glandais.solitaire.klondike.board.piles.StockPile;
import io.github.glandais.solitaire.klondike.board.piles.TableauPile;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;
import io.github.glandais.solitaire.klondike.cards.enums.OrderEnum;
import io.github.glandais.solitaire.klondike.cards.enums.StateCardEnum;
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
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            this.piles.put(foundationPilesEnum.getPilesEnum(), new FoundationPile(foundationPilesEnum));
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            this.piles.put(tableauPilesEnum.getPilesEnum(), new TableauPile(tableauPilesEnum));
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

    public List<MovementScore> getPossibleMovements() {
        synchronized (this) {
            List<Movement> movements = new ArrayList<>();
            getPilesMovements(movements);
            if (Logger.DEBUG) {
                Logger.debug("moves : " + movements);
            }
            List<MovementScore> movementScoreList = new ArrayList<>(movements.size());
            for (Movement movement : movements) {
                List<CardAction> actions = applyMovement(movement);
                int score = getScore();
                movementScoreList.add(new MovementScore(movement, score));
                revertMovement(actions);
            }
            movementScoreList.sort(Comparator.comparing(MovementScore::score));
            if (Logger.DEBUG) {
                Logger.debug("movementScoreList : " + movementScoreList);
            }
            return movementScoreList;
        }
    }

    private void getPilesMovements(List<Movement> movements) {
        for (Pile from : piles.values()) {
            List<MovableStack> movableStacks = from.getMovableStacks();
            getPileMovements(movements, from, movableStacks);
        }
    }

    private void getPileMovements(List<Movement> movements, Pile from, List<MovableStack> movableStacks) {
        if (Logger.DEBUG) {
            Logger.debug(from + " movableStacks : " + movableStacks);
        }
        for (MovableStack movableStack : movableStacks) {
            for (Pile to : piles.values()) {
                Optional<Movement> movementOptional = to.accept(this, movableStack);
                if (Logger.DEBUG) {
                    movementOptional.ifPresent(Logger::debug);
                }
                movementOptional.ifPresent(movements::add);
            }
        }
    }

    public List<CardAction> applyMovement(Move move) {
        synchronized (this) {
            Pile from = getPile(move.from());
            Pile to = getPile(move.to());

            List<CardAction> actions = new ArrayList<>(from.getActions(move));
            if (from.getPilesEnum() != to.getPilesEnum()) {
                actions.addAll(to.getActions(move));
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
            StringBuilder sbPiles = new StringBuilder(52 * 4);
            appendVisibleCards(sbPiles, PilesEnum.STOCK);
            for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
                sbPiles.append('z');
                appendVisibleCards(sbPiles, foundationPilesEnum.getPilesEnum());
            }
            Set<String> tableaux = HashSet.newHashSet(4);
            for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
                StringBuilder sbTableau = new StringBuilder(25);
                appendHiddenCards(sbTableau, tableauPilesEnum.getPilesEnum());
                sbTableau.append('z');
                appendVisibleCards(sbTableau, tableauPilesEnum.getPilesEnum());
                tableaux.add(sbTableau.toString());
            }
            return new State(sbPiles.toString(), tableaux);
        }
    }

    private void appendHiddenCards(StringBuilder sb, PilesEnum pilesEnum) {
        for (CardEnum cardEnum : getPile(pilesEnum).getHidden()) {
            appendCard(sb, cardEnum);
        }
    }

    private void appendVisibleCards(StringBuilder sb, PilesEnum pilesEnum) {
        for (CardEnum cardEnum : getPile(pilesEnum).getVisible()) {
            appendCard(sb, cardEnum);
        }
    }

    private void appendCard(StringBuilder sb, CardEnum cardEnum) {
        StateCardEnum stateCardEnum = cardEnum.getStateCardEnum();
        sb.append(stateCardEnum.getColor().getLabel());
        sb.append(stateCardEnum.getOrder().getLabel());
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

    public int getScore() {
        int score = 0;
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            Pile pile = getPile(foundationPilesEnum.getPilesEnum());
            score = score - pile.getVisible().size() * 10_000;
        }
        int kingsAtTop = 0;
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile pile = getPile(tableauPilesEnum.getPilesEnum());
            if (pile.getHidden().isEmpty() && !pile.getVisible().isEmpty() && pile.getVisible().getFirst().getOrderEnum() == OrderEnum.KING) {
                kingsAtTop++;
            }
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile pile = getPile(tableauPilesEnum.getPilesEnum());
            List<CardEnum> hidden = pile.getHidden();
            List<CardEnum> visible = pile.getVisible();
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

}
