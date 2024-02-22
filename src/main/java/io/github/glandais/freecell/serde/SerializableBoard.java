package io.github.glandais.freecell.serde;

import io.github.glandais.freecell.board.Board;
import io.github.glandais.freecell.board.enums.PilesEnum;
import io.github.glandais.freecell.board.enums.SuitePilesEnum;
import io.github.glandais.freecell.board.enums.TableauPilesEnum;
import io.github.glandais.freecell.board.piles.Pile;
import lombok.Data;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class SerializableBoard {

    private SerializablePile stock;

    private Map<SuitePilesEnum, SerializablePile> foundations;

    private Map<TableauPilesEnum, SerializablePile> tableaux;

    public Board toBoard() {
        Board board = new Board();
        restorePile(board.getPiles().get(PilesEnum.STOCK), stock);
        for (SuitePilesEnum suitePilesEnum : SuitePilesEnum.values()) {
            restorePile(board.getPiles().get(suitePilesEnum.getPilesEnum()), foundations.get(suitePilesEnum));
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
                Arrays.stream(SuitePilesEnum.values())
                        .collect(Collectors.toMap(Function.identity(), s -> SerializablePile.fromPile(board.getPile(s.getPilesEnum()))))
        );
        serializableBoard.setTableaux(
                Arrays.stream(TableauPilesEnum.values())
                        .collect(Collectors.toMap(Function.identity(), s -> SerializablePile.fromPile(board.getPile(s.getPilesEnum()))))
        );
        return serializableBoard;
    }

}
