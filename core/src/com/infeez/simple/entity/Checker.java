package com.infeez.simple.entity;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.infeez.simple.ResourceSingleton;
import com.infeez.simple.utils.Constants.*;

public class Checker extends GameObject {

    private GameEnvTypes type;

    public Checker(float x, float y, GameEnvTypes type,
                   SpriteBatch batch) {
        super(ResourceSingleton.getUniqueId(), x, y, 50, 50, batch);
        this.type = type;
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

    public void dispose() {

    }

    public GameEnvTypes getType() {
        return type;
    }
}
