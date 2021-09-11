package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

public class Tile extends Actor {
    private final int x;
    private final int y;
    private final Sprite base;
    private boolean alive = false;
    private String nextMove = "stay";
    private final List<Tile> connectingTiles = new ArrayList<>();
    private final List<Tile> closestTiles = new ArrayList<>();
    private final MainGame game;
    private  Pixmap pixmap;


    public Tile(int x, int y, final MainGame game) {
        this.x = x;
        this.y = y;
        this.game = game;
        int TILE_WIDTH_AND_HEIGHT = MainGame.getTileWidthAndHeight();
        pixmap = new Pixmap(TILE_WIDTH_AND_HEIGHT, TILE_WIDTH_AND_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(133/255f, 133/255f, 133/255f, 1f);
        //leave a few pixels between tiles to form grid.
        pixmap.fillRectangle(0, 0, TILE_WIDTH_AND_HEIGHT, TILE_WIDTH_AND_HEIGHT);
        base = new Sprite(new Texture(pixmap));
        base.setOrigin(base.getWidth() / 2, base.getHeight() / 2);
        setTouchable(Touchable.enabled);
        base.setPosition(TILE_WIDTH_AND_HEIGHT * x, TILE_WIDTH_AND_HEIGHT * y);
        base.setColor(133/255f, 133/255f, 133/255f, 1f);
        setBounds(base.getX(), base.getY(), base.getWidth(), base.getHeight());

        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (game.isContinuousAdding()) {
                    setToAlive();
                } else if (game.isContinuousDeleting()) {
                    setToDead();
                }
            }

        });


    }

    public void setAliveNext() {
        if (!alive) {
            setToAlive();
        } else {
            setToDead();
        }
    }


    public void setToDead() {
        if (alive) {
            alive = false;

            base.setColor(133/255f, 133/255f, 133/255f, 1f);
            game.removeAliveCell();
        }
    }

    public void setToAlive() {
        if (!alive) {
            alive = true;
            base.setColor(255/255f, 255/255f, 255/255f, 1f);
            game.addAliveCell();
        }
    }

    public void endMove() {

        if (nextMove.equals("alive")) {
            setAliveNext();
        } else if (nextMove.equals("dead")) {
            setToDead();
        }
        nextMove = "stay";
    }


    public void startMove() {
        int aliveCells = 0;
        for (Tile tile : connectingTiles) {
            if (tile.isAlive()) {
                aliveCells++;
            }
        }

        if (alive && (aliveCells < 2 || aliveCells > 3)) {
            nextMove = "dead";

        } else if (!alive && aliveCells == 3) {
            nextMove = "alive";
        } else {
            nextMove = "stay";
        }


    }

    public void fill() {
        setToAlive();
        for (Tile tile : closestTiles) {
            if (!tile.isAlive()) {
                tile.fill();
            }

        }

    }

    public void addConnection(Tile tile) {
        if (!connectingTiles.contains(tile)) {
            connectingTiles.add(tile);
        }
    }

    public void addClosestTiles(Tile tile) {
        if (!closestTiles.contains(tile)) {
            closestTiles.add(tile);
        }
    }

    public int getXNr() {
        return x;
    }

    public int getYNr() {
        return y;
    }

    public boolean isAlive() {
        return alive;
    }


    @Override
    public void draw(Batch batch, float alpha) {
        base.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

}
