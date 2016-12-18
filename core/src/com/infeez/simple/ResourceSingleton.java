package com.infeez.simple;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.*;

public class ResourceSingleton {

    private static Texture cellsTexture;
    private static TextureRegion blackCell;
    private static TextureRegion whiteCell;

    private static final Set<Integer> ids = new HashSet<>(1);

    public static Texture getCellsTexture(){
        if(cellsTexture == null){
            cellsTexture = new Texture(Gdx.files.internal("core\\assets\\cells.jpg"));
        }
        return cellsTexture;
    }

    public static TextureRegion getBlackCell(){
        if(blackCell == null){
            blackCell = new TextureRegion(getCellsTexture(), 0, 0, 50, 50);
        }
        return blackCell;
    }

    public static TextureRegion getWhiteCell(){
        if(whiteCell == null){
            whiteCell = new TextureRegion(getCellsTexture(), 50, 0, 50, 50);
        }
        return whiteCell;
    }

    public static int getUniqueId(){
        int rnd = new Random().nextInt(32 * 32);
        if(!ids.add(rnd)) {
            return getUniqueId();
        }
        return rnd;
    }
}
