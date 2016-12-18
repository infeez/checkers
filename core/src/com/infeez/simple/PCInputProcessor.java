package com.infeez.simple;

public interface PCInputProcessor {

    void mouseMove(int x, int y);

    void mouseDown(int x, int y, int pointer, MouseButton button);

    void mouseUp(int x, int y, int pointer, MouseButton button);

    enum MouseButton {
        RIGHT,
        LEFT
    }
}
