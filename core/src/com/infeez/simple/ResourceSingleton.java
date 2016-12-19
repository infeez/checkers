package com.infeez.simple;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.*;

public class ResourceSingleton {

    private static Texture cellsTexture;
    private static TextureRegion blackCell;
    private static TextureRegion whiteCell;

    private static Texture checkersTexture;
    private static TextureRegion blackChecker;
    private static TextureRegion whiteChecker;

    private static final Set<Integer> ids = new HashSet<>(1);

    public static Texture getCellsTexture(){
        if(cellsTexture == null){
            cellsTexture = new Texture(Gdx.files.internal("cells.jpg"));
        }
        return cellsTexture;
    }

    public static Texture getCheckersTexture(){
        if(checkersTexture == null){
            checkersTexture = new Texture(Gdx.files.internal("checkers.png"));
        }
        return checkersTexture;
    }

    public static TextureRegion getBlackCell(){
        if(blackCell == null){
            blackCell = new TextureRegion(getCellsTexture(), 50, 0, 50, 50);
        }
        return blackCell;
    }

    public static TextureRegion getWhiteCell(){
        if(whiteCell == null){
            whiteCell = new TextureRegion(getCellsTexture(), 0, 0, 50, 50);
        }
        return whiteCell;
    }

    public static TextureRegion getBlackChecker(){
        if(blackChecker == null){
            blackChecker = new TextureRegion(getCheckersTexture(), 50, 0, 50, 50);
        }
        return blackChecker;
    }

    public static TextureRegion getWhiteChecker(){
        if(whiteChecker == null){
            whiteChecker = new TextureRegion(getCheckersTexture(), 0, 0, 50, 50);
        }
        return whiteChecker;
    }

    public static int getUniqueId(){
        int rnd = new Random().nextInt(32 * 32);
        if(!ids.add(rnd)) {
            return getUniqueId();
        }
        return rnd;
    }

    public static void dispose(){
        if(cellsTexture == null) {
            return;
        }
        cellsTexture.dispose();
    }
}
