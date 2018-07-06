package com.infeez.simple.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.infeez.simple.base.GameSpriteBatch;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class GameObject implements Cloneable {

    private int id;
    private Rectangle rectangle;
    private TextureRegion textureRegion;
    protected GameSpriteBatch batch;

    public GameObject(int id, float x, float y,
                      float width, float height,
                      GameSpriteBatch batch){
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

    public TextureRegion getTextureRegion() {
        return textureRegion;
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

    public void setRectangle(@Nonnull Rectangle rectangle){
        rectangle.set(rectangle);
    }

    public void setTextureRegion(@Nonnull TextureRegion textureRegion){
        this.textureRegion = textureRegion;
    }

    public void draw(){
        if(batch == null){
            return;
        }
        if(textureRegion == null) {
            return;
        }
        batch.draw(this);
    }

    public void update(){
    }

    public boolean contains(@Nonnull GameObject gameObject){
        return contains(gameObject.getX(), gameObject.getY());
    }

    public boolean contains(float x, float y){
        return this.rectangle.contains(x, y);
    }

    public void dispose() {
    }

    public String toString() {
        return "GameObject{" +
                "id=" + id +
                '}';
    }

    public GameObject cloneGameObject() throws CloneNotSupportedException {
        return (GameObject) this.clone();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameObject that = (GameObject) o;
        return id == that.id;
    }

    public int hashCode() {
        return Objects.hash(id);
    }
}
