package io.github.glandais.solitaire.klondike.board;

import io.github.glandais.solitaire.klondike.board.enums.PilesEnum;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;

import java.util.List;

public interface Move {
    PilesEnum from();

    PilesEnum to();

    List<CardEnum> cards();
}
