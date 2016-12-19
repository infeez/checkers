package com.infeez.simple.input;

public interface PCInputProcessor {

    void mouseDrag(int x, int y);

    void mouseDown(int x, int y, int pointer, int mouseButton);

    void mouseUp(int x, int y, int pointer, int mouseButton);

}
