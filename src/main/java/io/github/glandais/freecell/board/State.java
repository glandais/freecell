package io.github.glandais.freecell.board;

import java.util.Set;

public record State(String piles, Set<String> tableauSet) {
}
