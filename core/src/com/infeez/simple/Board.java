package com.infeez.simple;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class Board {

    int COUNT_CELL = 8;
    Cell[][] cells = new Cell[COUNT_CELL][COUNT_CELL];
    SpriteBatch spriteBatch;

    public Board(SpriteBatch spriteBatch){
        this.spriteBatch = spriteBatch;
    }

    public void create(){
        float x = 0, y = 0;
        for(int i = 0; i < COUNT_CELL; i++){
            for(int j = 0; j < COUNT_CELL; j++){
                WorldType type = WorldType.BLACK;
                if ((j % 2 == 1 && i % 2 == 1) || (j % 2 == 0 && i % 2 == 0)) {
                    type = WorldType.WHITE;
                }
                cells[i][j] = new Cell(x, y, type, spriteBatch);
                x += Cell.CELL_SIZE;
            }
            x = 0;
            y += Cell.CELL_SIZE;
        }
    }

    public void draw(){
        for (Cell[] x: cells) {
            for (Cell y: x) {
                y.draw();

            }
        }
    }

    public void update(){

    }

}
