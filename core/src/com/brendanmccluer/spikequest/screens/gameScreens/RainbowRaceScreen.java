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
import com.brendanmccluer.spikequest.objects.GemObject;
import com.brendanmccluer.spikequest.objects.rainbowRaceObjects.CloudObject;
import com.brendanmccluer.spikequest.objects.rainbowRaceObjects.RingObject;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.RainbowDashObject;
import com.brendanmccluer.spikequest.objects.rainbowRaceObjects.StormCloudObject;
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
    private static final int GEM_LAYER = 4;
    private static final float BACKGROUND_SPEED_MIN = 5;
    private static final float FOREGROUND_SPEED_MIN = 350;
    private static final float BOOST_RESISTANCE = 2;
    private static final String BACKDROP_PATH = "backdrop/rainbowRaceBackdrop.png";
    private static final int BOOST_MAX = 500;
    private static final int NUM_GEMS = 15;
    private TankObject aTankObject = new TankObject();
    private RainbowDashObject aRainbowDashObject = new RainbowDashObject();
    private List<RainbowRaceObject> rings;
    private List<RainbowRaceObject> clouds;
    private List<GemObject> gems;
    private Random random;
    private TiledMap tileMap;
    private TiledMapRenderer tiledMapRenderer;
    private float boostSpeed = 0;
    private int[] renderLayers = { RENDER_LAYER }; //layers that are rendered in the tile map
    private SpikeQuestCamera foregroundCamera;
    private ShapeRenderer shapeRenderer;

    public RainbowRaceScreen(SpikeQuestGame game) {
        super(game);
        random = new Random();
        currentBackdropTexture = new Texture(BACKDROP_PATH);
        gameCamera = new SpikeQuestCamera(1300, currentBackdropTexture.getWidth() * 3, currentBackdropTexture.getHeight());
        foregroundCamera = new SpikeQuestCamera(1300, currentBackdropTexture.getWidth() * 3, currentBackdropTexture.getHeight());
        tileMap = new TmxMapLoader().load("tileMaps/RainbowRaceMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);
        rings = new ArrayList<RainbowRaceObject>();
        clouds = new ArrayList<RainbowRaceObject>();
        gems = new ArrayList<GemObject>();
        for(int i = 0; i < SpikeQuestTiles.getTileMapObjectCount(tileMap, RING_LAYER); i++)
            rings.add(new RingObject());
        for(int i = 0; i < SpikeQuestTiles.getTileMapObjectCount(tileMap, CLOUD_LAYER); i++) {
            clouds.add(random.nextBoolean() ? new CloudObject() : new StormCloudObject());
        }
        for(int i = 0; i < SpikeQuestTiles.getTileMapObjectCount(tileMap, GEM_LAYER); i++) {
            gems.add(new GemObject());
        }

        shapeRenderer = new ShapeRenderer();
    }


    /**
     * I am the main method of the screen
     * @param delta
     */
    @Override
    public void render(float delta) {
        useLoadingScreen(delta);
        if (screenStart || (game.assetManager.loadAssets() && aTankObject.isLoaded() && aRainbowDashObject.isLoaded() && loadListOfObjects(rings) && loadListOfObjects(clouds) && GemObject.gemsLoaded(gems))) {
            refresh();

            /**SCREEN START**/
            if (!screenStart) {
                aTankObject.spawn(gameCamera.getCameraWidth()/2, gameCamera.getCameraHeight()/2);
                spawnListInTileLayer(rings, RING_LAYER);
                spawnListInTileLayer(clouds, CLOUD_LAYER);
                spawnGems();
                screenStart = true;
            }

            update(delta);

            /**RENDER**/
            //ONLY USE GAMECAMERA TO DRAW THE BACKGROUND
            gameCamera.attachToBatch(game.batch);
            game.batch.begin();
            if (gameCamera.getCameraPositionX() - gameCamera.getCameraWidth() < currentBackdropTexture.getWidth())
                game.batch.draw(currentBackdropTexture, 0,0);
            if ((gameCamera.getCameraPositionX() + gameCamera.getCameraWidth() > currentBackdropTexture.getWidth() - 35) && (gameCamera.getCameraPositionX() + gameCamera.getCameraWidth() < (currentBackdropTexture.getWidth() * 2) - 35))
                game.batch.draw(currentBackdropTexture, currentBackdropTexture.getWidth()  - 35,0);
            if (gameCamera.getCameraPositionX() + gameCamera.getCameraWidth() > (currentBackdropTexture.getWidth() * 2) - 35)
                game.batch.draw(currentBackdropTexture, (currentBackdropTexture.getWidth() * 2)  - 35,0);
            game.batch.end();

            //Draw foreground objects
            foregroundCamera.attachToTileMapRenderer(tiledMapRenderer);
            foregroundCamera.attachToBatch(game.batch);
            tiledMapRenderer.render(renderLayers);
            game.batch.begin();
            drawRingsBack();
            game.batch.end();

            //**DEBUG**/
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.setProjectionMatrix(foregroundCamera.getProjectionMatrix());
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.begin();
            Rectangle debugRectangle = aTankObject.getCollisionRectangle();
            shapeRenderer.box(debugRectangle.x, debugRectangle.y,0, debugRectangle.width, debugRectangle.height, 0);
            shapeRenderer.end();
            //**/

            game.batch.begin();
            aTankObject.draw(game.batch);
            drawRingsFront();
            GemObject.drawGemObjects(gems, game.batch);
            drawListOfObjects(clouds);
            game.batch.end();
        }


    }

    /**
     * All updates to the objects occur here
     * @param delta
     */
    private void update(float delta) {
        float deltaForegroundX =  delta * (FOREGROUND_SPEED_MIN + boostSpeed); //change in foreground pos
        float deltaBackgroundX = delta * (BACKGROUND_SPEED_MIN + boostSpeed);  //change in background pos
        Vector2 translateVector = null;

        if (boostSpeed > 0) {
            if (boostSpeed - BOOST_RESISTANCE <= 0)
                boostSpeed = 0;
            else
                boostSpeed -= BOOST_RESISTANCE;
        }
        if (SpikeQuestTiles.getPolygonObjectCollision(tileMap, aTankObject.getCollisionRectangle() , COLLISION_LAYER) != null) {
            aTankObject.stop();
            boostSpeed = 0;
        }
        else {
            gameCamera.translateCamera(deltaBackgroundX, 0);
            foregroundCamera.translateCamera(deltaForegroundX, 0);
            aTankObject.setCurrentPositionX(aTankObject.getCurrentPositionX() + deltaForegroundX);
        }
        updateListOfObjects(rings, delta);
        updateListOfObjects(clouds, delta);
        GemObject.collectGemsTouched(gems, aTankObject);

        //update Tank according to keyboard
        translateVector = aTankObject.update(delta);
        //adjust cameras to tank moving up and down
        if ((aTankObject.getCenterY() > gameCamera.getCameraHeight()/2 && translateVector.y > 0) ||
                ((aTankObject.getCenterY() < gameCamera.getCameraPositionY() && translateVector.y < 0))) {
            gameCamera.translateCamera(0,translateVector.y);
            foregroundCamera.translateCamera(0, translateVector.y);
        }

    }

    private void drawListOfObjects(List<RainbowRaceObject> objects) {
        for (RainbowRaceObject object : objects)
            object.render(game.batch, foregroundCamera);
    }

    private void drawRingsBack() {
        for (RainbowRaceObject object : rings) {
            RingObject ring = (RingObject) object;
            ring.renderRingBack(game.batch, foregroundCamera);
        }
    }

    private void drawRingsFront() {
        for (RainbowRaceObject object : rings) {
            RingObject ring = (RingObject) object;
            ring.renderRingFront(game.batch, foregroundCamera);
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

    private void spawnListInTileLayer (List<RainbowRaceObject> objects, int tileLayer) {
        int i = 0;
        for (Rectangle rectangle : SpikeQuestTiles.getTileMapRectangles(tileMap, tileLayer)) {
            objects.get(i).setPosition(rectangle.x, rectangle.y);
            if (i >= objects.size())
                break;
            i++;
        }
    }

    private void spawnGems () {
        int i = 0;
        for (Rectangle rectangle : SpikeQuestTiles.getTileMapRectangles(tileMap, GEM_LAYER)) {
            gems.get(i).spawn(rectangle.x, rectangle.y, false);
            gems.get(i).canExpire = false;
            gems.get(i).resize(0.5f);
            if (i >= gems.size())
                break;
            i++;
        }
    }

    private void updateListOfObjects(List<RainbowRaceObject> objects, float delta) {
        for (RainbowRaceObject object : objects) {
            object.update(delta);

            //Specific to Rings
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

    @Override
    public void dispose() {
        super.dispose();
        disposeListOfObjects(clouds);
        disposeListOfObjects(rings);
        for (GemObject gem : gems)
            gem.discard();
        gems.clear();
        rings = null;
        clouds = null;
        gameCamera.discard();
        gameCamera = null;
        foregroundCamera.discard();
        foregroundCamera = null;
        if (shapeRenderer != null)
            shapeRenderer.dispose();
        tiledMapRenderer = null;
        tileMap.dispose();
        tileMap = null;
    }

    /**
     * I discard each object and also clear the list
     * @param objects
     */
    private void disposeListOfObjects(List<RainbowRaceObject> objects) {
        for (RainbowRaceObject object : objects)
            object.discard();
        objects.clear();
    }

}
