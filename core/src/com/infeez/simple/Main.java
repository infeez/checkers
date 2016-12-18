package com.infeez.simple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends CheckerApplication {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private Board board;

	public void create () {
		super.create();
		board = new Board(batch);
		board.create();
		setPCInputProcessor(board);
	}

	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		board.draw();
		board.update();
		batch.end();
	}

	public void dispose () {
		batch.dispose();
		board.dispose();
		ResourceSingleton.dispose();
	}
}
