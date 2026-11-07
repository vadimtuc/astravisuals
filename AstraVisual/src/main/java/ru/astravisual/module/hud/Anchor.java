package ru.astravisual.module.hud;

public enum Anchor {
    TOP_LEFT("Top Left"),
    TOP_RIGHT("Top Right"),
    BOTTOM_LEFT("Bottom Left"),
    BOTTOM_RIGHT("Bottom Right"),
    CENTER("Center");

    private final String label;

    Anchor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
