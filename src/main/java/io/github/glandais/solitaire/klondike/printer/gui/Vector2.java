package io.github.glandais.solitaire.klondike.printer.gui;

import lombok.Data;

@Data
public class Vector2 {
    public double x;
    public double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Vector2 position) {
        return Math.hypot(x - position.x, y - position.y);
    }
}
