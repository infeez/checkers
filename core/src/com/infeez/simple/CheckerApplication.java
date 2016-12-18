package com.infeez.simple;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class CheckerApplication extends ApplicationAdapter implements InputProcessor {

    private PCInputProcessor pcInputProcessor;
    protected SpriteBatch batch;

    public void create () {
        Gdx.input.setInputProcessor(this);
        batch = new SpriteBatch();
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
            this.pcInputProcessor.mouseDown(screenX, screenY, pointer, button == 1
                    ? PCInputProcessor.MouseButton.RIGHT : PCInputProcessor.MouseButton.LEFT);
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(this.pcInputProcessor != null){
            this.pcInputProcessor.mouseUp(screenX, screenY, pointer, button == 1
                    ? PCInputProcessor.MouseButton.RIGHT : PCInputProcessor.MouseButton.LEFT);
        }
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        if(this.pcInputProcessor != null){
            this.pcInputProcessor.mouseMove(screenX, screenY);
        }
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }

    protected void setPCInputProcessor(PCInputProcessor l){
        this.pcInputProcessor = l;
    }
}
