package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.klondike.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.TableauPilesEnum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.glandais.solitaire.klondike.printer.gui.Constants.*;

public class PrintableBoard extends ArrayList<PrintableCard> {

    public static final Comparator<PrintableCard> COMPARATOR = Comparator.comparing(PrintableCard::getZIndex).reversed();

    protected PrintableBoard(List<PrintableCard> c) {
        super(c);
        this.sort();
    }

    public void sort() {
        sort(COMPARATOR);
    }

    public PrintableBoard(Board<KlondikePilesEnum> board) {
        super();
        synchronized (board) {
            addCards(board);
        }
    }

    private void addCards(Board<KlondikePilesEnum> board) {
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            Pile<KlondikePilesEnum> pile = board.getPile(foundationPilesEnum.getKlondikePilesEnum());
            int i = 0;
            for (CardEnum cardEnum : pile.visible()) {
                add(new PrintableCard(cardEnum, getFoundationPosition(foundationPilesEnum.ordinal(), i++), null, true, -i));
            }
        }
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        int z = 0;
        for (int i = 0; i < stock.visible().size(); i++) {
            CardEnum cardEnum = stock.visible().get(i);
            add(new PrintableCard(cardEnum, getStockVisiblePosition(), null, true, 100-i));
        }
        for (int i = 0; i < stock.hidden().size(); i++) {
            CardEnum cardEnum = stock.hidden().get(i);
            add(new PrintableCard(cardEnum, getStackHiddenPosition(stock.hidden().size() - i), null, false, 200 + z--));
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile<KlondikePilesEnum> pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
            int i = 0;
            for (CardEnum cardEnum : pile.hidden()) {
                add(new PrintableCard(cardEnum, getTableauPosition(tableauPilesEnum.ordinal(), i), null, false, 50-i));
                i++;
            }
            for (CardEnum cardEnum : pile.visible()) {
                add(new PrintableCard(cardEnum, getTableauPosition(tableauPilesEnum.ordinal(), i), null, true, -i));
                i++;
            }
        }
        sort(COMPARATOR);
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
                .collect(Collectors.toMap(PrintableCard::getCard, Function.identity()));
        for (PrintableCard from : this) {
            PrintableCard to = toCards.get(from.getCard());
            if (from.getPosition().distance(to.getPosition()) < 0.1) {
                cards.add(new PrintableCard(
                        to.getCard(),
                        to.getPosition(),
                        null,
                        delta < 0.5 ? from.isFaceUp() : to.isFaceUp(),
                        to.getZIndex()
                ));
            }
        }
        for (PrintableCard from : this) {
            PrintableCard to = toCards.get(from.getCard());
            if (from.getPosition().distance(to.getPosition()) >= 0.1) {
                cards.add(new PrintableCard(
                        to.getCard(),
                        interpolate(from.getPosition(), to.getPosition(), delta),
                        null,
                        delta < 0.5 ? from.isFaceUp() : to.isFaceUp(),
                        to.getZIndex() - 1000
                ));
            }
        }
        return new PrintableBoard(cards);
    }

    private Vector2 interpolate(Vector2 from, Vector2 to, float delta) {
        double x = from.x + (to.x - from.x) * delta;
        double y = from.y + (to.y - from.y) * delta;
        return new Vector2(x, y);
    }
}
