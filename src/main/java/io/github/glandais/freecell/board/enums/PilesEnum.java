package io.github.glandais.freecell.board.enums;

public enum PilesEnum {
    STOCK("Stock"),
    SUITE_HEART("♡ suite"),
    SUITE_DIAMOND("♢ suite"),
    SUITE_CLUB("♧ suite"),
    SUITE_SPADE("♤ suite"),
    TABLEAU_1("Tableau 1"),
    TABLEAU_2("Tableau 2"),
    TABLEAU_3("Tableau 3"),
    TABLEAU_4("Tableau 4"),
    TABLEAU_5("Tableau 5"),
    TABLEAU_6("Tableau 6"),
    TABLEAU_7("Tableau 7");

    final String label;

    PilesEnum(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
