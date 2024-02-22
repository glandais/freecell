package io.github.glandais.solitaire.klondike.cards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ColorEnum {
    BLACK('B'), RED('R');

    final char label;
}
