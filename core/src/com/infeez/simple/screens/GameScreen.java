package com.infeez.simple.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.infeez.simple.base.CheckerApplication;
import com.infeez.simple.base.GameSpriteBatch;
import com.infeez.simple.entity.Board;


public class GameScreen implements Screen {

    private Board board;
    private GameSpriteBatch batch;
    private OrthographicCamera camera;

    public GameScreen(CheckerApplication context){
        batch = new GameSpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);
        board = new Board(batch);
        board.create();
        board.startNewGame();
        context.setPCInputProcessor(board);
    }

    public void show() {

    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        board.draw();
        board.update();
        batch.end();
    }

    public void resize(int width, int height) {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void hide() {

    }

    public void dispose() {
        batch.dispose();
        board.dispose();
    }
}
