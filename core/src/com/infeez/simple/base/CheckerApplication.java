package com.infeez.simple.base;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.infeez.simple.input.PCInputProcessor;

public abstract class CheckerApplication extends Game implements InputProcessor {

    private PCInputProcessor pcInputProcessor;

    public void create () {
        Gdx.input.setInputProcessor(this);
    }

    public void render () {
        super.render();
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

    public void setPCInputProcessor(PCInputProcessor l){
        this.pcInputProcessor = l;
    }
}
