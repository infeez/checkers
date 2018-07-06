package com.infeez.simple;

import com.infeez.simple.base.CheckerApplication;
import com.infeez.simple.screens.GameScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends CheckerApplication {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private GameScreen gameScreen;

    public void create() {
        super.create();
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    public void dispose() {
        ResourceSingleton.dispose();
    }

}
