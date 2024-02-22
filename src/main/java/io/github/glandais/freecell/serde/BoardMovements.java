package io.github.glandais.freecell.serde;

import io.github.glandais.freecell.board.MovementScore;

import java.util.List;

public record BoardMovements(long seed, List<MovementScore> movements) {
}
