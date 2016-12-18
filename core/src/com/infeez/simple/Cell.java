package com.infeez.simple;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Cell extends GameObject {

    public static final float CELL_SIZE = 50;

    private WorldType type;

    private Checker checker;

    public Cell(float x, float y, WorldType type, SpriteBatch batch){
        super(ResourceSingleton.getUniqueId(), x, y, CELL_SIZE, CELL_SIZE, batch);
        this.type = type;
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

    public Checker getChecker(){
        return checker;
    }

    public boolean isChecker(){
        return checker != null;
    }

    public void setChecker(WorldType type) {
        this.checker = new Checker(getX(), getY(), type, batch);
    }

    public WorldType removeChecker(){
        WorldType worldType = checker.getType();
        this.checker = null;
        return worldType;
    }

    public void drawChecker() {
        if(checker == null) {
            return;
        }
        batch.draw(checker.getTextureRegion(), checker.getX(), checker.getY(), checker.getWidth(), checker.getHeight());

    }

    public void dispose() {

    }

    public String toString() {
        return "Cell{" +
                "type=" + type +
                ", checker=" + checker +
                '}' + '\n' + "from "+ super.toString();
    }

    public void captureChecker(int x, int y) {
        if(checker == null){
            return;
        }
        checker.setX(x);
        checker.setY(y);
    }
}
