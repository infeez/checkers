package com.infeez.simple;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Cell extends GameObject {

    public static final float CELL_SIZE = 50;

    public Cell(float x, float y, WorldType type, SpriteBatch batch){
        super(ResourceSingleton.getUniqueId(), x, y, CELL_SIZE, CELL_SIZE, batch);
        TextureRegion textureRegion;
        if (type.equals(WorldType.WHITE)) {
            textureRegion = ResourceSingleton.getWhiteCell();
        } else if (type.equals(WorldType.BLACK)) {
            textureRegion = ResourceSingleton.getBlackCell();
        } else {
            textureRegion = ResourceSingleton.getWhiteCell();
        }
        setTextureRegion(textureRegion);

    }

}
