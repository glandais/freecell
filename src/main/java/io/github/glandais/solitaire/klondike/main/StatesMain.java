package io.github.glandais.solitaire.klondike.main;

import io.github.glandais.solitaire.common.Logger;
import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.move.Movement;
import io.github.glandais.solitaire.common.solver.States;
import io.github.glandais.solitaire.klondike.Klondike;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.printer.console.KlondikeConsolePrinter;

import java.util.List;
import java.util.Optional;

public class StatesMain {

    public static void main(String[] args) {
        KlondikeConsolePrinter klondikeConsolePrinter = new KlondikeConsolePrinter();
        Board<KlondikePilesEnum> board = Klondike.INSTANCE.getBoard(0);
        States<KlondikePilesEnum> states = new States<>();
        states.hasState(board, 0);
        for (int i = 0; i < 50; i++) {
            klondikeConsolePrinter.print(board);
            Logger.infoln(board.getPile(KlondikePilesEnum.STOCK));
            List<Movement<KlondikePilesEnum>> movements = board.computePossibleMovements();
            Optional<Movement<KlondikePilesEnum>> optionalMovement = movements.stream()
                    .filter(m -> m.getFrom() == KlondikePilesEnum.STOCK && m.getTo() == KlondikePilesEnum.STOCK)
                    .findFirst();
            if (optionalMovement.isPresent()) {
                Logger.infoln(optionalMovement.get());
                board.applyMovement(optionalMovement.get());
//                klondikeConsolePrinter.print(board);
                states.hasState(board, 0);
                System.out.println(states.getStatesPresent());
                System.out.println(states.getStatesNew());
                System.out.println(states.getStatesBetter());
            }
        }
    }

}
