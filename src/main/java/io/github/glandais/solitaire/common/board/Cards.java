package io.github.glandais.solitaire.common.board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.glandais.solitaire.common.cards.CardEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Cards implements Iterable<CardEnum> {

    int size = 0;
    CardEnum[] cards = new CardEnum[52];

    public static Cards of(CardEnum card) {
        Cards cards = new Cards();
        cards.size = 1;
        cards.cards[0] = card;
        return cards;
    }

    @JsonValue
    public List<CardEnum> forJson() {
        List<CardEnum> result = new ArrayList<>();
        this.forEach(result::add);
        return result;
    }

    @JsonCreator
    public static Cards fromValue(List<CardEnum> value) {
        if (value == null) {
            return null;
        }
        Cards cards = new Cards();
        cards.size = value.size();
        for (int i = 0; i < cards.size; i++) {
            cards.cards[i] = value.get(i);
        }
        return cards;
    }

    public Cards copy() {
        Cards copy = new Cards();
        copy.size = size;
        copy.cards = cards.clone();
        return copy;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @JsonIgnore
    public CardEnum getLast() {
        return cards[size - 1];
    }

    public void addFirst(CardEnum card) {
        for (int i = size - 1; i >= 0; i--) {
            cards[i + 1] = cards[i];
        }
        cards[0] = card;
        size = size + 1;
    }

    public void add(CardEnum card) {
        cards[size] = card;
        size = size + 1;
    }

    public void remove(CardEnum card) {
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (cards[i] != card && i != j) {
                cards[j] = cards[i];
            }
            j++;
        }
        size = j - 1;
    }

    public void addAll(List<CardEnum> cardEnumList) {
        for (CardEnum cardEnum : cardEnumList) {
            add(cardEnum);
        }
    }

    public CardEnum get(int i) {
        return cards[i];
    }

    public boolean contains(CardEnum card) {
        for (int i = 0; i < size; i++) {
            if (cards[i] == card) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public CardEnum getFirst() {
        return cards[0];
    }

    public static class CardsIterator implements Iterator<CardEnum> {
        final Cards cards;
        int i = 0;

        public CardsIterator(Cards cards) {
            this.cards = cards;
        }

        @Override
        public boolean hasNext() {
            return i < cards.size;
        }

        @Override
        public CardEnum next() {
            return cards.cards[i++];
        }
    }

    @Override
    public Iterator<CardEnum> iterator() {
        return new CardsIterator(this);
    }

    @Override
    public void forEach(Consumer<? super CardEnum> action) {
        for (int i = 0; i < size; i++) {
            action.accept(cards[i]);
        }
    }

    @Override
    public Spliterator<CardEnum> spliterator() {
        return Arrays.spliterator(cards, 0, size);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(cards[i].toString());
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Cards cardEnums = (Cards) o;

        if (size != cardEnums.size) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(cards, cardEnums.cards);
    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + Arrays.hashCode(cards);
        return result;
    }
}
