package io.github.glandais.solitaire.common.solver;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.PileType;
import lombok.Getter;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class States<T extends PileType<T>> {

    private final Map<ByteArray, Integer> states = Collections.synchronizedMap(new LruCache<>(10_000_000));
    @Getter
    private long statesPut = 0;
    @Getter
    private long statesPresent = 0;
    @Getter
    private long statesRemoved = 0;

    public boolean hasState(Board<T> board, int level) {
        ByteArray state = new ByteArray(board.computeState());
        Integer existingLevel = states.get(state);
        if (existingLevel == null || level < existingLevel) {
            statesPut++;
            states.put(state, level);
            return false;
        }
        statesPresent++;
        return true;
    }

    public void discardStates(int level) {
        final Iterator<Integer> each = states.values().iterator();
        while (each.hasNext()) {
            if (each.next() > level) {
                each.remove();
                statesRemoved++;
            }
        }
    }

    public int getStatesSize() {
        return states.size();
    }
}
