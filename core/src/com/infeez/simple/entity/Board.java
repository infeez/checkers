package com.infeez.simple.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.infeez.simple.Cells;
import com.infeez.simple.ResourceSingleton;
import com.infeez.simple.input.PCInputProcessor;
import com.infeez.simple.utils.BoardCommandUtil;
import com.infeez.simple.utils.CheckerPosition;
import com.infeez.simple.utils.Constants.GameEnvTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Board extends GameObject implements PCInputProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    private final Cells cells = new Cells();
    private boolean dragged = false;
    private Cell cellForDrag;

    public Board(SpriteBatch spriteBatch) {
        super(ResourceSingleton.getUniqueId(), 0, 0, 400, 400, spriteBatch);
    }

    public void create() {
        cells.createBoard(batch);
        cells.startCellsPosition();
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
        if(cellForDrag != null) {
            cellForDrag.captureChecker(x, y);
            dragged = true;
        } else {
            cellForDrag = cells.findCellByCoordinatesAndHaveChecker(x, y);
        }
    }

    public void mouseDown(int x, int y, int pointer, int mouseButton) {
    }

    public void mouseUp(int x, int y, int pointer, int mouseButton) {
        if (!dragged || cellForDrag == null) {
            return;
        }
        final GameEnvTypes type = cellForDrag.removeChecker();
        if(type == null){
            return;
        }
        Checker newChecker = null;
        for(Cell cell : cells){
            if (!cell.isChecker() && cell.contains(x, y) && cell.isBlackType()) {
                newChecker = cell.setChecker(type);
                break;
            } else if((cell.isChecker() && cell.contains(x, y)) || (!cell.isBlackType() && cell.contains(x, y))) {
                cellForDrag.setChecker(type);
                break;
            }
        }
        if(newChecker != null) {
            System.out.println("From " + cellForDrag.getBoardStringPosition() + " to " + newChecker.getBoardStringPosition());
        }
        cellForDrag = null;
        dragged = false;
    }

    public void moveChecker(String from, String to){
        CheckerPosition chPosFrom = BoardCommandUtil.parseCommand(from);
        CheckerPosition chPosTo = BoardCommandUtil.parseCommand(to);
        if(chPosFrom == null || chPosTo == null){
            return;
        }

        Checker checkToMove = cells.getCell(chPosFrom).getChecker();
        if(checkToMove == null){
            return;
        }

        cells.getCell(chPosFrom).removeChecker();

        Cell cellTarget = cells.getCell(chPosTo);
        cellTarget.setChecker(checkToMove.getType());
    }
}
