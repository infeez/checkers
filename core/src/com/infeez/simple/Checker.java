package com.infeez.simple;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Checker extends GameObject {

    public Checker(float x, float y, WorldType type,
                   SpriteBatch batch) {
        super(ResourceSingleton.getUniqueId(), x, y, 50, 50, batch);
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
