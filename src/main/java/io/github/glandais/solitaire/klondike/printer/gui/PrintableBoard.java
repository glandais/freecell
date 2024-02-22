package io.github.glandais.solitaire.klondike.printer.gui;

import dev.aurumbyte.sypherengine.util.math.Vector2;
import io.github.glandais.solitaire.klondike.board.Board;
import io.github.glandais.solitaire.klondike.board.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.board.enums.PilesEnum;
import io.github.glandais.solitaire.klondike.board.enums.TableauPilesEnum;
import io.github.glandais.solitaire.klondike.board.piles.Pile;
import io.github.glandais.solitaire.klondike.cards.enums.CardEnum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.glandais.solitaire.klondike.printer.gui.Constants.*;

public class PrintableBoard extends ArrayList<PrintableCard> {

    protected PrintableBoard(List<PrintableCard> c) {
        super(c);
    }

    public PrintableBoard(Board board) {
        super();
        synchronized (board) {
            addCards(board);
        }
    }

    private void addCards(Board board) {
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            Pile pile = board.getPile(foundationPilesEnum.getPilesEnum());
            int i = 0;
            for (CardEnum cardEnum : pile.getVisible()) {
                add(new PrintableCard(cardEnum, getFoundationPosition(foundationPilesEnum.ordinal(), i++), true, -i));
            }
        }
        Pile stock = board.getPile(PilesEnum.STOCK);
        for (int i = 0; i < stock.getVisible().size(); i++) {
            CardEnum cardEnum = stock.getVisible().get(i);
            if (i == stock.getVisible().size() - 1) {
                add(new PrintableCard(cardEnum, getStockVisiblePosition(), true, -i));
            } else {
                add(new PrintableCard(cardEnum, getStackHiddenPosition(i), false, i == 0 ? 100 : -i));
            }
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile pile = board.getPile(tableauPilesEnum.getPilesEnum());
            int i = 0;
            for (CardEnum cardEnum : pile.getHidden()) {
                add(new PrintableCard(cardEnum, getTableauPosition(tableauPilesEnum.ordinal(), i), false, 10));
                i++;
            }
            for (CardEnum cardEnum : pile.getVisible()) {
                add(new PrintableCard(cardEnum, getTableauPosition(tableauPilesEnum.ordinal(), i), true, -i));
                i++;
            }
        }
        sort(Comparator.comparing(PrintableCard::zIndex).reversed());
    }

    private Vector2 getTableauPosition(int ordinal, int i) {
        return new Vector2(TOP_X + (CARD_WIDTH + SPACE) * ordinal, TOP_Y + CARD_HEIGHT + SPACE * (2 + i));
    }

    private Vector2 getFoundationPosition(int i, int j) {
        return new Vector2(TOP_X + (CARD_WIDTH + SPACE) * i, TOP_Y - j * SPACE_FOUNDATION);
    }

    private Vector2 getStockVisiblePosition() {
        return new Vector2(TOP_X + (CARD_WIDTH + SPACE) * 5, TOP_Y);
    }

    private Vector2 getStackHiddenPosition(int i) {
        return new Vector2(TOP_X + (CARD_WIDTH + SPACE) * 6 + i * SPACE_STACK, TOP_Y);
    }

    public PrintableBoard interpolate(PrintableBoard printableBoardTo, float delta) {
        List<PrintableCard> cards = new ArrayList<>();
        Map<CardEnum, PrintableCard> toCards = printableBoardTo.stream()
                .collect(Collectors.toMap(PrintableCard::card, Function.identity()));
        for (PrintableCard from : this) {
            PrintableCard to = toCards.get(from.card());
            if (Vector2.distance(from.position(), to.position()) < 0.1) {
                cards.add(new PrintableCard(
                        to.card(),
                        to.position(),
                        delta < 0.5 ? from.faceUp() : to.faceUp(),
                        to.zIndex()
                ));
            }
        }
        for (PrintableCard from : this) {
            PrintableCard to = toCards.get(from.card());
            if (Vector2.distance(from.position(), to.position()) >= 0.1) {
                cards.add(new PrintableCard(
                        to.card(),
                        interpolate(from.position(), to.position(), delta),
                        delta < 0.5 ? from.faceUp() : to.faceUp(),
                        to.zIndex() - 100
                ));
            }
        }
        cards.sort(Comparator.comparing(PrintableCard::zIndex).reversed());
        return new PrintableBoard(cards);
    }

    private Vector2 interpolate(Vector2 from, Vector2 to, float delta) {
        float x = from.xPos + (to.xPos - from.xPos) * delta;
        float y = from.yPos + (to.yPos - from.yPos) * delta;
        return new Vector2(x, y);
    }
}
