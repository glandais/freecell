package io.github.glandais.freecell.serde;

import io.github.glandais.freecell.board.piles.Pile;
import io.github.glandais.freecell.cards.enums.CardEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SerializablePile {
    protected List<CardEnum> hidden;
    protected List<CardEnum> visible;

    public static SerializablePile fromPile(Pile pile) {
        SerializablePile result = new SerializablePile();
        result.setVisible(new ArrayList<>(pile.getVisible()));
        result.setHidden(new ArrayList<>(pile.getHidden()));
        return result;
    }
}
