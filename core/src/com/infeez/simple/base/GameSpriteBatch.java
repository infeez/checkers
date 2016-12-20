package com.infeez.simple.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.infeez.simple.entity.GameObject;

public class GameSpriteBatch extends SpriteBatch {

    public void draw(GameObject gameObject){
        draw(gameObject.getTextureRegion(), gameObject.getX(), gameObject.getY(), gameObject.getWidth(), gameObject.getHeight());
    }

}
