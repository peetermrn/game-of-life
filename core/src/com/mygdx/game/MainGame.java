package com.mygdx.game;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.*;


public class MainGame implements Screen, InputProcessor {
    private static final double REFRESH_FREQUENCY = 0.1;
    private static final int ZERO = 0;
    private static final int CAMERA_MOVE_AMOUNT = 20;
    private static final int TILE_WIDTH_AND_HEIGHT = 20;
    private final Stage stage;
    private final OrthographicCamera camera;
    private Actor clickedTile;
    private float currentZoom = 3f;
    private float timeSinceLastRun = 0;

    private boolean running = false;

    private final List<Tile> tiles = new ArrayList<>();
    private int liveCells = 0;
    private final int mapWidthAndHeightInTiles = 80;
    private final BitmapFont instructions;
    private final BitmapFont info;
    private final SpriteBatch batch;
    private int generations = 0;
    private final List<List<Integer>> allKeys = new ArrayList<>();
    private final List<List<Integer>> gGliderGun = new ArrayList<>(Arrays.asList(Arrays.asList(10, 67), Arrays.asList(10, 68), Arrays.asList(11, 67), Arrays.asList(11, 68), Arrays.asList(20, 66), Arrays.asList(20, 67), Arrays.asList(20, 68), Arrays.asList(21, 65), Arrays.asList(21, 69), Arrays.asList(22, 64), Arrays.asList(22, 70), Arrays.asList(23, 64), Arrays.asList(23, 70), Arrays.asList(24, 67), Arrays.asList(25, 65), Arrays.asList(25, 69), Arrays.asList(26, 66), Arrays.asList(26, 67), Arrays.asList(26, 68), Arrays.asList(27, 67), Arrays.asList(30, 68), Arrays.asList(30, 69), Arrays.asList(30, 70), Arrays.asList(31, 68), Arrays.asList(31, 69), Arrays.asList(31, 70), Arrays.asList(32, 67), Arrays.asList(32, 71), Arrays.asList(34, 66), Arrays.asList(34, 67), Arrays.asList(34, 71), Arrays.asList(34, 72), Arrays.asList(44, 69), Arrays.asList(44, 70), Arrays.asList(45, 69), Arrays.asList(45, 70)));
    private final List<List<Integer>> sGliderGun = new ArrayList<>(Arrays.asList(Arrays.asList(9, 68), Arrays.asList(9, 69), Arrays.asList(10, 68), Arrays.asList(10, 69), Arrays.asList(13, 65), Arrays.asList(13, 66), Arrays.asList(14, 65), Arrays.asList(14, 66), Arrays.asList(16, 68), Arrays.asList(16, 69), Arrays.asList(17, 68), Arrays.asList(17, 69), Arrays.asList(30, 51), Arrays.asList(30, 52), Arrays.asList(30, 57), Arrays.asList(30, 58), Arrays.asList(30, 59), Arrays.asList(31, 50), Arrays.asList(31, 52), Arrays.asList(31, 57), Arrays.asList(31, 60), Arrays.asList(32, 50), Arrays.asList(32, 57), Arrays.asList(32, 60), Arrays.asList(33, 49), Arrays.asList(33, 50), Arrays.asList(34, 60), Arrays.asList(35, 56), Arrays.asList(35, 60), Arrays.asList(36, 57), Arrays.asList(36, 59), Arrays.asList(37, 58), Arrays.asList(40, 57), Arrays.asList(40, 58), Arrays.asList(41, 57), Arrays.asList(41, 58)));
    private final List<List<Integer>> infinity = new ArrayList<>(Arrays.asList(Arrays.asList(12, 42), Arrays.asList(13, 42), Arrays.asList(14, 42), Arrays.asList(15, 42), Arrays.asList(16, 42), Arrays.asList(17, 42), Arrays.asList(18, 42), Arrays.asList(19, 42), Arrays.asList(21, 42), Arrays.asList(22, 42), Arrays.asList(23, 42), Arrays.asList(24, 42), Arrays.asList(25, 42), Arrays.asList(29, 42), Arrays.asList(30, 42), Arrays.asList(31, 42), Arrays.asList(38, 42), Arrays.asList(39, 42), Arrays.asList(40, 42), Arrays.asList(41, 42), Arrays.asList(42, 42), Arrays.asList(43, 42), Arrays.asList(44, 42), Arrays.asList(46, 42), Arrays.asList(47, 42), Arrays.asList(48, 42), Arrays.asList(49, 42), Arrays.asList(50, 42)));
    private final Map<List<Integer>, Tile> tileMap = new HashMap<>();
    private boolean continuousAdding = false;
    private boolean continuousDeleting = false;
    private boolean fill = false;
    private final Viewport viewport;
    private boolean firstRender = true;

    public MainGame() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        //camera
        camera = new OrthographicCamera(w, h);
        viewport = new StretchViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.update();
        camera.zoom = 3f;
        //stuff
        instructions = new BitmapFont();
        info = new BitmapFont();
        batch = new SpriteBatch();
        InputMultiplexer multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(this);
        //stage
        stage = new Stage(viewport);
        multiplexer.addProcessor(stage);
        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (stage.hit(x, y, true) == null) {
                    clickedTile = null;
                } else {
                    clickedTile = stage.hit(x, y, true);
                    if (fill) {
                        ((Tile) clickedTile).fill();
                    } else {
                        ((Tile) clickedTile).setAliveNext();
                    }
                }
            }
        });

        //create tiles.
        for (int x = ZERO; x < mapWidthAndHeightInTiles; x++) {
            for (int y = ZERO; y < mapWidthAndHeightInTiles; y++) {
                Tile tile = new Tile(x, y, this);
                stage.addActor(tile);
                tiles.add(tile);
                List<Integer> xAndYAsList = new ArrayList<>(Arrays.asList(x, y));
                tileMap.put(xAndYAsList, tile);
                allKeys.add(xAndYAsList);
            }
        }
        //create connections between tiles.
        ConnectionsAdder connectionsAdder = new ConnectionsAdder(this);
        connectionsAdder.addConnections(tiles, tileMap);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(100 / 255f, 100 / 255f, 100 / 255f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());
        camera.update();
        stage.getViewport().getCamera().update();
        moveCamera();
        if (firstRender) {
            firstRender = false;
            camera.position.set(mapWidthAndHeightInTiles * TILE_WIDTH_AND_HEIGHT / 2f, mapWidthAndHeightInTiles * TILE_WIDTH_AND_HEIGHT / 2f+128, 0);
        }
        //refresh map.
        timeSinceLastRun += Gdx.graphics.getDeltaTime();
        if (liveCells > ZERO && running && timeSinceLastRun > REFRESH_FREQUENCY) {
            runMove();
            timeSinceLastRun = ZERO;
            generations++;
        }
        //text.
        batch.setProjectionMatrix(camera.combined); //or your matrix to draw GAME WORLD, not UI
        batch.begin();
        instructions.getData().setScale(2.5f, 2.5f);
        info.getData().setScale(4, 4);
        String text = "Play/pause - P\n\nSingle - Space\n\nReset - R\n\nZoom - Scroll\n\nZoom in - N\n\nZoom out - M\n\nMove - WASD/mouse\n\nGosper glider gun - G\n\nSimkin glider gun - S\n\ninfinity - I\n\nContinuous adding - C\n\nAll alive - W\n\nContinuous deleting - D\n\nBucket tool - F\n\nAlive Cells - " + liveCells + "\n\nGenerations - " + generations+ "\n\nPlaying - " + running;
        String text2 = "THE GAME OF LIFE\nEvery cell with less that two or more than three alive neighbors dies.\nEvery dead cell with exactly three alive neighbours comes to life.";
        info.draw(batch, text2, ZERO, mapWidthAndHeightInTiles * TILE_WIDTH_AND_HEIGHT + info.getLineHeight() * 3);
        instructions.draw(batch, text, mapWidthAndHeightInTiles * TILE_WIDTH_AND_HEIGHT + 50, mapWidthAndHeightInTiles * TILE_WIDTH_AND_HEIGHT - 50);
        batch.end();

    }

    public void addAliveCell() {
        liveCells++;
    }

    public void removeAliveCell() {
        liveCells--;
    }

    private void moveCamera() {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        if (x <= 35) {
            camera.translate(-CAMERA_MOVE_AMOUNT, 0);
            stage.getViewport().getCamera().translate(-CAMERA_MOVE_AMOUNT, 0, 0);

        }
        if (x >= (Gdx.graphics.getWidth() - 35)) {
            camera.translate(CAMERA_MOVE_AMOUNT, 0);
            stage.getViewport().getCamera().translate(CAMERA_MOVE_AMOUNT, 0, 0);

        }
        if (y <= 35) {
            camera.translate(0, CAMERA_MOVE_AMOUNT);
            stage.getViewport().getCamera().translate(0, CAMERA_MOVE_AMOUNT, 0);

        }
        if (y >= (Gdx.graphics.getHeight() - 35)) {

            camera.translate(0, -CAMERA_MOVE_AMOUNT);
            stage.getViewport().getCamera().translate(0, -CAMERA_MOVE_AMOUNT, 0);

        }
        float startX = camera.viewportWidth / 2;
        float startY = camera.viewportHeight / 2;
        boundaries(camera, startX, startY, mapWidthAndHeightInTiles * TILE_WIDTH_AND_HEIGHT - startX * 2, mapWidthAndHeightInTiles * TILE_WIDTH_AND_HEIGHT - 2 * startY);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            runMove();
        }
        if (keycode == Input.Keys.P) {
            running = !running;
            continuousDeleting = false;
            continuousAdding = false;
            fill = false;
        }
        if (keycode == Input.Keys.G) {
            gosperGliderGun(gGliderGun);
        }
        if (keycode == Input.Keys.S) {
            gosperGliderGun(sGliderGun);
        }
        if (keycode == Input.Keys.W) {
            gosperGliderGun(allKeys);
        }
        if (keycode == Input.Keys.I) {
            gosperGliderGun(infinity);
        }
        if (keycode == Input.Keys.R) {
            reset();
        }
        if (keycode == Input.Keys.N) {
            scrolled(-1,-1);
        }
        if (keycode == Input.Keys.M) {
            scrolled(1,1);
        }
        if (keycode == Input.Keys.F) {
            continuousDeleting = false;
            continuousAdding = false;
            fill = !fill;
        }
        if (keycode == Input.Keys.A) {
            continuousDeleting = false;
            fill = false;
            continuousAdding = !continuousAdding;
        }
        if (keycode == Input.Keys.D) {
            continuousAdding = false;
            fill = false;
            continuousDeleting = !continuousDeleting;
        }
        if (keycode == Input.Keys.LEFT)
            camera.translate(-32, 0);
        if (keycode == Input.Keys.RIGHT)
            camera.translate(32, 0);
        if (keycode == Input.Keys.UP)
            camera.translate(0, 32);
        if (keycode == Input.Keys.DOWN)
            camera.translate(0, -32);


        return true;
    }

    public void gosperGliderGun(List<List<Integer>> preset) {
        for (List<Integer> list : preset) {
            if (tileMap.containsKey(list)) {
                tileMap.get(list).setToAlive();
            }
        }
    }

    private void reset() {
        running = false;
        timeSinceLastRun = ZERO;
        generations = ZERO;
        continuousDeleting = false;
        continuousAdding = false;
        fill = false;
        for (Tile tile : tiles) {
            tile.setToDead();

        }
        liveCells = ZERO;
    }

    private void runMove() {
        for (Tile tile : tiles) {
            tile.startMove();
        }
        for (Tile tile : tiles) {
            tile.endMove();
        }
    }


    public static void boundaries(Camera cam, float startX, float startY, float width, float height) {
        Vector3 position = cam.position;
        if (position.x < startX - 500) {
            position.x = startX - TILE_WIDTH_AND_HEIGHT - 500;
        }
        if (position.y < startY - 500) {
            position.y = startY - TILE_WIDTH_AND_HEIGHT - 500;
        }
        if (position.x > startX + width + 500) {
            position.x = startX + width + TILE_WIDTH_AND_HEIGHT + 500;
        }
        if (position.y > startY + height + 500) {
            position.y = startY + height + TILE_WIDTH_AND_HEIGHT + 500;
        }
        cam.position.set(position);
        cam.update();
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        currentZoom += amountX * 0.25;
        currentZoom += amountY * 0.25;

        float maxZoom = 3f;
        if (currentZoom >= maxZoom) currentZoom = maxZoom;
        float minZoom = 0.5f;
        if (currentZoom <= minZoom) currentZoom = minZoom;
        camera.zoom = currentZoom;
        camera.update();
        return false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();

    }

    public boolean isContinuousAdding() {
        return continuousAdding;
    }

    public boolean isContinuousDeleting() {
        return continuousDeleting;
    }

    public static int getTileWidthAndHeight() {
        return TILE_WIDTH_AND_HEIGHT;
    }
}
