package com.infeez.simple.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.infeez.simple.Cells;
import com.infeez.simple.input.PCInputProcessor;
import com.infeez.simple.WorldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Board extends GameObject implements PCInputProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    private final Cells cells = new Cells();

    private static boolean clicked = false;

    public Board(SpriteBatch spriteBatch) {
        super(0, 0, 0, 400, 400, spriteBatch);
    }

    public void create() {
        float x = 0, y = 0;
        for (int i = 0; i < Cells.COUNT_CELL; i++) {
            for (int j = 0; j < Cells.COUNT_CELL; j++) {
                WorldType type = WorldType.BLACK;
                if ((j % 2 == 1 && i % 2 == 1) || (j % 2 == 0 && i % 2 == 0)) {
                    type = WorldType.WHITE;
                }
                Cell cell = new Cell(x, y, type, batch);
                cells.setCell(cell, i, j);
                x += Cell.CELL_SIZE;
            }
            x = 0;
            y += Cell.CELL_SIZE;
        }

        cells.getCell(1, 1).setChecker(WorldType.WHITE);
    }

    public void draw() {
        cells.forEach(GameObject::draw);
        cells.forEach(Cell::drawChecker);
    }

    public void update() {
        cells.forEach(GameObject::update);
    }

    public void dispose() {
        cells.forEach(GameObject::dispose);
    }

    public void mouseClickMove(int x, int y) {
        cells.forEach(cell -> {
            if (cell.isChecker() && cell.contains(x, y)) {
                cell.captureChecker(x, y);
                clicked = true;
            }
        });
    }

    public void mouseDown(int x, int y, int pointer, MouseButton button) {
    }

    public void mouseUp(int x, int y, int pointer, MouseButton button) {
        if (clicked) {
            WorldType type = null;
            for (Cell cell : cells) {
                if (cell.isChecker() && !cell.contains(x, y)) {
                    type = cell.removeChecker();
                }
            }
            for (Cell cell : cells) {
                if (!cell.isChecker() && cell.contains(x, y) && type != null) {
                    cell.setChecker(type);
                }
            }
            clicked = false;
        }
    }
}
