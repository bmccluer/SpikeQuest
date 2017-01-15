package com.brendanmccluer.spikequest.screens.gameScreens;

import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.brendanmccluer.spikequest.common.objects.RainbowRaceFinishLine;
import com.brendanmccluer.spikequest.common.objects.RainbowRaceProgressBar;
import com.brendanmccluer.spikequest.common.objects.RainbowRaceStartLine;
import com.brendanmccluer.spikequest.common.objects.ScoreBoardObject;
import com.brendanmccluer.spikequest.common.objects.ScoreControlObject;
import com.brendanmccluer.spikequest.interfaces.RainbowRaceObject;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;
import com.brendanmccluer.spikequest.objects.GemObject;
import com.brendanmccluer.spikequest.objects.ponies.DerpyObject;
import com.brendanmccluer.spikequest.objects.rainbowRaceObjects.CloudObject;
import com.brendanmccluer.spikequest.objects.rainbowRaceObjects.RingObject;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.RainbowDashObject;
import com.brendanmccluer.spikequest.objects.rainbowRaceObjects.StormCloudObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.screens.MainMenuScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;
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
    private static final int RAINBOW_DASH_LAYER = 5;
    private static final int POINTS_RING_RED = 200;
    private static final int POINTS_RING_YELLOW = 100;
    private static final int POINTS_RING_GREEN = 50;
    private static final int POINTS_DARK_CLOUD = -100;
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
    private static TankObject tankObject;
    private static RainbowDashObject rainbowDashObject;
    private static RainbowRaceProgressBar progressBar;
    private static ScoreBoardObject scoreboard;
    private static List<RainbowRaceObject> rings;
    private static List<RainbowRaceObject> clouds;
    private static List<GemObject> gems;
    private static List<List<Rectangle>> rainbowDashTargetLists;
    private static int startCount = 3;
    private static boolean raceStarted = false;
    private static float rainbowProgressBarPosition = 0;
    private static RainbowRaceFinishLine finishLine;
    private static ScoreControlObject scoreControlObject;
    private static DerpyObject derpyObject;
    //----------------------------------------------------------
    private TiledMap tileMap;
    private TiledMapRenderer tiledMapRenderer;
    private float boostSpeed = BOOST_MAX;
    private int[] renderLayers = { RENDER_LAYER }; //layers that are rendered in the tile map
    private SpikeQuestCamera foregroundCamera;
    private ShapeRenderer shapeRenderer;
    private boolean collidingWithCloud = false;
    private boolean isLoseGame = false;
    private boolean restart = false;
    private boolean isWinGame = false;
    private RainbowRaceStartLine startLine;
    private Sprite startMessageSprite = null;
    private Sprite readySprite = null;
    private Sprite setSprite = null;
    private Sprite goSprite = null;
    private long time = 0;
    private SpikeQuestSoundEffect lightningSfx, bottleRocketSfx, metalClangSfx, cuckooClockSfx,
    shortBeepSfx, airHornSfx;
    private SpikeQuestMusic raceMusic;


    public RainbowRaceScreen(SpikeQuestGame game) {
        super(game);
    }

    @Override
    public void initialize() {
        if (tiledMapList == null) {
            startNewGame();
        }
        else {
            //updateMainGame part in progress bar
            progressBar.tankPart++;
        }
        tankObject.controlsDisabled = false;
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
     * I startScreen all of the static objects shared between instances
     */
    private void startNewGame() {
        TmxMapLoader mapLoader = new TmxMapLoader();
        //tiledMapList = SpikeQuestTiles.getRandomTileMapList(mapLoader.load("tileMaps/RainbowRaceMap.tmx"), mapLoader.load("tileMaps/RainbowRaceMap2.tmx"));
        tiledMapList = new ArrayList<>();
        tiledMapList.add(mapLoader.load("tileMaps/RainbowRaceMap1.tmx"));
        tiledMapList.add(mapLoader.load("tileMaps/RainbowRaceMap2.tmx"));
        tiledMapList.add(mapLoader.load("tileMaps/RainbowRaceMap3.tmx"));
        rainbowDashTargetLists = new ArrayList<>();
        for (TiledMap aTileMap : tiledMapList) {
            List<Rectangle> rainbowDashTargetList = new ArrayList<>();
            rainbowDashTargetList.addAll(SpikeQuestTiles.getTileMapRectanglesSortedXAscending(aTileMap, RAINBOW_DASH_LAYER));
            rainbowDashTargetLists.add(rainbowDashTargetList);
        }
        rings = new ArrayList<>();
        clouds = new ArrayList<>();
        gems = new ArrayList<>();
        tankObject = new TankObject();
        rainbowDashObject = new RainbowDashObject();
        rainbowDashObject.setGravity(0);
        readySprite = new Sprite(new Texture("textures/ReadyTexture.png"));
        setSprite = new Sprite(new Texture("textures/SetTexture.png"));
        goSprite = new Sprite(new Texture("textures/GoTexture.png"));
        startMessageSprite = new Sprite();
        progressBar = new RainbowRaceProgressBar();
        scoreboard = new ScoreBoardObject();
        finishLine = new RainbowRaceFinishLine();
        startLine = new RainbowRaceStartLine();
        scoreControlObject = new ScoreControlObject();
        derpyObject = new DerpyObject();
        game.assetManager.setAsset("sounds/LightningStrike.wav", "Sound");
        game.assetManager.setAsset("sounds/BottleRocketSound.wav", "Sound");
        game.assetManager.setAsset("sounds/MetalClang.wav", "Sound");
        game.assetManager.setAsset("sounds/CuckooClock.wav", "Sound");
        game.assetManager.setAsset("sounds/AirHorn.wav", "Sound");
        game.assetManager.setAsset("sounds/ShortBeepTone.wav", "Sound");
        game.assetManager.setAsset("music/Platformer2.mp3", "Music");
    }

    /**
     * I am the main method of the screen
     * @param delta
     */
    @Override
    public void render(float delta) {
        useLoadingScreen(delta);

        if (!screenStart && !(game.assetManager.loadAssets() && tankObject.isLoaded() && rainbowDashObject.isLoaded() && loadListOfObjects(rings) && loadListOfObjects(clouds)
                && GemObject.gemsLoaded(gems) && progressBar.isLoaded() && scoreboard.isLoaded() && finishLine.isLoaded() && derpyObject.isLoaded() && (startLine == null || startLine.isLoaded())))
            return;

        refresh();

        if (!screenStart)
            startScreen();

        if (raceStarted) {
            if (forwardScreen())
               return;

            isLoseGame = !isWinGame && getTargetRectangle() == null && progressBar.rainbowPart >= 3;
            //check lose game
            if (isLoseGame) {
                updateLoseGame(delta);
                if (!finishLine.getIsRibbonBroke())
                    finishLine.breakRibbon();
            }
            else
                updateMainGame(delta);

            updateRainbowDash(delta);
        }

        updateScoreBoard();
        updateProgressBar();
        if (!raceStarted)
            rainbowDashObject.crouch();
        if (startCount > 0) {
            updateStartRace(delta);
        }

        renderObjects();

    }

    /**
     * I determine if we should go to the next screen
     * and set next screen if true
     * @return
     */
    private boolean forwardScreen() {
        //go to next screen
        if (restart) {
            dispose();
            SpikeQuestScreenManager.forwardScreen(this, new RainbowRaceScreen(game), game);
            return true;
        }
        if (tankObject.getCurrentPositionX() >= foregroundCamera.getWorldWidth() + 100) {
            if (finishLine.getIsRibbonBroke() && (System.currentTimeMillis() - time) > 3000) {
                dispose();
                SpikeQuestScreenManager.forwardScreen(this, new SaveScoreScreen(game, scoreboard.getScore(), scoreboard.getGems(), new MainMenuScreen(game)), game);
                return true;
            }
            else if (!tiledMapList.isEmpty()) {
                disposePartial();
                SpikeQuestScreenManager.forwardScreen(this, new RainbowRaceScreen(game), game);
                return true;
            }
        }
        return false;
    }

    /**
     * I draw all of the objects
     */
    private void renderObjects() {
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

        /**DEBUG**/
        /*if (getTargetRectangle() != null) {
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.setProjectionMatrix(foregroundCamera.getProjectionMatrix());
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.begin();
            Rectangle debugRectangle = getTargetRectangle();
            shapeRenderer.box(debugRectangle.x, debugRectangle.y,0, debugRectangle.width, debugRectangle.height, 0);
            shapeRenderer.end();
        }*/

        game.batch.begin();
        if (progressBar.tankPart == 2)
            finishLine.drawBack(game.batch);
        if (startLine != null)
            startLine.drawBack(game.batch);
        //only draw Rainbow on same screen as Tank
        if (progressBar.rainbowPart == progressBar.tankPart)
            rainbowDashObject.draw(game.batch);
        tankObject.draw(game.batch);
        if (startLine != null)
            startLine.drawFront(game.batch);
        if (progressBar.tankPart == 2)
            finishLine.drawFront(game.batch);
        drawRingsFront();
        GemObject.drawGemObjects(gems, game.batch);
        drawListOfObjects(clouds);
        progressBar.draw(game.batch);
        scoreboard.draw(game.batch);
        if (startMessageSprite != null) {
            startMessageSprite.setPosition(foregroundCamera.getCameraPositionX() - startMessageSprite.getWidth() / 2,
                    foregroundCamera.getCameraPositionY() - startMessageSprite.getHeight() / 2);
            startMessageSprite.draw(game.batch);
        }
        scoreControlObject.draw(game.batch);
        if (isLoseGame)
            derpyObject.draw(game.batch);
        game.batch.end();
    }

    /**
     * I place the objects in their starting positions
     */
    private void startScreen() {
        tankObject.spawn(gameCamera.getCameraWidth()/2, gameCamera.getCameraHeight()/2);
        if (!raceStarted) {
            rainbowDashObject.spawn(gameCamera.getCameraWidth()/2 + 20, gameCamera.getCameraHeight()/2 + 20);
            rainbowDashObject.changeToFlying();
        }
        spawnListInTileLayer(rings, RING_LAYER);
        spawnListInTileLayer(clouds, CLOUD_LAYER);
        spawnGems();
        scoreboard.spawn(0,0);
        scoreboard.subtractLife();
        scoreboard.subtractLife();
        if (progressBar.tankPart == 2)
            finishLine.setCurrentPositionXY(foregroundCamera.getWorldWidth() - finishLine.getCollisionRectangle().getWidth(), foregroundCamera.getWorldHeight()/2 - finishLine.getCollisionRectangle().getHeight()/2);
        if (startLine != null) {
            startLine.position = new Vector2(tankObject.getCenterX() - startLine.getCollisionRectangle().getWidth()/2 + 25, tankObject.getCenterY() - startLine.getCollisionRectangle().getHeight()/2 + 70);
        }
        lightningSfx = new SpikeQuestSoundEffect(((Sound) game.assetManager.loadAsset("sounds/LightningStrike.wav", "Sound")), 100);
        bottleRocketSfx = new SpikeQuestSoundEffect(((Sound) game.assetManager.loadAsset("sounds/BottleRocketSound.wav", "Sound")), 100);
        metalClangSfx = new SpikeQuestSoundEffect(((Sound) game.assetManager.loadAsset("sounds/MetalClang.wav", "Sound")), 100);
        cuckooClockSfx = new SpikeQuestSoundEffect(((Sound) game.assetManager.loadAsset("sounds/CuckooClock.wav", "Sound")), 100);
        shortBeepSfx = new SpikeQuestSoundEffect(((Sound) game.assetManager.loadAsset("sounds/ShortBeepTone.wav", "Sound")), 100);
        airHornSfx = new SpikeQuestSoundEffect(((Sound) game.assetManager.loadAsset("sounds/AirHorn.wav", "Sound")), 100);
        raceMusic = new SpikeQuestMusic(((Music) game.assetManager.loadAsset("music/Platformer2.mp3", "Music")));
        screenStart = true;
    }

    /**
     * I move RainbowDash according to the tile map and
     * also set the progress bar position
     * @param delta
     */
    private void updateRainbowDash(float delta) {
        Rectangle aTargetRectangle = getTargetRectangle();
        float speed = 450 * delta;

        if (aTargetRectangle != null) {
            rainbowDashObject.moveTowardsPoint(aTargetRectangle.getX(), aTargetRectangle.getY(), speed);
        }
        else
            rainbowDashObject.moveRight(speed);

        rainbowProgressBarPosition += speed;

        if (rainbowProgressBarPosition >= foregroundCamera.getWorldWidth()) {
            progressBar.rainbowPart++;
            rainbowProgressBarPosition = 0;
            //set for next screen
            rainbowDashObject.setCurrentPositionXY(gameCamera.getCameraWidth()/2, gameCamera.getCameraHeight()/2);
        }
    }

    private Rectangle getTargetRectangle() {
        Rectangle aRectangle = null;
        Rectangle aTargetRectangle = null;
        List<Rectangle> rainbowDashTargetList = null;

        if (progressBar.rainbowPart < rainbowDashTargetLists.size())
            rainbowDashTargetList = rainbowDashTargetLists.get(progressBar.rainbowPart);

        if (rainbowDashTargetList == null || rainbowDashTargetList.isEmpty())
            return null;

        aTargetRectangle = rainbowDashTargetList.get(0);
        aRectangle = rainbowDashObject.getCollisionRectangle();

        //Move to next rectangle?
        if (rainbowDashObject.getCenterX() >= aTargetRectangle.getX()) {
            rainbowDashTargetList.remove(0);
            if (rainbowDashTargetList.isEmpty())
                return null;
            aTargetRectangle = rainbowDashTargetList.get(0);
        }

        return aTargetRectangle;
    }

    private void updateProgressBar() {
        progressBar.position.x =  foregroundCamera.getCameraPositionX() - foregroundCamera.getCameraWidth()/2 + (foregroundCamera.getCameraWidth() - progressBar.getWidth())/2;
        progressBar.position.y = foregroundCamera.getCameraPositionY() - foregroundCamera.getCameraHeight()/2 + 50;
        progressBar.updateRainbowMarker(rainbowProgressBarPosition /foregroundCamera.getWorldWidth());
        progressBar.updateTankMarker(tankObject.getCurrentPositionX()/foregroundCamera.getWorldWidth());
    }

    private void updateScoreBoard() {
        scoreboard.setCurrentPositionX(foregroundCamera.getCameraPositionX() + foregroundCamera.getCameraWidth()/2 - scoreboard.getCollisionRectangle().getWidth());
        scoreboard.setCurrentPositionY(foregroundCamera.getCameraPositionY() + foregroundCamera.getCameraHeight()/2 - scoreboard.getCollisionRectangle().getHeight());
    }

    /**
     * All updates to Tank and collisions
     * @param delta
     */
    private void updateMainGame(float delta) {
        Vector2 translateVector = null;
        float deltaForegroundX =  delta * (FOREGROUND_SPEED_MIN + boostSpeed*FOREGROUND_SPEED_MIN); //change in foreground pos
        //TO DEBUG RAINBOW USE THIS
        //float deltaForegroundX =  delta * 450; //change in foreground pos
        float deltaBackgroundX = delta * (BACKGROUND_SPEED_MIN + boostSpeed*BACKGROUND_SPEED_MIN);  //change in background pos

        if (tankObject.getCurrentPositionX() >= foregroundCamera.getWorldWidth() - foregroundCamera.getCameraWidth()) {
            tankObject.controlsDisabled = true;

            //last section
            if (progressBar.tankPart == 2 && !isWinGame) {
                tankObject.setGravity(0);
                tankObject.moveTowardsPoint(finishLine.getCenterX() + 1000, finishLine.getCenterY() ,deltaForegroundX);
                if (tankObject.getCollisionRectangle().getX() > finishLine.getCenterX() - 45) {
                    finishLine.breakRibbon();
                    time = System.currentTimeMillis();
                    isWinGame = true;
                }
            }
        }

        scoreboard.addGems(GemObject.collectGemsTouched(gems, tankObject));

        //apply cloud collision
        if (tankObject.isShocked() || collidingWithCloud) {
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
        if (!tankObject.tankHit && SpikeQuestTiles.getPolygonObjectCollision(tileMap, tankObject.getCollisionRectangle() , COLLISION_LAYER) != null) {
            tankObject.crash(deltaForegroundX);
            metalClangSfx.playSound(true, 0.5f, 0.35f);
            cuckooClockSfx.playSound(true, 0.8f, 0.25f);
            boostSpeed = -1.5f;
            //gameCamera.translateCamera(-deltaBackgroundX, 0);
            //foregroundCamera.translateCamera(-deltaForegroundX, 0);
            tankObject.setCurrentPositionX(tankObject.getCurrentPositionX() - deltaForegroundX);
        }
        else {
            gameCamera.translateCamera(deltaBackgroundX, 0);
            foregroundCamera.translateCamera(deltaForegroundX, 0);
            tankObject.setCurrentPositionX(tankObject.getCurrentPositionX() + deltaForegroundX);
        }
        collidingWithCloud = false; //reset flag
        updateListOfObjects(rings, delta);
        updateListOfObjects(clouds, delta);

        //updateMainGame Tank according to keyboard
        translateVector = tankObject.update(delta);
        if (!tankObject.controlsDisabled)
            adjustToBounds(tankObject, foregroundCamera);

        //adjust cameras to tank moving up and down
        if ((tankObject.getCenterY() > gameCamera.getCameraHeight()/2 && translateVector.y > 0) ||
                ((tankObject.getCenterY() < gameCamera.getCameraPositionY() && translateVector.y < 0))) {
            gameCamera.translateCamera(0,translateVector.y);
            foregroundCamera.translateCamera(0, translateVector.y);
        }

    }

    /**
     * I run the updateMainGame for the beginning of the race
     * and set the raceStarted flag
     * @param delta
     */
    private void updateStartRace(float delta) {
        int width = startMessageSprite.getRegionWidth();
        int height = startMessageSprite.getRegionHeight();

        if (time == 0) {
            time = System.currentTimeMillis();
            startMessageSprite = readySprite;
            shortBeepSfx.playSound(true, 1f, 1);
        }
        else if (System.currentTimeMillis() - time > TIME_BETWEEN_START_WORDS) {
            startCount--;
            time = System.currentTimeMillis();

            if (startCount == 2) {
                startMessageSprite = setSprite;
                startMessageSprite.setScale(1);
                shortBeepSfx.playSound(true, 1f, 1);
            }
            else if (startCount == 1) {
                startMessageSprite = goSprite;
                raceStarted = true;
                airHornSfx.playSound(true);
                raceMusic.playMusic(true);
            }
            else {
                startMessageSprite = null;
                return;
            }
        }
       // startMessageSprite.setSize(width + (width * delta), height * (height * delta));
        startMessageSprite.setScale(startMessageSprite.getScaleX() + (0.5f * delta));

    }

    private void updateLoseGame(float delta) {
        if(!derpyObject.isSpawned()) {
            derpyObject.spawn(0,0);
            derpyObject.setGravity(0);
            derpyObject.setBanner("Try Again!");
            derpyObject.setBannerTextSize(2);
            derpyObject.setCurrentPositionXY(foregroundCamera.getCameraPositionX() - foregroundCamera.getCameraWidth()/2 - derpyObject.getCollisionRectangle().getWidth(),
                    foregroundCamera.getCameraPositionY() + foregroundCamera.getCameraHeight()/2 - derpyObject.getCollisionRectangle().getHeight());
        }

        if(derpyObject.getCollisionRectangle().getX() > foregroundCamera.getCameraPositionX())
            restart = true;

        derpyObject.moveRight(delta * 150);
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
            if (object instanceof RingObject && !tankObject.tankHit && !((RingObject) object).beenTouched && object.isColliding(tankObject.getCollisionRectangleFront())) {
                RingObject ringObject = (RingObject) object;
                boost(ringObject);
                ringObject.beenTouched = true;
                switch (ringObject.ringType) {
                    case RED :
                        addScore(POINTS_RING_RED, tankObject.getCenterX(), tankObject.getCenterY());
                        bottleRocketSfx.playSound(true, 1.5f, 0.25f);
                        break;
                    case YELLOW :
                        addScore(POINTS_RING_YELLOW, tankObject.getCenterX(), tankObject.getCenterY());
                        bottleRocketSfx.playSound(true, 1.8f, 0.25f);
                        break;
                    case GREEN :
                        addScore(POINTS_RING_GREEN, tankObject.getCenterX(), tankObject.getCenterY());
                        bottleRocketSfx.playSound(true, 2f, 0.25f);
                }
                return;
            }
            //Specific to Clouds
            if (!tankObject.tankHit && object instanceof CloudObject && object.isColliding(tankObject.getCollisionRectangle())) {
                collidingWithCloud = true;
                return;
            }
            //Specific to StormClouds
            if (!tankObject.tankHit && object instanceof StormCloudObject && object.isColliding(tankObject.getCollisionRectangle())) {
                tankObject.shock();
                boostSpeed = -1.5f;
                addScore(POINTS_DARK_CLOUD, tankObject.getCenterX(), tankObject.getCenterY());
                lightningSfx.playSound(true);
                cuckooClockSfx.playSound(true, 0.8f, 0.25f);
                return;
            }

        }
    }

    private void addScore(int points, float xPos, float yPos) {
        scoreboard.addScore(points);
        scoreControlObject.createNewScore(points, xPos, yPos);
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
        if (startLine != null) {
            startLine.discard();
            startLine = null;
        }
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
        rainbowDashTargetLists.clear();
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
        for (TiledMap aTileMap : tiledMapList)
            tileMap.dispose();
        tiledMapList.clear();
        tiledMapList = null;
        progressBar.dispose();
        progressBar = null;
        finishLine.discard();
        finishLine = null;
        scoreControlObject.discard();
        scoreControlObject = null;
        derpyObject.discard();
        derpyObject = null;
        raceStarted = false;
        startCount = 3;
        rainbowProgressBarPosition = 0;
        bottleRocketSfx = null;
        lightningSfx = null;
        cuckooClockSfx = null;
        metalClangSfx = null;
        shortBeepSfx = null;
        airHornSfx = null;
        raceMusic.stopMusic();
        raceMusic = null;
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
