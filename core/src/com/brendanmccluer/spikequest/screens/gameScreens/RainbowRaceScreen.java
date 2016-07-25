package com.brendanmccluer.spikequest.screens.gameScreens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.interfaces.RainbowRaceObject;
import com.brendanmccluer.spikequest.objects.RingObject;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.RainbowDashObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.tiles.SpikeQuestTiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by brend on 6/25/2016.
 */
public class RainbowRaceScreen extends AbstractSpikeQuestScreen {
    private static final int RENDER_LAYER = 0;
    private static final int COLLISION_LAYER = 1;
    private static final int RING_LAYER = 2;
    private static final int CLOUD_LAYER = 3;
    private static final float BACKGROUND_SPEED_MIN = 5;
    private static final float FOREGROUND_SPEED_MIN = 350;
    private static final float BOOST_RESISTANCE = 2;
    private static final String BACKDROP_PATH = "backdrop/rainbowRaceBackdrop.png";
    private static final int BOOST_MAX = 500;
    private TankObject aTankObject = new TankObject();
    private RainbowDashObject aRainbowDashObject = new RainbowDashObject();
    private List<RainbowRaceObject> rings;
    private Random random;
    private TiledMap tileMap;
    private TiledMapRenderer tiledMapRenderer;
    private float boostSpeed = 0;
    private int[] renderLayers = { RENDER_LAYER }; //layers that are rendered in the tile map
    private SpikeQuestCamera foregroundCamera;
    private ShapeRenderer shapeRenderer;

    public RainbowRaceScreen(SpikeQuestGame game) {
        super(game);
        currentBackdropTexture = new Texture(BACKDROP_PATH);
        gameCamera = new SpikeQuestCamera(1300, currentBackdropTexture.getWidth() * 2, currentBackdropTexture.getHeight());
        foregroundCamera = new SpikeQuestCamera(1300, currentBackdropTexture.getWidth() * 2, currentBackdropTexture.getHeight());
        tileMap = new TmxMapLoader().load("tileMaps/RainbowRaceMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);
        rings = new ArrayList<RainbowRaceObject>();
        for(int i = 0; i < SpikeQuestTiles.getTileMapObjectCount(tileMap, RING_LAYER); i++)
            rings.add(new RingObject());
        random = new Random();
        shapeRenderer = new ShapeRenderer();
    }


    /**
     * I am the main method of the screen
     * @param delta
     */
    @Override
    public void render(float delta) {
        useLoadingScreen(delta);
        if (screenStart || (game.assetManager.loadAssets() && aTankObject.isLoaded() && aRainbowDashObject.isLoaded() && loadListOfObjects(rings))) {
            refresh();

            /**SCREEN START**/
            if (!screenStart) {
                aTankObject.spawn(gameCamera.getCameraWidth()/2, gameCamera.getCameraHeight()/2);
                spawnListInTileLayer(rings, RING_LAYER);
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

            //Need to translate from sprite location in gameCamera to position in foregroundCamera
            Rectangle foregroundPositionRectangle = aTankObject.getCollisionRectangle();
            //foregroundPositionRectangle.x += foregroundCamera.getCameraPositionX() - foregroundCamera.getCameraWidth()/2;
            foregroundPositionRectangle.x += (foregroundCamera.getCameraPositionX() - gameCamera.getCameraPositionX());
            if (SpikeQuestTiles.getPolygonObjectCollision(tileMap, foregroundPositionRectangle , COLLISION_LAYER) != null) {
                aTankObject.stop();
                boostSpeed = 0;
            }
            else {
                gameCamera.translateCamera(delta * (BACKGROUND_SPEED_MIN + boostSpeed), 0);
                foregroundCamera.translateCamera(delta * (FOREGROUND_SPEED_MIN + boostSpeed), 0);
                updateListOfObjects(rings, delta);
            }
            if ((aTankObject.getCenterY() > gameCamera.getCameraHeight()/2 && translateVector.y > 0) ||
                    ((aTankObject.getCenterY() < gameCamera.getCameraHeight()/2 && translateVector.y < 0))) {
                gameCamera.translateCamera(0,translateVector.y);
                foregroundCamera.translateCamera(0, translateVector.y);
            }

            //updateListOfObjects(mountains, delta);

            /**RENDER**/
            gameCamera.attachToBatch(game.batch);
            foregroundCamera.attachToTileMapRenderer(tiledMapRenderer);
            game.batch.begin();
            if (gameCamera.getCameraPositionX() - gameCamera.getCameraWidth() < currentBackdropTexture.getWidth())
                game.batch.draw(currentBackdropTexture, 0,0);
            if (gameCamera.getCameraPositionX() + gameCamera.getCameraWidth() > currentBackdropTexture.getWidth() - 35)
                game.batch.draw(currentBackdropTexture, currentBackdropTexture.getWidth() - 35,0);
            //render the collision objects
            game.batch.end();
            tiledMapRenderer.render(renderLayers);
            game.batch.begin();
            drawRingsBack();
            game.batch.end();
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.setProjectionMatrix(gameCamera.getProjectionMatrix());
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.begin();
            Rectangle debugRectangle = aTankObject.getCollisionRectangle();
            shapeRenderer.box(debugRectangle.x, debugRectangle.y,0, debugRectangle.width, debugRectangle.height, 0);
            shapeRenderer.end();
            game.batch.begin();
            aTankObject.draw(game.batch);
            drawRingsFront();
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

    private void spawnListInTileLayer (List<RainbowRaceObject> objects, int tileLayer) {
        int i = 0;
        for (Rectangle rectangle : SpikeQuestTiles.getTileMapRectangles(tileMap, tileLayer)) {
            rings.get(i).position().x = rectangle.x;
            rings.get(i).position().y = rectangle.y;
            if (i >= objects.size())
                break;
            i++;
        }
    }

   /* public void spawnMountains() {
        for (int i=0; i < mountainLocations.length; i++) {
            mountains.get(i).position().x = mountainLocations[i];
        }
    }*/

    private void updateListOfObjects(List<RainbowRaceObject> objects, float delta) {
        for (RainbowRaceObject object : objects) {
            object.update(delta * (FOREGROUND_SPEED_MIN + boostSpeed));
            if (object instanceof RingObject && !((RingObject) object).beenTouched && object.isColliding(aTankObject.getCollisionRectangle())) {
                RingObject ringObject = (RingObject) object;
                boost(ringObject);
                ringObject.beenTouched = true;
            }

        }
    }

    private void boost(RingObject ring) {
        switch (ring.ringType) {
            case RED : increaseBoost(500);
                break;
            case YELLOW : increaseBoost(250);
                break;
            default : increaseBoost(150);
        }
    }

    private void increaseBoost(int amount) {
        if (boostSpeed + amount >= BOOST_MAX)
            boostSpeed = BOOST_MAX;
        else
            boostSpeed += amount;
    }

}
