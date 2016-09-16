package com.brendanmccluer.spikequest.screens.gameScreens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.common.objects.RainbowRaceProgressBar;
import com.brendanmccluer.spikequest.interfaces.RainbowRaceObject;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;
import com.brendanmccluer.spikequest.objects.GemObject;
import com.brendanmccluer.spikequest.objects.rainbowRaceObjects.CloudObject;
import com.brendanmccluer.spikequest.objects.rainbowRaceObjects.RingObject;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.RainbowDashObject;
import com.brendanmccluer.spikequest.objects.rainbowRaceObjects.StormCloudObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.screens.MainMenuScreen;
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
    private static final long TIME_BETWEEN_START_WORDS = 1500; //in milliseconds
    private static final String BACKDROP_PATH = "backdrop/rainbowRaceBackdrop.png";
    //THESE ARE USED AS RATIOS AGAINST THE CAMERA SPEEDS--------
    private static final float BACKGROUND_SPEED_MIN = 5;
    private static final float FOREGROUND_SPEED_MIN = 350;
    private static final float BOOST_RESISTANCE = 0.015f;
    private static final float RECOVER_SPEED = 0.050f;
    private static final float BOOST_MAX = 2;
    private static final float CLOUD_COLLISION_REDUCTION = 0.50f;
    //----------------------------------------------------------
    //STATIC OBJECTS SHARED BY ALL INSTANCES--------------------
    private static List<TiledMap> tiledMapList;
    private static int score = 0;
    private static int gemCount = 0;
    private static TankObject aTankObject;
    private static RainbowDashObject aRainbowDashObject;
    private static RainbowRaceProgressBar progressBar;
    private static List<RainbowRaceObject> rings;
    private static List<RainbowRaceObject> clouds;
    private static List<GemObject> gems;
    private static int startCount = 3;
    private static boolean raceStarted = false;
    //----------------------------------------------------------
    private TiledMap tileMap;
    private TiledMapRenderer tiledMapRenderer;
    private float boostSpeed = BOOST_MAX;
    private int[] renderLayers = { RENDER_LAYER }; //layers that are rendered in the tile map
    private SpikeQuestCamera foregroundCamera;
    private ShapeRenderer shapeRenderer;
    private boolean collidingWithCloud = false;
    private Sprite startMessageSprite = null;
    private Sprite readySprite = null;
    private Sprite setSprite = null;
    private Sprite goSprite = null;
    private long time = 0;


    public RainbowRaceScreen(SpikeQuestGame game) {
        super(game);
        if (tiledMapList == null) {
            initialize();
        }
        else {
            //update part in progress bar
            progressBar.tankPart++;
        }
        aTankObject.controlsDisabled = false;
        Random random = new Random();
        //get next map off of the list
        tileMap = tiledMapList.remove(0);
        currentBackdropTexture = new Texture(BACKDROP_PATH);
        for(int i = 0; i < SpikeQuestTiles.getTileMapObjectCount(tileMap, RING_LAYER); i++)
            rings.add(new RingObject());
        for(int i = 0; i < SpikeQuestTiles.getTileMapObjectCount(tileMap, CLOUD_LAYER); i++)
            clouds.add(random.nextBoolean() ? new CloudObject() : new StormCloudObject());
        for(int i = 0; i < SpikeQuestTiles.getTileMapObjectCount(tileMap, GEM_LAYER); i++)
            gems.add(new GemObject());
        gameCamera = new SpikeQuestCamera(1300, currentBackdropTexture.getWidth() * 3, currentBackdropTexture.getHeight());
        foregroundCamera = new SpikeQuestCamera(1300, currentBackdropTexture.getWidth() * 3, currentBackdropTexture.getHeight());
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);
        shapeRenderer = new ShapeRenderer();
    }

    /**
     * I initialize all of the static objects shared between instances
     */
    private void initialize() {
        TmxMapLoader mapLoader = new TmxMapLoader();
        //tiledMapList = SpikeQuestTiles.getRandomTileMapList(mapLoader.load("tileMaps/RainbowRaceMap.tmx"), mapLoader.load("tileMaps/RainbowRaceMap2.tmx"));
        tiledMapList = new ArrayList<TiledMap>();
        tiledMapList.add(mapLoader.load("tileMaps/RainbowRaceMap1.tmx"));
        tiledMapList.add(mapLoader.load("tileMaps/RainbowRaceMap2.tmx"));
        tiledMapList.add(mapLoader.load("tileMaps/RainbowRaceMap3.tmx"));
        rings = new ArrayList<RainbowRaceObject>();
        clouds = new ArrayList<RainbowRaceObject>();
        gems = new ArrayList<GemObject>();
        aTankObject = new TankObject();
        aRainbowDashObject = new RainbowDashObject();
        readySprite = new Sprite(new Texture("textures/ReadyTexture.png"));
        setSprite = new Sprite(new Texture("textures/SetTexture.png"));
        goSprite = new Sprite(new Texture("textures/GoTexture.png"));
        startMessageSprite = new Sprite();
        progressBar = new RainbowRaceProgressBar();
    }

    /**
     * I am the main method of the screen
     * @param delta
     */
    @Override
    public void render(float delta) {
        useLoadingScreen(delta);
        if (screenStart || (game.assetManager.loadAssets() && aTankObject.isLoaded() && aRainbowDashObject.isLoaded() && loadListOfObjects(rings) && loadListOfObjects(clouds)
                && GemObject.gemsLoaded(gems) && progressBar.isLoaded())) {
            refresh();

            /**SCREEN START**/
            if (!screenStart) {
                aTankObject.spawn(gameCamera.getCameraWidth()/2, gameCamera.getCameraHeight()/2);
                aRainbowDashObject.spawn(gameCamera.getCameraWidth()/2, gameCamera.getCameraHeight()/2);
                spawnListInTileLayer(rings, RING_LAYER);
                spawnListInTileLayer(clouds, CLOUD_LAYER);
                spawnGems();
                progressBar.position.x = foregroundCamera.getCameraPositionX() - foregroundCamera.getCameraWidth()/2 + (foregroundCamera.getCameraWidth() - progressBar.getWidth())/2;
                progressBar.position.y = foregroundCamera.getCameraPositionY() - foregroundCamera.getCameraHeight()/2 + 50;
                screenStart = true;
            }

            if (raceStarted) {
                /**NEXT SCREEN OR GAME OVER**/
                if (aTankObject.getCurrentPositionX() >= foregroundCamera.getWorldWidth()) {
                    //go to next screen
                    if (tiledMapList.isEmpty()) {
                        dispose();
                        SpikeQuestScreenManager.forwardScreen(this, new SaveScoreScreen(game, score, gemCount, new MainMenuScreen(game)), game);
                    }
                    else {
                        disposePartial();
                        SpikeQuestScreenManager.forwardScreen(this, new RainbowRaceScreen(game), game);
                    }
                    return;
                }

                update(delta);
            }

            if (startCount > 0){
                updateStartRace(delta);
            }

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
//            shapeRenderer.setAutoShapeType(true);
//            shapeRenderer.setProjectionMatrix(foregroundCamera.getProjectionMatrix());
//            shapeRenderer.setColor(Color.BLACK);
//            shapeRenderer.begin();
//            Rectangle debugRectangle = aTankObject.getCollisionRectangle();
//            shapeRenderer.box(debugRectangle.x, debugRectangle.y,0, debugRectangle.width, debugRectangle.height, 0);
//            shapeRenderer.end();
            //**/

            game.batch.begin();
            aTankObject.draw(game.batch);
            drawRingsFront();
            GemObject.drawGemObjects(gems, game.batch);
            drawListOfObjects(clouds);
            progressBar.draw(game.batch);
            if (startMessageSprite != null) {
                startMessageSprite.setPosition(foregroundCamera.getCameraPositionX() - startMessageSprite.getWidth() / 2,
                        foregroundCamera.getCameraPositionY() - startMessageSprite.getHeight() / 2);
                startMessageSprite.draw(game.batch);
            }
            game.batch.end();
        }

    }

    /**
     * All updates to the objects occur here
     * @param delta
     */
    private void update(float delta) {
        Vector2 translateVector = null;
        float deltaForegroundX =  delta * (FOREGROUND_SPEED_MIN + boostSpeed*FOREGROUND_SPEED_MIN); //change in foreground pos
        float deltaBackgroundX = delta * (BACKGROUND_SPEED_MIN + boostSpeed*BACKGROUND_SPEED_MIN);  //change in background pos

        if (aTankObject.getCurrentPositionX() >= foregroundCamera.getWorldWidth() - foregroundCamera.getCameraWidth())
            aTankObject.controlsDisabled = true;

        //apply cloud collision
        if (aTankObject.isShocked() || collidingWithCloud) {
            deltaForegroundX *= CLOUD_COLLISION_REDUCTION;
            deltaBackgroundX *= CLOUD_COLLISION_REDUCTION;
        }

        if (boostSpeed > 0) {
            if (boostSpeed - BOOST_RESISTANCE <= 0)
                boostSpeed = 0;
            else
                boostSpeed -= BOOST_RESISTANCE;
        }
        else if (boostSpeed < 0) {
            if (boostSpeed + RECOVER_SPEED >= 0)
                boostSpeed = 0;
            else
                boostSpeed += RECOVER_SPEED;
        }
        if (!aTankObject.tankHit && SpikeQuestTiles.getPolygonObjectCollision(tileMap, aTankObject.getCollisionRectangle() , COLLISION_LAYER) != null) {
            aTankObject.crash(deltaForegroundX);
            boostSpeed = -1.5f;
            //gameCamera.translateCamera(-deltaBackgroundX, 0);
            //foregroundCamera.translateCamera(-deltaForegroundX, 0);
            aTankObject.setCurrentPositionX(aTankObject.getCurrentPositionX() - deltaForegroundX);
        }
        else {
            gameCamera.translateCamera(deltaBackgroundX, 0);
            foregroundCamera.translateCamera(deltaForegroundX, 0);
            aTankObject.setCurrentPositionX(aTankObject.getCurrentPositionX() + deltaForegroundX);
        }
        collidingWithCloud = false; //reset flag
        updateListOfObjects(rings, delta);
        updateListOfObjects(clouds, delta);
        GemObject.collectGemsTouched(gems, aTankObject);

        //update Tank according to keyboard
        translateVector = aTankObject.update(delta);
        if (!aTankObject.controlsDisabled)
            adjustToBounds(aTankObject, foregroundCamera);

        //adjust cameras to tank moving up and down
        if ((aTankObject.getCenterY() > gameCamera.getCameraHeight()/2 && translateVector.y > 0) ||
                ((aTankObject.getCenterY() < gameCamera.getCameraPositionY() && translateVector.y < 0))) {
            gameCamera.translateCamera(0,translateVector.y);
            foregroundCamera.translateCamera(0, translateVector.y);
        }

        //update progress bar
        progressBar.position.x =  foregroundCamera.getCameraPositionX() - foregroundCamera.getCameraWidth()/2 + (foregroundCamera.getCameraWidth() - progressBar.getWidth())/2;
        progressBar.position.y = foregroundCamera.getCameraPositionY() - foregroundCamera.getCameraHeight()/2 + 50;
        progressBar.updateRainbowMarker(aRainbowDashObject.getCurrentPositionX()/foregroundCamera.getWorldWidth());
        progressBar.updateTankMarker(aTankObject.getCurrentPositionX()/foregroundCamera.getWorldWidth());
    }

    /**
     * I run the update for the beginning of the race
     * and set the raceStarted flag
     * @param delta
     */
    private void updateStartRace(float delta) {
        int width = startMessageSprite.getRegionWidth();
        int height = startMessageSprite.getRegionHeight();

        if (time == 0) {
            time = System.currentTimeMillis();
            startMessageSprite = readySprite;
        }
        else if (System.currentTimeMillis() - time > TIME_BETWEEN_START_WORDS) {
            startCount--;
            time = System.currentTimeMillis();

            if (startCount == 2) {
                startMessageSprite = setSprite;
                startMessageSprite.setScale(1);
            }
            else if (startCount == 1) {
                startMessageSprite = goSprite;
                raceStarted = true;
            }
            else {
                startMessageSprite = null;
                return;
            }
        }
       // startMessageSprite.setSize(width + (width * delta), height * (height * delta));
        startMessageSprite.setScale(startMessageSprite.getScaleX() + (0.5f * delta));

    }

    /**
     * I keep the object from going outside the camera bounds
     * @param anObject
     * @param aCamera
     */
    private void adjustToBounds(AbstractSpikeQuestSpriteObject anObject, SpikeQuestCamera aCamera) {
        float yMax = aCamera.getCameraPositionY() + aCamera.getCameraHeight()/2;
        float yMin = 0;
        float xMax = aCamera.getCameraPositionX() + aCamera.getCameraWidth()/2;
        float xMin = aCamera.getCameraPositionX() - aCamera.getCameraWidth()/2;

        //check y
        if (anObject.getCollisionRectangle().getY() + anObject.getCollisionRectangle().getHeight() > yMax)
            anObject.setCurrentPositionY(yMax - anObject.getCollisionRectangle().getHeight());
        else if (anObject.getCollisionRectangle().getY() < yMin)
            anObject.setCurrentPositionY(yMin);

        //check x
        if (anObject.getCollisionRectangle().getX() + anObject.getCollisionRectangle().getWidth() > xMax)
            anObject.setCurrentPositionX(xMax - anObject.getCollisionRectangle().getWidth());
        else if (anObject.getCollisionRectangle().getX() < xMin)
            anObject.setCurrentPositionX(xMin);

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
            if (object instanceof RingObject && !aTankObject.tankHit && !((RingObject) object).beenTouched && object.isColliding(aTankObject.getCollisionRectangleFront())) {
                RingObject ringObject = (RingObject) object;
                boost(ringObject);
                ringObject.beenTouched = true;
                return;
            }
            //Specific to Clouds
            if (!aTankObject.tankHit && object instanceof CloudObject && object.isColliding(aTankObject.getCollisionRectangle())) {
                collidingWithCloud = true;
                return;
            }
            //Specific to StormClouds
            if (!aTankObject.tankHit && object instanceof StormCloudObject && object.isColliding(aTankObject.getCollisionRectangle())) {
                aTankObject.shock();
                boostSpeed = -1.5f;
                return;
            }

        }
    }

    private void boost(RingObject ring) {
        switch (ring.ringType) {
            case RED : increaseBoost(2f);
                break;
            case YELLOW : increaseBoost(1.5f);
                break;
            default : increaseBoost(1.25f);
        }
    }

    private void increaseBoost(float amount) {
        if (boostSpeed + amount >= BOOST_MAX)
            boostSpeed = BOOST_MAX;
        else
            boostSpeed += amount;
    }

    /**
     * I only dispose objects and clear lists but
     * do not dispose everything shared by the instances
     */
    private void disposePartial() {
        if (readySprite != null) {
            readySprite.getTexture().dispose();
            setSprite.getTexture().dispose();
            goSprite.getTexture().dispose();
            startMessageSprite = null;
            readySprite = null;
            setSprite = null;
            goSprite = null;
        }
        disposeListOfObjects(clouds);
        disposeListOfObjects(rings);
        for (GemObject gem : gems)
            gem.discard();
        gems.clear();
        gameCamera.discard();
        foregroundCamera.discard();
        tileMap.dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (readySprite != null) {
            readySprite.getTexture().dispose();
            setSprite.getTexture().dispose();
            goSprite.getTexture().dispose();
            readySprite = null;
            setSprite = null;
            goSprite = null;
            startMessageSprite = null;
        }
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
        progressBar.dispose();
        progressBar = null;
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
