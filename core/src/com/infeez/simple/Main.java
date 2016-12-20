package com.infeez.simple;

import com.infeez.simple.base.CheckerApplication;
import com.infeez.simple.entity.Board;
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

	protected void boardRender() {
		board.draw();
		board.update();
	}

	public void dispose () {
		batch.dispose();
		board.dispose();
		ResourceSingleton.dispose();
	}
}
