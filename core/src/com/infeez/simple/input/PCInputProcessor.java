package com.infeez.simple.input;

public interface PCInputProcessor {

    void mouseClickMove(int x, int y);

    void mouseDown(int x, int y, int pointer, MouseButton button);

    void mouseUp(int x, int y, int pointer, MouseButton button);

    enum MouseButton {
        RIGHT,
        LEFT
    }
}
