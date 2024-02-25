package io.github.glandais.solitaire.common.board;

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

    public Board<T> copy() {
        SequencedMap<T, Pile<T>> pilesCopy = new LinkedHashMap<>();
        for (Map.Entry<T, Pile<T>> entry : piles.entrySet()) {
            pilesCopy.put(entry.getKey(), entry.getValue().copy());
        }
        return new Board<>(pilesCopy);
    }

    public List<CardAction<T>> applyMovement(Move<T> move) {
        synchronized (this) {

            T fromPileType = move.from();
            PlayablePile<T> from = fromPileType.playablePile();
            Pile<T> pileFrom = getPile(fromPileType);

            T toPileType = move.to();
            PlayablePile<T> to = toPileType.playablePile();
            Pile<T> pileTo = getPile(toPileType);

            List<CardAction<T>> actions = new ArrayList<>(from.getActions(this, pileFrom, move));
            if (fromPileType != toPileType) {
                actions.addAll(to.getActions(this, pileTo, move));
            }
            applyActions(actions);
            return actions;
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
            List<Movement<T>> movements = new ArrayList<>();
            computePilesMovements(movements);
            if (Logger.DEBUG) {
                Logger.debug("moves : " + movements);
            }
            return movements;
        }
    }

    private void computePilesMovements(List<Movement<T>> movements) {
        for (Pile<T> from : piles.values()) {
            List<MovableStack<T>> movableStacks = from.pileType().playablePile().getMovableStacks(this, from);
            computePileMovements(movements, from, movableStacks);
        }
    }

    private void computePileMovements(List<Movement<T>> movements, Pile<T> from, List<MovableStack<T>> movableStacks) {
        if (Logger.DEBUG) {
            Logger.debug(from.pileType() + " movableStacks : " + movableStacks);
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
                    .collect(Collectors.joining());
        }
    }

    private String computeState(Pile<T> pile) {
        StringBuilder stringBuilder = new StringBuilder(52 * 2);
        for (CardEnum cardEnum : pile.hidden()) {
            stringBuilder.append(cardEnum.toString());
        }
        stringBuilder.append(",");
        for (CardEnum cardEnum : pile.visible()) {
            stringBuilder.append(cardEnum.toString());
        }
        return stringBuilder.toString();
    }

}
