package io.github.glandais.solitaire.common.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.MovableStack;
import io.github.glandais.solitaire.common.move.Move;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record Board<T extends PileType<T>>(Pile<T>[] piles) {

    public Board(List<Pile<KlondikePilesEnum>> piles) {
        this(piles.stream().sorted(Comparator.comparing(p -> p.pileType().ordinal()))
                .toArray(Pile[]::new));
    }

    public Pile<T> getPile(T pileType) {
        return piles[pileType.ordinal()];
    }

    @JsonIgnore
    public Collection<Pile<T>> getPileValues() {
        return List.of(piles);
    }

    public Board<T> copy() {
        Pile<T>[] copy = new Pile[piles.length];
        for (int i = 0; i < piles.length; i++) {
            copy[i] = piles[i].copy();
        }
        return new Board<>(copy);
    }

    public List<CardAction<T>> applyMovement(Move<T> move) {
        synchronized (this) {

            T fromPileType = move.getFrom();
            PlayablePile<T> from = fromPileType.playablePile();
            Pile<T> pileFrom = getPile(fromPileType);

            T toPileType = move.getTo();
            PlayablePile<T> to = toPileType.playablePile();
            Pile<T> pileTo = getPile(toPileType);

            List<CardAction<T>> actions = from.getActions(this, pileFrom, move);
            if (fromPileType != toPileType) {
                actions = new ArrayList<>(actions);
                actions.addAll(to.getActions(this, pileTo, move));
            }
            applyActions(actions);

//            check();

            return actions;
        }
    }

    private void check() {
        Map<CardEnum, String> places = new EnumMap<>(CardEnum.class);
        int i;
        for (Pile<T> pile : piles) {
            i = 0;
            for (CardEnum cardEnum : pile.visible()) {
                if (places.containsKey(cardEnum)) {
                    Logger.infoln("invalid");
                }
                places.put(cardEnum, pile.pileType().name() + "-visible-" + i);
                i++;
            }
            for (CardEnum cardEnum : pile.hidden()) {
                if (places.containsKey(cardEnum)) {
                    Logger.infoln("invalid");
                }
                places.put(cardEnum, pile.pileType().name() + "-hidden-" + i);
                i++;
            }
        }
        if (places.size() != 52) {
            Logger.infoln("invalid");
        }
    }

    private void applyActions(List<CardAction<T>> actions) {
        for (CardAction<T> action : actions) {
            Pile<T> pile = getPile(action.pileType());
            action.perform(pile);
        }
    }

    public void revertMovement(List<CardAction<T>> actions) {
        synchronized (this) {
            for (CardAction<T> action : actions.reversed()) {
                Pile<T> pile = getPile(action.pileType());
                action.revert(pile);
            }
        }
    }

    public List<Movement<T>> computePossibleMovements() {
        synchronized (this) {
            List<MovableStack<T>> movableStacks = getMovableStacks();
            List<Movement<T>> movements = computePileMovements(movableStacks);
            if (Logger.DEBUG) {
                Logger.debug("moves : " + movements);
            }
            return movements;
        }
    }

    @JsonIgnore
    public List<MovableStack<T>> getMovableStacks() {
        List<MovableStack<T>> movableStacks = new ArrayList<>();
        for (Pile<T> from : piles) {
            movableStacks.addAll(from.pileType().playablePile().getMovableStacks(this, from));
        }
        return movableStacks;
    }

    private List<Movement<T>> computePileMovements(List<MovableStack<T>> movableStacks) {
        List<Movement<T>> movements = new ArrayList<>(movableStacks.size());
        if (Logger.DEBUG) {
            Logger.debug("movableStacks : " + movableStacks);
        }
        for (MovableStack<T> movableStack : movableStacks) {
            for (Pile<T> to : piles) {
                Movement<T> movement = to.pileType().playablePile().accept(this, to, movableStack);
                if (movement != null) {
                    if (Logger.DEBUG) {
                        Logger.debug(movement);
                    }
                    movements.add(movement);
                }
            }
        }

        return movements;
    }

    public byte[] computeState() {
        byte[][] state = new byte[piles.length][];
        int p = 0;
        for (Pile<T> pile : piles) {
            if (pile.pileType().isSwappable()) {
                state[p] = computePileState(pile);
                p++;
            }
        }
        int swappableEnd = p;
        for (Pile<T> pile : piles) {
            if (!pile.pileType().isSwappable()) {
                state[p] = computePileState(pile);
                p++;
            }
        }
        Arrays.sort(state, 0, swappableEnd, Arrays::compare);
        byte[] result = new byte[52 + piles.length * 2];
        int i = 0;
        for (byte[] bytes : state) {
            for (byte b : bytes) {
                result[i++] = b;
            }
            result[i++] = 127;
        }
        return result;
    }

    private byte[] computePileState(Pile<T> pile) {
        Cards hidden = pile.hidden();
        Cards visible = pile.visible();
        byte[] result = new byte[hidden.size() + visible.size() + 1];
        int i = 0;
        for (CardEnum cardEnum : hidden) {
            result[i++] = (byte) cardEnum.ordinal();
        }
        result[i++] = -128;
        for (CardEnum cardEnum : visible) {
            result[i++] = (byte) cardEnum.ordinal();
        }
        return result;
    }

}
