package io.github.glandais.solitaire.klondike.board;

import java.util.Set;

public record State(String piles, Set<String> tableauSet) {
}
