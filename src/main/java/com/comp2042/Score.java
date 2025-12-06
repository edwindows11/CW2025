package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty lines = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty linesProperty() {
        return lines;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public void add(int i) {
        score.setValue(score.getValue() + i);
    }

    public void addLines(int i) {
        lines.setValue(lines.getValue() + i);
    }

    public void setLevel(int i) {
        level.setValue(i);
    }

    public int getLevel() {
        return level.getValue();
    }

    public void reset() {
        score.setValue(0);
        lines.setValue(0);
        level.setValue(1);
    }
}
