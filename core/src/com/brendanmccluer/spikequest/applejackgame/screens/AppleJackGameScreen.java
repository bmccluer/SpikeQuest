package com.brendanmccluer.spikequest.applejackgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.*;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestShapeRenderer;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.actors.SpikeQuestActorComparator;
import com.brendanmccluer.spikequest.applejackgame.ApplejackGamePlayer;
import com.brendanmccluer.spikequest.applejackgame.objects.AppleTreeObject;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.applejackgame.objects.AppleBucketObject;
import com.brendanmccluer.spikequest.applejackgame.objects.TreeDropSpotObject;
import com.brendanmccluer.spikequest.objects.ponies.BigMacObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.tiles.SpikeQuestTiles;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sun.security.provider.ConfigFile;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by brend on 10/29/2017.
 */

public class AppleJackGameScreen extends AbstractSpikeQuestScreen {
    private static final String TAG = "AppleJackGameScreen";
   // private static final int RENDER_LAYER = 0;
    private static final int TREE_DROP_OBJECT_LAYER = 0;
    private static final int SORT_DROP_OBJECT_LAYER = 1;
    private static final int COLLISION_LAYER = 2;
    private static final int BAKE_DROP_OBJECT_LAYER = 3;
    private static final int APPLE_CART_DROP_OBJECT_LAYER = 4;
    private static final int SLOP_DROP_OBJECT_LAYER = 5;
    private Stage stage;
    private ApplejackGamePlayer player;
    private BigMacObject bigMac;
    private static final float BIG_MAC_SPEED = 100;
    private static final float PLAYER_SPEED = 300;
    private static final float PLAYER_RUN_SPEED = 600;
    private AppleBucketObject bucket;
    private Group treeDropSpotObjectGroup;
    private Group treeObjectGroup;
    private TiledMap tileMap;
    private TiledMapRenderer tiledMapRenderer;
    private List<Rectangle> collisionRects;
   // private int[] renderLayers = { RENDER_LAYER }; //layers that are rendered in the tile map
    private Action buckTreeAction;
    private Action dropHitDetectionAction;

    //target objects
    private AppleBucketObject bigMacTargetedBucket;
    private AppleTreeObject bigMacTargetedTree;

    public AppleJackGameScreen(SpikeQuestGame game) {
        super(game);
    }

    @Override
    public void render(float delta) {
        //don't exceed 1 for change in time
        delta = Math.min(delta, 1);
        refresh();
        super.useLoadingScreen(delta);
        if(!isLoaded())
            return;
        if(!screenStart) {
            screenStart = true;
        }
        updateController(delta);
        handleCollisions();
        updateGameLogic(delta);
        //set depth of objects
        List<Actor> actorList = Arrays.asList(stage.getActors().toArray());
        Collections.sort(actorList, new SpikeQuestActorComparator());
        int zIndex = 0;
        for(Actor actor : actorList) {
            actor.setZIndex(zIndex++);
        }
        stage.act(delta);
        gameCamera.attachToBatch(game.batch);
        gameCamera.attachToTileMapRenderer(tiledMapRenderer);
        game.batch.begin();
        drawBackdrop();
        //tiledMapRenderer.render();
        game.batch.end();
        stage.draw();

        stage.setDebugAll(SpikeQuestGame.debugMode);
        if(SpikeQuestGame.debugMode) {
            SpikeQuestShapeRenderer renderer = new SpikeQuestShapeRenderer();
            renderer.drawRectangle(player.getBounds(), gameCamera);
            SpikeQuestTiles.drawPolygons(tileMap, COLLISION_LAYER, gameCamera);
        }
    }

    private void updateController(float delta) {
        boolean moving = false;
        player.velocity.setZero();
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if((player.getY() + player.getBounds().getHeight()) < gameCamera.getCameraHeight())
                player.velocity.y = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? PLAYER_RUN_SPEED : PLAYER_SPEED;
            player.setCurrentAnimation(player.animWalk);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if(player.getY() > 0)
                player.velocity.y = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? -PLAYER_RUN_SPEED : -PLAYER_SPEED;
            player.setCurrentAnimation(player.animWalk);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(player.getX() > 0)
                player.velocity.x = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? -PLAYER_RUN_SPEED : -PLAYER_SPEED;
            player.setCurrentAnimation(player.animWalk);
            player.setFlip(true, false);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(player.getX() + player.getBounds().getWidth() < gameCamera.getCameraWidth())
                player.velocity.x = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? PLAYER_RUN_SPEED : PLAYER_SPEED;
            player.setCurrentAnimation(player.animWalk);
            player.setFlip(false, false);
        }
        if(player.velocity.isZero())
            player.setCurrentAnimation(player.animStand);
        if(input.justTouched()) {
            bucket.setPosition(gameCamera.getMousePositionX() - bucket.getWidth()/2, gameCamera.getMousePositionY() - bucket.getHeight()/2);
            for(Actor dropSpotObject : treeDropSpotObjectGroup.getChildren()) {
                ((TreeDropSpotObject)dropSpotObject).removeActor();
            }
            //TODO: Use Spike to place bucket and remove bucket
            TreeDropSpotObject dropSpotObject = (TreeDropSpotObject) treeDropSpotObjectGroup.hit(bucket.getCenterX(), bucket.getCenterY(), false);
            if(dropSpotObject != null) {
                Gdx.app.debug(TAG, "Tree Drop Spot hit by bucket");
                dropSpotObject.heldActor = bucket;
            }
        }
        //debug controls
        if(SpikeQuestGame.debugMode) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                bucket.emptyBucket();
            }
        }
    }

    private void updateGameLogic(float delta) {
        if(bigMac.isBucking && bigMacTargetedBucket != null && bigMacTargetedTree != null) {
            float percentComplete = bigMac.percentComplete;
            //TODO: set timer object of drop spot
            bigMacTargetedBucket.fillMixedApples(percentComplete);
            if(bigMacTargetedBucket.isFull()) {
                Gdx.app.debug(TAG, "Bucket has been filled. Stop Big Mac bucking");
                bigMac.stopBucking();
                bigMacTargetedTree.stopShaking();
                bigMacTargetedTree = null;
                bigMacTargetedBucket = null;
            }
        }
        //find new target to fill for BigMac
        else if(bigMac.getActions().size == 0){
            for(Actor actor : treeDropSpotObjectGroup.getChildren()) {
                TreeDropSpotObject dropSpotObject = (TreeDropSpotObject)actor;
                if(dropSpotObject.heldActor instanceof AppleBucketObject) {
                    AppleBucketObject appleBucketObject = (AppleBucketObject) dropSpotObject.heldActor;
                    if(!appleBucketObject.isFull()) {
                        Gdx.app.debug(TAG, "Found new bucket object for Big Mac");
                        //move to the target and do buck action
                        //speed * time = distance. Therefore, calculate time = distance/speed
                        Vector2 location = new Vector2(dropSpotObject.treeObject.getCenterX() + bigMac.getWidth()/2, dropSpotObject.treeObject.getY());
                        float distance = location.dst(bigMac.getX(), bigMac.getY());
                        bigMac.addAction(sequence(moveTo(location.x, location.y, distance/ BIG_MAC_SPEED, Interpolation.linear), buckTreeAction));
                        bigMacTargetedBucket = appleBucketObject;
                        bigMacTargetedTree = dropSpotObject.treeObject;
                    }
                }
            }
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        gameCamera = new SpikeQuestCamera(1920, 1920, 1080);
        currentBackdropTexture = new Texture(Gdx.files.internal("Backdrop/ApplejackGameBackground.png"));
        buildStage();
        buildActions();
        super.addToLoader(player);
        input.setInputProcessor(stage);
    }

    private void buildActions() {
        buckTreeAction = new Action() {
            @Override
            public boolean act(float delta) {
                bigMac.buck();
                bigMacTargetedTree.shake();
                return true;
            }
        };
    }


    private void buildStage() {
        Viewport viewport = new StretchViewport(gameCamera.getCameraWidth(), gameCamera.getCameraHeight());
        viewport.setCamera(gameCamera.camera);
        stage = new Stage(viewport);
        TmxMapLoader mapLoader = new TmxMapLoader();
        treeDropSpotObjectGroup = new Group();
        treeObjectGroup = new Group();
        tileMap = mapLoader.load("tileMaps/AppleJackGame.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);
        bigMac = new BigMacObject();
        bucket = new AppleBucketObject();
        player = new ApplejackGamePlayer(SpikeQuestStaticFilePaths.SPIKE_MAIN_TEXTURE_ATLAS, "spike");
        player.maxVelocity = PLAYER_RUN_SPEED;
        player.setScale(0.17f);
        bigMac.setPosition(gameCamera.getCameraWidth() - 100, gameCamera.getCameraPositionY());
        stage.addActor(bigMac);
        stage.addActor(bucket);
        stage.addActor(player);

        for(Rectangle rect : SpikeQuestTiles.getTileMapRectangles(tileMap, TREE_DROP_OBJECT_LAYER)){
            TreeDropSpotObject object = new TreeDropSpotObject();
            object.setPosition(rect.getX(), rect.getY());
            object.setSize(rect.getWidth(), rect.getHeight());
            treeDropSpotObjectGroup.addActor(object);
            //create tree object for every dropSpotObject and add to stage
            AppleTreeObject tree = new AppleTreeObject();
            tree.setPosition(object.getX() + 20, object.getY());
            treeObjectGroup.addActor(tree);
            stage.addActor(tree);
            super.addToLoader(tree);
            //set reference to tree in drop spot
            object.treeObject = tree;
        }
        stage.addActor(treeDropSpotObjectGroup);
        super.addToLoader(bigMac, bucket);
    }

    private void handleCollisions() {
        Rectangle playerRect = player.getBounds();
        Polygon polygon = SpikeQuestTiles.getPolygonObjectCollision(tileMap, playerRect, COLLISION_LAYER);
        if(polygon != null) {
            Gdx.app.debug(TAG, "Player collision with collision objects");
            //Draw polygon local vertices from bottom left to top left. Then set position
            Polygon playerPolygon = new Polygon(new float[] { 0, 0, playerRect.getWidth(), 0, playerRect.getWidth(), playerRect.getHeight(), 0, playerRect.getHeight()});
            playerPolygon.setPosition(playerRect.getX(), playerRect.getY());
            MinimumTranslationVector mtv = new MinimumTranslationVector();
            //calculate minimum translation vector to move player away from collision. NOTE player polygon must be first argument
            if(Intersector.overlapConvexPolygons(playerPolygon, polygon, mtv))
                //normal is the direction vector. Depth is the length to move along the vector
                player.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
            return;
        }
       /* for (Actor actor : treeObjectGroup.getChildren()) {
            AppleTreeObject treeObject = (AppleTreeObject)actor;
            Rectangle treeRect = new Rectangle(treeObject.getX(), treeObject.getY(), treeObject.getWidth(), treeObject.getHeight());
            if(treeRect.overlaps(playerRect)) {
                Gdx.app.debug(TAG, "Player collision with tree");
                Rectangle intersectRect = new Rectangle();
                Intersector.intersectRectangles(playerRect, treeRect, intersectRect);
            }
        }*/

    }

    private void buildDropObjectLayer() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        safeDispose(stage, currentBackdropTexture);
        stage.clear();
        super.dispose();
    }
}
