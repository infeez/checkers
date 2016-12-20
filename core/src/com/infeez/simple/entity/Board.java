package com.infeez.simple.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.infeez.simple.Cells;
import com.infeez.simple.ResourceSingleton;
import com.infeez.simple.input.PCInputProcessor;
import com.infeez.simple.utils.Constants.GameEnvTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Board extends GameObject implements PCInputProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    private final Cells cells = new Cells();

    private boolean dragged = false;

    public Board(SpriteBatch spriteBatch) {
        super(ResourceSingleton.getUniqueId(), 0, 0, 400, 400, spriteBatch);
    }

    public void create() {
        cells.createBoard(batch);

        cells.getCell(1, 1).setChecker(GameEnvTypes.WHITE);
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

    public void mouseDrag(int x, int y) {
        cells.forEach(cell -> {
            if (cell.isChecker()) {
                cell.captureChecker(x, y);
                dragged = true;
            }
        });
    }

    public void mouseDown(int x, int y, int pointer, int mouseButton) {
        Checker checker = cells.findCheckerByCoords(x, y);
        if(checker != null) {
            logger.info(checker.toString());
        }
    }

    public void mouseUp(int x, int y, int pointer, int mouseButton) {
        if (!dragged) {
            return;
        }
        GameEnvTypes type = null;
        for (Cell cell : cells) {
            if (cell.isChecker() && !cell.contains(x, y)) {
                type = cell.removeChecker();
                break;
            }
        }
        if(type != null){
            for (Cell cell : cells) {
                if (!cell.isChecker() && cell.contains(x, y)) {
                    cell.setChecker(type);
                    break;
                }
            }
        }
        dragged = false;
    }

    void moveChecker(){

    }
}
