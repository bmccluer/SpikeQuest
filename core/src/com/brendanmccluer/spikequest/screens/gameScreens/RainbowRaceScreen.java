package com.brendanmccluer.spikequest.screens.gameScreens;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.interfaces.RainbowRaceObject;
import com.brendanmccluer.spikequest.objects.MountainObject;
import com.brendanmccluer.spikequest.objects.RingObject;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.RainbowDashObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by brend on 6/25/2016.
 */
public class RainbowRaceScreen extends AbstractSpikeQuestScreen {
    private static final float BACKGROUND_SPEED_MIN = 15;
    private static final float FOREGROUND_SPEED_MIN = 200;
    private static final float BOOST_RESISTANCE = 2;
    private static final int BOOST_MAX = 500;
    private TankObject aTankObject = new TankObject();
    private RainbowDashObject aRainbowDashObject = new RainbowDashObject();
    private List<RainbowRaceObject> rings;
    private List<RainbowRaceObject> mountains;
    private int numRings = 30;
    private Random random;
    private TiledMap backgroundTileMap; //render the background
    private TiledMap foregroundTileMap; //render the objects with collisions
    private TiledMapRenderer tiledMapRenderer;
    private float boostSpeed = 0;
    private int[] mountainLocations = {10000, 22000, 30000, 45000, 50000, 64000, 78000};

    public RainbowRaceScreen(SpikeQuestGame game) {
        super(game);
        gameCamera = new SpikeQuestCamera(2500, 20800, 1600);
        backgroundTileMap = new TmxMapLoader().load("tileMaps/PonyvilleSky.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(backgroundTileMap);
        rings = new ArrayList<RainbowRaceObject>();
        mountains = new ArrayList<RainbowRaceObject>();
        for(int i = 0; i < numRings; i++)
            rings.add(new RingObject());
        for(int i = 0; i < mountainLocations.length; i++)
            mountains.add(new MountainObject());
        random = new Random();
    }


    /**
     * I am the main method of the screen
     * @param delta
     */
    @Override
    public void render(float delta) {
        useLoadingScreen(delta);
        if (screenStart || (game.assetManager.loadAssets() && aTankObject.isLoaded() && aRainbowDashObject.isLoaded() && loadListOfObjects(rings) && loadListOfObjects(mountains))) {
            refresh();

            /**SCREEN START**/
            if (!screenStart) {
                aTankObject.spawn(gameCamera.getCameraWidth()/2, gameCamera.getCameraHeight()/2);
                spawnRings();
                spawnMountains();
                screenStart = true;
            }

            /**UPDATE**/
            if (boostSpeed > 0) {
                if (boostSpeed - BOOST_RESISTANCE <= 0)
                    boostSpeed = 0;
                else
                    boostSpeed -= BOOST_RESISTANCE;
            }
            Vector2 translateVector = aTankObject.update(delta, boostSpeed);
            //move background camera
            gameCamera.translateCamera(delta * (BACKGROUND_SPEED_MIN + boostSpeed), 0);
            //move Tank up or down
            if ((aTankObject.getCenterY() > gameCamera.getCameraHeight()/2 && translateVector.y > 0) ||
                    ((aTankObject.getCenterY() < gameCamera.getCameraHeight()/2 && translateVector.y < 0)))
                gameCamera.translateCamera(0,translateVector.y);
            updateListOfObjects(rings, delta);
            updateListOfObjects(mountains, delta);

            /**RENDER**/
            gameCamera.attachToBatch(game.batch);
            gameCamera.attachToTileMapRenderer(tiledMapRenderer);
            tiledMapRenderer.render();
            game.batch.begin();
            drawRingsBack();
            aTankObject.draw(game.batch);
            drawRingsFront();
            drawListOfObjects(mountains);
            game.batch.end();
        }


    }

    private void drawListOfObjects(List<RainbowRaceObject> objects) {
        for (RainbowRaceObject object : objects)
            object.render(game.batch, gameCamera);
    }

    private void drawRingsBack() {
        for (RainbowRaceObject object : rings) {
            RingObject ring = (RingObject) object;
            ring.renderRingBack(game.batch, gameCamera);
        }
    }

    private void drawRingsFront() {
        for (RainbowRaceObject object : rings) {
            RingObject ring = (RingObject) object;
            ring.renderRingFront(game.batch, gameCamera);
        }
    }

    private boolean loadListOfObjects(List<RainbowRaceObject> objects) {
        boolean loaded = true;
        for (RainbowRaceObject object : objects) {
            if (!object.isLoaded())
                loaded = false;
        }
        return loaded;
    }

    private void spawnRings() {
        int betweenSpaceMax = 1000;
        int betweenSpaceMin = 0;
        for (RainbowRaceObject object : rings) {
            RingObject ring = (RingObject) object;
            ring.position = new Vector2(new Random().nextInt(betweenSpaceMax - betweenSpaceMin) + betweenSpaceMin + aTankObject.getCenterX(), random.nextInt(random.nextInt(gameCamera.getWorldHeight() - 150) + 50));
            betweenSpaceMax += 1200;
            betweenSpaceMin += 1200;
            //ring.position = new Vector2(aTankObject.getCenterX(), aTankObject.getCenterY());
        }
    }

    /**
     * I iterate through all rectangles in the tile layer and place the
     * objects in the positions
     * the
     * @param objects
     * @param tileLayer
     */
   /* private void spawnListInTileLayer (List<RainbowRaceObject> objects, int tileLayer) {
        int i = 0;
        for (Rectangle rectangle : SpikeQuestTiles.getTileMapRectangles(backgroundTileMap, tileLayer)) {
            rings.get(i).position().x = rectangle.x;
            rings.get(i).position().y = rectangle.y;
            if (i >= objects.size())
                break;
            i++;
        }
    }*/

    public void spawnMountains() {
        for (int i=0; i < mountainLocations.length; i++) {
            mountains.get(i).position().x = mountainLocations[i];
        }
    }

    private void updateListOfObjects(List<RainbowRaceObject> objects, float delta) {
        for (RainbowRaceObject object : objects) {
            object.update(delta * (FOREGROUND_SPEED_MIN + boostSpeed));
            if (object instanceof RingObject && object.isColliding(aTankObject.getCollisionRectangle())) {
                boost((RingObject) object);
                ((RingObject) object).position.x = -50;
            }

        }
    }

    private void boost(RingObject ring) {
        switch (ring.ringType) {
            case RED : increaseBoost(500);
                break;
            case YELLOW : increaseBoost(250);
                break;
            default : increaseBoost(100);
        }
    }

    private void increaseBoost(int amount) {
        if (boostSpeed + amount >= BOOST_MAX)
            boostSpeed = BOOST_MAX;
        else
            boostSpeed += amount;
    }

}
