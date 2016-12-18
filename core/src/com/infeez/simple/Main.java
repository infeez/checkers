package com.infeez.simple;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends ApplicationAdapter {

	Logger logger = LoggerFactory.getLogger(Main.class);

	SpriteBatch batch;

	Board board;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		board = new Board(batch);
		board.create();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		board.draw();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
