package com.brendanmccluer.spikequest.screens.gameScreens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;
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
    private static final String BACKGROUND_FILE_PATH = "backdrop/rainbowRaceBackdrop.png";
    private static final float BACKGROUND_SPEED_MIN = 150;
    private static final float FOREGROUND_SPEED_MIN = 200;
    private TankObject aTankObject = new TankObject();
    private RainbowDashObject aRainbowDashObject = new RainbowDashObject();
    private List<AbstractSpikeQuestObject> rings;
    private List<AbstractSpikeQuestObject> mountains;
    private int numRings = 30;
    private int numMountains = 10;
    private Random random;
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private float boostSpeed = 0;
    private int[] mountainLocations = {300, 600, 800, 2500, 3500};

    public RainbowRaceScreen(SpikeQuestGame game) {
        super(game);
        gameCamera = new SpikeQuestCamera(2500, 20800, 1600);
        tiledMap = new TmxMapLoader().load("tileMaps/PonyvilleSky.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        rings = new ArrayList<AbstractSpikeQuestObject>();
        mountains = new ArrayList<AbstractSpikeQuestObject>();
        for(int i = 0; i < numRings; i++)
            rings.add(new RingObject());
        for(int i = 0; i < numMountains; i++)
            mountains.add(new MountainObject());
        random = new Random();
    }


    @Override
    public void render(float delta) {
        useLoadingScreen(delta);
        if (screenStart || (game.assetManager.loadAssets() && aTankObject.isLoaded() && aRainbowDashObject.isLoaded() && loadListOfObjects(rings) && loadListOfObjects(mountains))) {
            refresh();

            if (!screenStart) {
                aTankObject.spawn(gameCamera.getCameraWidth()/2, gameCamera.getCameraHeight()/2);
                spawnRings();
                screenStart = true;
            }
            gameCamera.attachToBatch(game.batch);
            Vector2 translateVector = aTankObject.update(delta);
            //move background camera
            gameCamera.translateCamera(delta * (BACKGROUND_SPEED_MIN + boostSpeed), 0);

            //move Tank up or down
            if ((aTankObject.getCenterY() > gameCamera.getCameraHeight()/2 && translateVector.y > 0) ||
                    ((aTankObject.getCenterY() < gameCamera.getCameraHeight()/2 && translateVector.y < 0)))
                gameCamera.translateCamera(0,translateVector.y);
            updateRings(delta);
            gameCamera.attachToTileMapRenderer(tiledMapRenderer);
            tiledMapRenderer.render();
            game.batch.begin();
            drawMountains();
            drawRingsBack();
            aTankObject.draw(game.batch);
            drawRingsFront();
            game.batch.end();
        }


    }

    private void drawMountains() {
        for (int posX : mountainLocations)
            if (isPositionInCamera(posX))
                return;

    }


    private void drawRingsBack() {
        for (AbstractSpikeQuestObject object : rings) {
            RingObject ring = (RingObject) object;
            if (isPositionInCamera(ring.position.x))
                ring.renderRingBack(game.batch);
        }
    }

    private void drawRingsFront() {
        for (AbstractSpikeQuestObject object : rings) {
            RingObject ring = (RingObject) object;
            if (isPositionInCamera(ring.position.x))
                ring.renderRingFront(game.batch);
        }
    }

    private boolean loadListOfObjects(List<AbstractSpikeQuestObject> objects) {
        boolean loaded = true;
        for (AbstractSpikeQuestObject object : objects) {
            if (!object.isLoaded())
                loaded = false;
        }
        return loaded;
    }

    private void spawnRings() {
        for (AbstractSpikeQuestObject object : rings) {
            RingObject ring = (RingObject) object;
            ring.position = new Vector2(new Random().nextInt(20700) + aTankObject.getCenterX(), random.nextInt(random.nextInt(gameCamera.getWorldHeight() - 150) + 50));
            //ring.position = new Vector2(aTankObject.getCenterX(), aTankObject.getCenterY());
        }
    }

    private void updateRings(float delta) {
        for (AbstractSpikeQuestObject object : rings) {
            RingObject ring = (RingObject) object;
            ring.update(delta * (FOREGROUND_SPEED_MIN + boostSpeed));
        }
    }

    private boolean isPositionInCamera(float posX) {
        return (posX > (gameCamera.getCameraPositionX() - gameCamera.getCameraWidth()/2 -50) && posX < (gameCamera.getCameraPositionX() + gameCamera.getCameraWidth()/2 + 50));
    }
}
