package com.infeez.simple;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Checker extends GameObject {

    private WorldType type;

    public Checker(float x, float y, WorldType type,
                   SpriteBatch batch) {
        super(ResourceSingleton.getUniqueId(), x, y, 50, 50, batch);
        this.type = type;
        TextureRegion textureRegion;
        if (type.equals(WorldType.WHITE)) {
            textureRegion = ResourceSingleton.getWhiteChecker();
        } else if (type.equals(WorldType.BLACK)) {
            textureRegion = ResourceSingleton.getBlackChecker();
        } else {
            textureRegion = ResourceSingleton.getWhiteChecker();
        }
        setTextureRegion(textureRegion);
    }

    public void dispose() {

    }

    public WorldType getType() {
        return type;
    }
}
