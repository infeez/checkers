package com.infeez.simple;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Board extends GameObject implements PCInputProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    private final Cells cells = new Cells();

    public Board(SpriteBatch spriteBatch){
        super(0, 0, 0, 400, 400, spriteBatch);
    }

    public void create(){
        float x = 0, y = 0;
        for(int i = 0; i < Cells.COUNT_CELL; i++){
            for(int j = 0; j < Cells.COUNT_CELL; j++){
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
    }

    public void draw(){
        cells.forEach(GameObject::draw);
    }

    public void update(){

    }

    public void dispose() {
        cells.forEach(GameObject::dispose);
    }

    public void mouseMove(int x, int y) {

    }

    public void mouseDown(int x, int y, int pointer, MouseButton button) {
        cells.forEach(cell -> {
            if(cell.contains(x, y)){
                logger.error(cell.toString() + " by mouse " + button);
            }
        });
    }

    public void mouseUp(int x, int y, int pointer, MouseButton button) {

    }
}
