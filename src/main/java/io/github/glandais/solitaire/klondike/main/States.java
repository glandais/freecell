package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class States {

    public static void main(String[] args) {
        KlondikeConsolePrinter klondikeConsolePrinter = new KlondikeConsolePrinter();
        Board<KlondikePilesEnum> board = Klondike.INSTANCE.getBoard(0);
        Set<String> states = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            List<Movement<KlondikePilesEnum>> movements = board.computePossibleMovements();
            Optional<Movement<KlondikePilesEnum>> optionalMovement = movements.stream()
                    .filter(m -> m.getFrom() == KlondikePilesEnum.STOCK && m.getTo() == KlondikePilesEnum.STOCK)
                    .findFirst();
            if (optionalMovement.isPresent()) {
                board.applyMovement(optionalMovement.get());
//                klondikeConsolePrinter.print(board);
                String state = board.computeState();
                states.add(state);
                System.out.println(state);
                System.out.println(states.size());
            }
        }
    }

}
