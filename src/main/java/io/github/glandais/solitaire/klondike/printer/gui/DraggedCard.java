package io.github.glandais.solitaire.klondike.printer.gui;

public record DraggedCard(PrintableCard printableCard, double origX, double origY, int origZ) {
    DraggedCard(PrintableCard printableCard) {
        this(printableCard, printableCard.position.x, printableCard.position.y, printableCard.zIndex);
    }
}
