package com.mygdx.game;

import com.badlogic.gdx.Game;


public class GameOfLife extends Game {


    @Override
    public void create() {
        this.setScreen(new MainGame());
    }


    public void render() {
        super.render();
    }

    public void dispose() {
    }
}
