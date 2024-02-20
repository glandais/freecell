package io.github.glandais.freecell.serde;

import io.github.glandais.freecell.board.Movements;

public record BoardMovements(long seed, Movements movements) {
}
