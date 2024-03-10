package io.github.glandais.solitaire.common.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.common.execution.CardAction;
import io.github.glandais.solitaire.common.move.MovableStack;
import io.github.glandais.solitaire.common.move.Move;
import io.github.glandais.solitaire.common.move.Movement;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Board<T extends PileType<T>>(SequencedMap<T, Pile<T>> piles) {

    public Pile<T> getPile(T pileType) {
        return piles.get(pileType);
    }

    @JsonIgnore
    public Collection<Pile<T>> getPileValues() {
        return piles.values();
    }

    public Board<T> copy() {
        SequencedMap<T, Pile<T>> pilesCopy = new LinkedHashMap<>();
        for (Map.Entry<T, Pile<T>> entry : piles.entrySet()) {
            pilesCopy.put(entry.getKey(), entry.getValue().copy());
        }
        return new Board<>(pilesCopy);
    }

    public List<CardAction<T>> applyMovement(Move<T> move) {
        synchronized (this) {

            T fromPileType = move.getFrom();
            PlayablePile<T> from = fromPileType.playablePile();
            Pile<T> pileFrom = getPile(fromPileType);

            T toPileType = move.getTo();
            PlayablePile<T> to = toPileType.playablePile();
            Pile<T> pileTo = getPile(toPileType);

            List<CardAction<T>> actions = new ArrayList<>(from.getActions(this, pileFrom, move));
            if (fromPileType != toPileType) {
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
        for (Map.Entry<T, Pile<T>> entry : piles.entrySet()) {
            i = 0;
            for (CardEnum cardEnum : entry.getValue().visible()) {
                if (places.containsKey(cardEnum)) {
                    Logger.infoln("invalid");
                }
                places.put(cardEnum, entry.getKey().name() + "-visible-" + i);
                i++;
            }
            for (CardEnum cardEnum : entry.getValue().hidden()) {
                if (places.containsKey(cardEnum)) {
                    Logger.infoln("invalid");
                }
                places.put(cardEnum, entry.getKey().name() + "-hidden-" + i);
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
        for (Pile<T> from : piles.values()) {
            movableStacks.addAll(from.pileType().playablePile().getMovableStacks(this, from));
        }
        return movableStacks;
    }

    private List<Movement<T>> computePileMovements(List<MovableStack<T>> movableStacks) {
        List<Movement<T>> movements = new ArrayList<>();
        if (Logger.DEBUG) {
            Logger.debug("movableStacks : " + movableStacks);
        }
        for (MovableStack<T> movableStack : movableStacks) {
            for (Pile<T> to : piles.values()) {
                Optional<Movement<T>> movementOptional = to.pileType().playablePile().accept(this, to, movableStack);
                if (Logger.DEBUG) {
                    movementOptional.ifPresent(Logger::debug);
                }
                movementOptional.ifPresent(movements::add);
            }
        }

        return movements;
    }

    public String computeState() {
        synchronized (this) {
            List<String> orderedTiles = new ArrayList<>();
            List<String> unorderedTiles = new ArrayList<>();
            for (Map.Entry<T, Pile<T>> entry : piles.entrySet()) {
                String pileState = computeState(entry.getValue());
                if (entry.getKey().isSwappable()) {
                    unorderedTiles.add(pileState);
                } else {
                    orderedTiles.add(pileState);
                }
            }
            return Stream.concat(
                            unorderedTiles.stream().sorted(),
                            orderedTiles.stream()
                    )
                    .collect(Collectors.joining("|"));
        }
    }

    private String computeState(Pile<T> pile) {
        StringBuilder stringBuilder = new StringBuilder(52 * 2);
        for (CardEnum cardEnum : pile.hidden()) {
            stringBuilder.append(cardEnum.getSortableLabel());
        }
        stringBuilder.append(",");
        for (CardEnum cardEnum : pile.visible()) {
            stringBuilder.append(cardEnum.getSortableLabel());
        }
        return stringBuilder.toString();
    }

}
