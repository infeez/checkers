package com.infeez.simple.entity;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.infeez.simple.ResourceSingleton;
import com.infeez.simple.base.GameSpriteBatch;
import com.infeez.simple.utils.BoardCommandUtil;
import com.infeez.simple.utils.BoardArrayPosition;
import com.infeez.simple.utils.Constants.GameEnvTypes;

public class Checker extends GameObject {

    private GameEnvTypes type;
    private int arrayPosI;
    private int arrayPosJ;

    public Checker(BoardArrayPosition boardArrayPosition,
                   float x, float y, GameEnvTypes type,
                   GameSpriteBatch batch) {
        super(ResourceSingleton.getUniqueId(), x, y, 50, 50, batch);
        this.type = type;
        this.arrayPosI = boardArrayPosition.getIndexFirst();
        this.arrayPosJ = boardArrayPosition.getIndexSecond();
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

    public BoardArrayPosition getBoardPosition(){
        return new BoardArrayPosition(arrayPosI, arrayPosJ);
    }

    public GameEnvTypes getType() {
        return type;
    }
}
