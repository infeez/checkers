package com.infeez.simple.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.infeez.simple.ResourceSingleton;
import com.infeez.simple.base.GameSpriteBatch;
import com.infeez.simple.utils.BoardArrayPosition;
import com.infeez.simple.utils.BoardCommandUtil;
import com.infeez.simple.utils.Constants.GameEnvTypes;

public class Cell extends GameObject {

    public static final int CELL_SIZE = 50;

    private GameEnvTypes type;
    private Checker checker;
    private Checker capturedChecker;
    private int arrayPosI;
    private int arrayPosJ;

    public Cell(int arrayPosI, int arrayPosJ,
                float x, float y, GameEnvTypes type, GameSpriteBatch batch) {
        super(ResourceSingleton.getUniqueId(), x, y, CELL_SIZE, CELL_SIZE, batch);
        this.type = type;
        this.arrayPosI = arrayPosI;
        this.arrayPosJ = arrayPosJ;
        TextureRegion textureRegion;
        if (type.equals(GameEnvTypes.WHITE)) {
            textureRegion = ResourceSingleton.getWhiteCell();
        } else if (type.equals(GameEnvTypes.BLACK)) {
            textureRegion = ResourceSingleton.getBlackCell();
        } else {
            textureRegion = ResourceSingleton.getWhiteCell();
        }
        setTextureRegion(textureRegion);
    }

    public String getBoardStringPosition() {
        return BoardCommandUtil.checkerPositionToCommand(getBoardPosition());
    }

    public BoardArrayPosition getBoardPosition() {
        return new BoardArrayPosition(arrayPosI, arrayPosJ);
    }

    public Checker getChecker() {
        return checker;
    }

    public boolean isBlackType() {
        return type.equals(GameEnvTypes.BLACK);
    }

    public boolean isWhiteType() {
        return type.equals(GameEnvTypes.WHITE);
    }

    public boolean isChecker() {
        return checker != null;
    }

    public Checker setChecker(GameEnvTypes type) {
        this.checker = new Checker(getBoardPosition(), getX(), getY(), type, batch);
        return this.checker;
    }

    public GameEnvTypes removeChecker() {
        capturedChecker = null;
        if(checker == null){
            return null;
        }
        GameEnvTypes worldType = checker.getType();
        this.checker = null;
        return worldType;
    }

    public void drawChecker() {
        if (checker == null) {
            return;
        }
        batch.draw(checker);
    }

    public void drawCapturedChecker(){
        if(capturedChecker == null) {
            return;
        }
        batch.draw(capturedChecker);
    }

    public String toString() {
        return "Cell{" +
                "type=" + type +
                ", checker=" + checker +
                '}' + '\n' + "from " + super.toString();
    }

    public void captureChecker(int x, int y) {
        if (checker == null) {
            return;
        }
        checker.setX(x - CELL_SIZE / 2);
        checker.setY(y - CELL_SIZE / 2);
        capturedChecker = checker;
    }
}
