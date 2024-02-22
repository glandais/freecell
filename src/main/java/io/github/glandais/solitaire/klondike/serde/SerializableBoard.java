package io.github.glandais.solitaire.klondike.serde;

import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.board.enums.PilesEnum;
import io.github.glandais.solitaire.klondike.board.enums.TableauPilesEnum;
import io.github.glandais.solitaire.klondike.board.piles.Pile;
import lombok.Data;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class SerializableBoard {

    private SerializablePile stock;

    private Map<FoundationPilesEnum, SerializablePile> foundations;

    private Map<TableauPilesEnum, SerializablePile> tableaux;

    public Board toBoard() {
        Board board = new Board();
        restorePile(board.getPiles().get(PilesEnum.STOCK), stock);
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            restorePile(board.getPiles().get(foundationPilesEnum.getPilesEnum()), foundations.get(foundationPilesEnum));
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            restorePile(board.getPiles().get(tableauPilesEnum.getPilesEnum()), tableaux.get(tableauPilesEnum));
        }
        return board;
    }

    private void restorePile(Pile pile, SerializablePile stock) {
        pile.getVisible().clear();
        pile.getVisible().addAll(stock.getVisible());
        pile.getHidden().clear();
        pile.getHidden().addAll(stock.getHidden());
    }

    public static SerializableBoard fromBoard(Board board) {
        SerializableBoard serializableBoard = new SerializableBoard();
        serializableBoard.setStock(SerializablePile.fromPile(board.getPile(PilesEnum.STOCK)));
        serializableBoard.setFoundations(
                Arrays.stream(FoundationPilesEnum.values())
                        .collect(Collectors.toMap(Function.identity(), s -> SerializablePile.fromPile(board.getPile(s.getPilesEnum()))))
        );
        serializableBoard.setTableaux(
                Arrays.stream(TableauPilesEnum.values())
                        .collect(Collectors.toMap(Function.identity(), s -> SerializablePile.fromPile(board.getPile(s.getPilesEnum()))))
        );
        return serializableBoard;
    }

}
