package io.github.glandais.solitaire.common.board;

import java.util.List;
import java.util.Set;

public record State(List<String> orderedTiles, Set<String> unorderedTiles) {
}
