package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConnectionsAdder {
    MainGame mainGame;
    List<Tile> tiles = new ArrayList<>();


    public ConnectionsAdder(MainGame mainGame) {
        this.mainGame = mainGame;
    }

    public void addConnections(List<Tile> tiles, Map<List<Integer>, Tile> tileMap) {
        this.tiles = tiles;
        for (Tile tile : tiles) {
            int tileX = tile.getXNr();
            int tileY = tile.getYNr();
            checkTile(tileX, tileY, tile, tileMap);

        }

    }

    private void checkTile(int x, int y, Tile tile, Map<List<Integer>, Tile> tileMap) {
        if (tileMap.containsKey(new ArrayList<>(Arrays.asList(x - 1, y - 1)))) {
            Tile tile2 = tileMap.get(new ArrayList<>(Arrays.asList(x - 1, y - 1)));
            tile2.addConnection(tile);

        }
        if (tileMap.containsKey(new ArrayList<>(Arrays.asList(x, y - 1)))) {
            Tile tile2 = tileMap.get(new ArrayList<>(Arrays.asList(x, y - 1)));
            tile2.addConnection(tile);
            tile2.addClosestTiles(tile);
        }
        if (tileMap.containsKey(new ArrayList<>(Arrays.asList(x + 1, y - 1)))) {
            Tile tile2 = tileMap.get(new ArrayList<>(Arrays.asList(x + 1, y - 1)));
            tile2.addConnection(tile);
        }
        if (tileMap.containsKey(new ArrayList<>(Arrays.asList(x + 1, y)))) {
            Tile tile2 = tileMap.get(new ArrayList<>(Arrays.asList(x + 1, y)));
            tile2.addConnection(tile);
            tile2.addClosestTiles(tile);
        }
        if (tileMap.containsKey(new ArrayList<>(Arrays.asList(x + 1, y + 1)))) {
            Tile tile2 = tileMap.get(new ArrayList<>(Arrays.asList(x + 1, y + 1)));
            tile2.addConnection(tile);
        }
        if (tileMap.containsKey(new ArrayList<>(Arrays.asList(x, y + 1)))) {
            Tile tile2 = tileMap.get(new ArrayList<>(Arrays.asList(x, y + 1)));
            tile2.addConnection(tile);
            tile2.addClosestTiles(tile);
        }
        if (tileMap.containsKey(new ArrayList<>(Arrays.asList(x - 1, y + 1)))) {
            Tile tile2 = tileMap.get(new ArrayList<>(Arrays.asList(x - 1, y + 1)));
            tile2.addConnection(tile);
        }
        if (tileMap.containsKey(new ArrayList<>(Arrays.asList(x - 1, y)))) {
            Tile tile2 = tileMap.get(new ArrayList<>(Arrays.asList(x - 1, y)));
            tile2.addConnection(tile);
            tile2.addClosestTiles(tile);
        }

    }
}
