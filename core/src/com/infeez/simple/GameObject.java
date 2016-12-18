package com.infeez.simple;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.sun.istack.internal.NotNull;

public abstract class GameObject {

    private int id;
    private Rectangle rectangle;
    private Texture texture;
    private TextureRegion textureRegion;
    protected SpriteBatch batch;

    public GameObject(int id, float x, float y,
                      float width, float height,
                      SpriteBatch batch){
        this.id = id;
        this.batch = batch;
        this.rectangle = new Rectangle(x, y, width, height);
    }

    public int getId(){
        return id;
    }

    public float getX() {
        return rectangle.getX();
    }

    public float getY() {
        return rectangle.getY();
    }

    public float getWidth() {
        return rectangle.getWidth();
    }

    public float getHeight() {
        return rectangle.getHeight();
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setX(float x){
        rectangle.setX(x);
    }

    public void setY(float y){
        rectangle.setY(y);
    }

    public void setWidth(float width){
        rectangle.setWidth(width);
    }

    public void setHeight(float height){
        rectangle.setHeight(height);
    }

    public void setRectangle(Rectangle rectangle){
        rectangle.set(rectangle);
    }

    public void setTextureRegion(@NotNull TextureRegion textureRegion){
        this.textureRegion = textureRegion;
    }

    public void setTexture(@NotNull Texture texture){
        this.texture = texture;
    }

    public void draw(){
        if(batch == null){
            return;
        }
        if(texture != null && textureRegion == null){
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
        if(textureRegion != null && texture == null){
            batch.draw(textureRegion, getX(), getY(), getWidth(), getHeight());
        }
    }

    public void update(){
    }

    public boolean contains(GameObject gameObject){
        return this.rectangle.contains(gameObject.getRectangle());
    }

    public boolean contains(int x, int y){
        return this.rectangle.contains(x, y);
    }

    public void dispose() {
        if(texture == null) {
            return;
        }
        texture.dispose();
    }

    public String toString() {
        return "GameObject{" +
                "id=" + id +
                '}';
    }
}
