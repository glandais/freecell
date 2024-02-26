package io.github.glandais.solitaire.klondike.printer.gui;

import io.github.glandais.solitaire.common.board.Board;
import io.github.glandais.solitaire.common.board.Pile;
import io.github.glandais.solitaire.common.cards.CardEnum;
import io.github.glandais.solitaire.klondike.enums.FoundationPilesEnum;
import io.github.glandais.solitaire.klondike.enums.KlondikePilesEnum;
import io.github.glandais.solitaire.klondike.enums.TableauPilesEnum;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.glandais.solitaire.klondike.printer.gui.Constants.*;

public class PrintableBoard extends ArrayList<PrintableCard> {

    public static final Comparator<PrintableCard> COMPARATOR = Comparator.comparing(PrintableCard::getZIndex).reversed();
    private Board<KlondikePilesEnum> board;
    @Getter
    private Map<CardEnum, PrintableCard> cardsMap;

    // only for replay
    protected PrintableBoard(List<PrintableCard> c) {
        super(c);
        this.board = null;
        this.cardsMap = new EnumMap<>(CardEnum.class);
        this.forEach(card -> cardsMap.put(card.getCard(), card));
        this.sort();
    }

    public PrintableBoard(Board<KlondikePilesEnum> board) {
        super();
        this.board = board;
        this.cardsMap = new EnumMap<>(CardEnum.class);
        for (CardEnum cardEnum : CardEnum.values()) {
            PrintableCard card = new PrintableCard(cardEnum, new Vector2(0, 0), new Vector2(0, 0), PrintableCardFace.FRONT, 0);
            add(card);
            cardsMap.put(card.getCard(), card);
        }
        setCardsPosition();
    }

    public void sort() {
        sort(COMPARATOR);
    }

    public List<PrintableCard> getStarts() {
        List<PrintableCard> starts = new ArrayList<>();

        starts.add(new PrintableCard(CardEnum.KING_CLUB, getFoundationPosition(FoundationPilesEnum.SPADE.ordinal(), 0), null, PrintableCardFace.SPADE, 0));
        starts.add(new PrintableCard(CardEnum.KING_CLUB, getFoundationPosition(FoundationPilesEnum.HEART.ordinal(), 0), null, PrintableCardFace.HEART, 0));
        starts.add(new PrintableCard(CardEnum.KING_CLUB, getFoundationPosition(FoundationPilesEnum.DIAMOND.ordinal(), 0), null, PrintableCardFace.DIAMOND, 0));
        starts.add(new PrintableCard(CardEnum.KING_CLUB, getFoundationPosition(FoundationPilesEnum.CLUB.ordinal(), 0), null, PrintableCardFace.CLUB, 0));

        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            starts.add(new PrintableCard(CardEnum.KING_CLUB, getTableauPosition(tableauPilesEnum.ordinal(), 0), null, PrintableCardFace.WHITE, 0));
        }
        starts.add(new PrintableCard(CardEnum.KING_CLUB, getStockVisiblePosition(), null, PrintableCardFace.WHITE, 0));
        return starts;
    }

    public void setCardsPosition() {
        synchronized (board) {
            doSetCardsPosition();
        }
    }

    private void doSetCardsPosition() {
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            Pile<KlondikePilesEnum> pile = board.getPile(foundationPilesEnum.getKlondikePilesEnum());
            int i = 0;
            for (CardEnum cardEnum : pile.visible()) {
                PrintableCard card = cardsMap.get(cardEnum);
                card.setPosition(getFoundationPosition(foundationPilesEnum.ordinal(), i++));
                card.setFace(PrintableCardFace.FRONT);
                card.setZIndex(-i);
            }
        }
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        int z = 0;
        for (int i = 0; i < stock.visible().size(); i++) {
            CardEnum cardEnum = stock.visible().get(i);

            PrintableCard card = cardsMap.get(cardEnum);
            card.setPosition(getStockVisiblePosition());
            card.setFace(PrintableCardFace.FRONT);
            card.setZIndex(100 - i);
        }
        for (int i = 0; i < stock.hidden().size(); i++) {
            CardEnum cardEnum = stock.hidden().get(i);

            PrintableCard card = cardsMap.get(cardEnum);
            card.setPosition(getStackHiddenPosition(i));
            card.setFace(PrintableCardFace.BACK);
            card.setZIndex(200 + z--);
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            Pile<KlondikePilesEnum> pile = board.getPile(tableauPilesEnum.getKlondikePilesEnum());
            int i = 0;
            for (CardEnum cardEnum : pile.hidden()) {
                PrintableCard card = cardsMap.get(cardEnum);
                card.setPosition(getTableauPosition(tableauPilesEnum.ordinal(), i));
                card.setFace(PrintableCardFace.BACK);
                card.setZIndex(50 - i);
                i++;
            }
            for (CardEnum cardEnum : pile.visible()) {
                PrintableCard card = cardsMap.get(cardEnum);
                card.setPosition(getTableauPosition(tableauPilesEnum.ordinal(), i));
                card.setFace(PrintableCardFace.FRONT);
                card.setZIndex(-i);
                i++;
            }
        }
        sort();
    }

    public PrintableCard getCardAt(double x, double y) {
        for (PrintableCard printableCard : this.reversed()) {
            if (!printableCard.dragged && inBounds(printableCard.position, x, y)) {
                return printableCard;
            }
        }
        return null;
    }

    private boolean inBounds(Vector2 position, double x, double y) {
        return position.x <= x &&
                x <= position.x + Constants.CARD_WIDTH &&
                position.y <= y &&
                y <= position.y + Constants.CARD_HEIGHT;
    }

    public KlondikePilesEnum getPileEnum(double x, double y) {
        PrintableCard cardAt = getCardAt(x, y);
        if (cardAt != null) {
            for (KlondikePilesEnum klondikePilesEnum : KlondikePilesEnum.values()) {
                List<CardEnum> visible = board.getPile(klondikePilesEnum).visible();
                if (visible.contains(cardAt.getCard())) {
                    return klondikePilesEnum;
                }
            }
        }
        for (FoundationPilesEnum foundationPilesEnum : FoundationPilesEnum.values()) {
            if (inBounds(getFoundationPosition(foundationPilesEnum.ordinal(), 0), x, y)) {
                return foundationPilesEnum.getKlondikePilesEnum();
            }
        }
        for (TableauPilesEnum tableauPilesEnum : TableauPilesEnum.values()) {
            if (board.getPile(tableauPilesEnum.getKlondikePilesEnum()).visible().isEmpty() &&
                    inBounds(getTableauPosition(tableauPilesEnum.ordinal(), 0), x, y)) {
                return tableauPilesEnum.getKlondikePilesEnum();
            }
        }
        return null;
    }

    private Vector2 getTableauPosition(int ordinal, int i) {
        return new Vector2(TOP_X + (CARD_WIDTH + SPACE) * ordinal, TOP_Y + CARD_HEIGHT + SPACE * (2 + i));
    }

    private Vector2 getFoundationPosition(int ordinal, int i) {
        return new Vector2(TOP_X + (CARD_WIDTH + SPACE) * ordinal, TOP_Y - i * SPACE_FOUNDATION);
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
                        delta < 0.5 ? from.getFace() : to.getFace(),
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
                        delta < 0.5 ? from.getFace() : to.getFace(),
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

    public boolean onStockVisible(double x, double y) {
        return inBounds(getStockVisiblePosition(), x, y);
    }

    public boolean onStockPickable(double x, double y) {
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        if (!stock.hidden().isEmpty()) {
            CardEnum pickable = stock.hidden().getLast();
            PrintableCard printableCard = cardsMap.get(pickable);
            return inBounds(printableCard.position, x, y);
        }
        return false;
    }

    public boolean onHiddenStockPickable(double x, double y) {
        Pile<KlondikePilesEnum> stock = board.getPile(KlondikePilesEnum.STOCK);
        for (CardEnum cardEnum : stock.hidden()) {
            PrintableCard printableCard = cardsMap.get(cardEnum);
            if (inBounds(printableCard.position, x, y)) {
                return true;
            }
        }
        return false;
    }
}
