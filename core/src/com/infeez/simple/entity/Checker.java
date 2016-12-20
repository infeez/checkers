package com.infeez.simple.entity;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.infeez.simple.ResourceSingleton;
import com.infeez.simple.utils.BoardCommandUtil;
import com.infeez.simple.utils.CheckerPosition;
import com.infeez.simple.utils.Constants.*;

public class Checker extends GameObject {

    private GameEnvTypes type;
    private int arrayPosI;
    private int arrayPosJ;

    public Checker(CheckerPosition checkerPosition,
                   float x, float y, GameEnvTypes type,
                   SpriteBatch batch) {
        super(ResourceSingleton.getUniqueId(), x, y, 50, 50, batch);
        this.type = type;
        this.arrayPosI = checkerPosition.getIndexFirst();
        this.arrayPosJ = checkerPosition.getIndexSecond();
        TextureRegion textureRegion;
        if (type.equals(GameEnvTypes.WHITE)) {
            textureRegion = ResourceSingleton.getWhiteChecker();
        } else if (type.equals(GameEnvTypes.BLACK)) {
            textureRegion = ResourceSingleton.getBlackChecker();
        } else {
            textureRegion = ResourceSingleton.getWhiteChecker();
        }
        setTextureRegion(textureRegion);
    }

    public boolean isBlackType() {
        return type.equals(GameEnvTypes.BLACK);
    }

    public boolean isWhiteType() {
        return type.equals(GameEnvTypes.WHITE);
    }

    public String getBoardStringPosition(){
        return BoardCommandUtil.checkerPositionToCommand(getBoardPosition());
    }

    public CheckerPosition getBoardPosition(){
        return new CheckerPosition(arrayPosI, arrayPosJ);
    }

    public GameEnvTypes getType() {
        return type;
    }
}
