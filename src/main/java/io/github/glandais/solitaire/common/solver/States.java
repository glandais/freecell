package io.github.glandais.solitaire.common.solver;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.PileType;
import lombok.Getter;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class States<T extends PileType<T>> {

    private final Cache<ByteArray, AtomicInteger> states = Caffeine.newBuilder()
            .maximumSize(10_000_000)
            .build();
    @Getter
    private long statesNew = 0;
    @Getter
    private long statesBetter = 0;
    @Getter
    private long statesPresent = 0;
    @Getter
    private long statesRemoved = 0;

    public boolean hasState(Board<T> board, int level) {
        ByteArray state = new ByteArray(board.computeState());
        AtomicInteger atomicInteger = states.get(state, b -> new AtomicInteger(10000));
        int stateLevel = atomicInteger.get();
        if (stateLevel == 10000 || level < stateLevel) {
            if (stateLevel == 10000) {
                statesNew++;
            } else {
                statesBetter++;
            }
            atomicInteger.set(level);
            return false;
        }
        statesPresent++;
        return true;
    }

    public void discardStates(int level) {
        synchronized (states) {
            final Iterator<AtomicInteger> each = states.asMap().values().iterator();
            while (each.hasNext()) {
                if (each.next().get() > level) {
                    each.remove();
                    statesRemoved++;
                }
            }
        }
    }

    public long getStatesSize() {
        return states.estimatedSize();
    }
}
