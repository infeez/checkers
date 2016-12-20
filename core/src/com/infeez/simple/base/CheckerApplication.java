package com.infeez.simple.base;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.infeez.simple.input.PCInputProcessor;

public abstract class CheckerApplication extends ApplicationAdapter implements InputProcessor {

    private PCInputProcessor pcInputProcessor;
    protected GameSpriteBatch batch;
    protected OrthographicCamera cam;

    public void create () {
        Gdx.input.setInputProcessor(this);
        batch = new GameSpriteBatch();
        cam = new OrthographicCamera();
        cam.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(cam.combined);
    }

    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (cam != null) {
            cam.update();
        }
        batch.begin();
        boardRender();
        batch.end();
    }

    public boolean keyDown(int keycode) {
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(this.pcInputProcessor != null){
            this.pcInputProcessor.mouseDown(screenX, screenY, pointer, button);
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(this.pcInputProcessor != null){
            this.pcInputProcessor.mouseUp(screenX, screenY, pointer, button);
        }
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(this.pcInputProcessor != null){
            this.pcInputProcessor.mouseDrag(screenX, screenY);
        }
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }

    protected void setPCInputProcessor(PCInputProcessor l){
        this.pcInputProcessor = l;
    }

    protected abstract void boardRender();
}
