package com.brendanmccluer.spikequest.applejackgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.brendanmccluer.spikequest.SpikeQuestBox2D;
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
    private static final float PPM_WIDTH = 100;
    private static final float PPM_HEIGHT = 100;
    private static final float VIEWPORT_METERS_WIDTH = Gdx.graphics.getWidth()/ PPM_WIDTH;
    private static final float VIEWPORT_METERS_HEIGHT = Gdx.graphics.getHeight() / PPM_HEIGHT;
    private static final int NUM_BUCKETS = 3;

    private Stage stage; //stage in pixels
    private ApplejackGamePlayer player;
    private BigMacObject bigMac;
    private static final float BIG_MAC_SPEED = 100;
    private static final float PLAYER_SPEED = 5;
    private static final float PLAYER_RUN_SPEED = 600;
    private Group treeDropSpotObjectGroup;
    private TiledMap tileMap;
    private TiledMapRenderer tiledMapRenderer;
    private List<Rectangle> collisionRects;
    private List<Actor> appleBucketObjectList;
    private List<Actor> appleTreeObjectList;
   // private int[] renderLayers = { RENDER_LAYER }; //layers that are rendered in the tile map
    private Action buckTreeAction;
    private Action dropHitDetectionAction;

    private World b2world;
    private Box2DDebugRenderer b2debugRenderer;

    //target objects
    private AppleBucketObject bigMacTargetedBucket;
    private AppleTreeObject bigMacTargetedTree;

    public AppleJackGameScreen(SpikeQuestGame game) {
        super(game);
    }

    @Override
    public void render(float delta) {
        //don't exceed 0.25 for change in time
        delta = Math.min(delta, 0.25f);
        refresh();
        super.useLoadingScreen(delta);
        if(!isLoaded())
            return;
        if(!screenStart) {
            buildStage();
            //null the lists. We don't need them anymore
            convertSizeToMeters(bigMac, player);
            convertSizeToMeters(appleTreeObjectList);
            convertSizeToMeters(appleBucketObjectList);
            resetObjectsPos();
            buildBox2DWorld();
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
        game.batch.setProjectionMatrix(stage.getCamera().projection);
        game.batch.begin();
        //tiledMapRenderer.render();
        game.batch.draw(currentBackdropTexture, -VIEWPORT_METERS_WIDTH/2, -VIEWPORT_METERS_HEIGHT/2, VIEWPORT_METERS_WIDTH, VIEWPORT_METERS_HEIGHT);
        game.batch.end();
        stage.draw();

        if(SpikeQuestGame.debugMode) {
            gameCamera.attachToBatch(game.batch);
            //SpikeQuestShapeRenderer renderer = new SpikeQuestShapeRenderer();
            //renderer.drawRectangle(player.getBounds(), stage.getCamera());
            //SpikeQuestTiles.drawPolygons(tileMap, COLLISION_LAYER, gameCamera);
        }
        if(b2debugRenderer != null && b2world != null) {
            b2debugRenderer.render(b2world, stage.getCamera().combined);
        }
        b2world.step(delta, 8, 3);
    }

    private void updateController(float delta) {
        boolean moving = false;
        player.body.setLinearVelocity(Vector2.Zero);
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if((player.getY() + player.getWorldHeight()) < VIEWPORT_METERS_HEIGHT) {
                player.body.setLinearVelocity(player.body.getLinearVelocity().x, PLAYER_SPEED);
            }
            //player.velocity.y = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? PLAYER_RUN_SPEED : PLAYER_SPEED;
            player.setCurrentAnimation(player.animWalk);
            moving = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if(player.getY() > 0)
                player.body.setLinearVelocity(player.body.getLinearVelocity().x, -PLAYER_SPEED);
            player.setCurrentAnimation(player.animWalk);
            moving = true;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(player.getX() > 0)
                player.body.setLinearVelocity(-PLAYER_SPEED, player.body.getLinearVelocity().y);
            player.setCurrentAnimation(player.animWalk);
            player.setFlip(true, false);
            moving = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(player.getX() + player.getWorldWidth() < VIEWPORT_METERS_WIDTH)
                player.body.setLinearVelocity(PLAYER_SPEED, player.body.getLinearVelocity().y);
            player.setCurrentAnimation(player.animWalk);
            player.setFlip(false, false);
            moving = true;
        }
        if(!moving) {
            player.setCurrentAnimation(player.animStand);
        }

        /*if(input.justTouched()) {
            bucket.setPosition(gameCamera.getMousePositionX() / PPM_WIDTH - bucket.getWorldWidth()/2, gameCamera.getMousePositionY() / PPM_HEIGHT - bucket.getWorldHeight()/2);
            for(Actor dropSpotObject : treeDropSpotObjectGroup.getChildren()) {
                ((TreeDropSpotObject)dropSpotObject).removeActor();
            }
            //TODO: Use Spike to place bucket and remove bucket
            TreeDropSpotObject dropSpotObject = (TreeDropSpotObject) treeDropSpotObjectGroup.hit(bucket.getCenterX(), bucket.getCenterY(), false);
            if(dropSpotObject != null) {
                Gdx.app.debug(TAG, "Tree Drop Spot hit by bucket");
                dropSpotObject.heldActor = bucket;
            }
        }*/
        //debug controls
        if(SpikeQuestGame.debugMode) {
            /*if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                bucket.emptyBucket();
            }*/
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
        Gdx.app.debug(TAG, "Resizing Game");
    }

    public void initialize() {
        Gdx.app.debug(TAG, "Initializing Game");
        gameCamera = new SpikeQuestCamera(Gdx.graphics.getWidth(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        currentBackdropTexture = new Texture(Gdx.files.internal("Backdrop/ApplejackGameBackground.png"));
        stage = new Stage();
        TmxMapLoader mapLoader = new TmxMapLoader();
        tileMap = mapLoader.load("tileMaps/AppleJackGame.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);
        bigMac = new BigMacObject();
        player = new ApplejackGamePlayer(SpikeQuestStaticFilePaths.SPIKE_MAIN_TEXTURE_ATLAS, "spike");
        super.addToLoader(bigMac, player);
        appleTreeObjectList = new ArrayList<Actor>();
        for(int i=0; i < SpikeQuestTiles.getTileMapObjectCount(tileMap, TREE_DROP_OBJECT_LAYER); i++) {
            AppleTreeObject appleTreeObject = new AppleTreeObject();
            appleTreeObjectList.add(appleTreeObject);
            super.addToLoader(appleTreeObject);
        }
        appleBucketObjectList = new ArrayList<Actor>();
        for(int i=0; i < NUM_BUCKETS; i++) {
            AppleBucketObject bucketObject = new AppleBucketObject();
            appleBucketObjectList.add(bucketObject);
            super.addToLoader(bucketObject);
        }
    }

    @Override
    public void show() {
        initialize();
        buildActions();
        if(SpikeQuestGame.debugMode)
            b2debugRenderer = new Box2DDebugRenderer();
    }

    private void buildActions() {
        Gdx.app.debug(TAG, "Building actions");
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
        Gdx.app.debug(TAG, "Building Stage");
        stage.clear();
        //IMPORTANT: STAGE IS SET IN TERMS OF METERS, NOT PIXELS!
        OrthographicCamera stageCamera = new OrthographicCamera(VIEWPORT_METERS_WIDTH, VIEWPORT_METERS_HEIGHT);
        stageCamera.position.set(stageCamera.viewportWidth/2f, stageCamera.viewportHeight/2f, 0);
        stage.setViewport(new StretchViewport(VIEWPORT_METERS_WIDTH, VIEWPORT_METERS_HEIGHT, stageCamera));
        bigMac.setScale(0.15f);
        player.setScale(0.25f);
        stage.addActor(bigMac);
        stage.addActor(player);
        Iterator<Actor> treeIterator = appleTreeObjectList.iterator();
        treeDropSpotObjectGroup = new Group();
        //treeObjectGroup = new Group();
        for(Rectangle rect : SpikeQuestTiles.getTileMapRectangles(tileMap, TREE_DROP_OBJECT_LAYER)){
            //rectangles are in pixels. Must convert to meters
            rect.set(rect.getX() / PPM_WIDTH, rect.getY() / PPM_HEIGHT, rect.getWidth() / PPM_WIDTH, rect.getHeight() / PPM_HEIGHT);
            TreeDropSpotObject object = new TreeDropSpotObject();
            AppleTreeObject appleTreeObject = (AppleTreeObject) treeIterator.next();
            object.setPosition(rect.getX(), rect.getY());
            object.setSize(rect.getWidth(), rect.getHeight());
            treeDropSpotObjectGroup.addActor(object);
            object.treeObject = appleTreeObject;
            object.treeObject.setPosition(object.getX() + 0.5f, object.getY());
            object.treeObject.setScale(0.30f);
            stage.addActor(appleTreeObject);
            //treeObjectGroup.addActor(appleTreeObject);
        }
        for(Actor actor : appleBucketObjectList) {
            //bucketObjectGroup.addActor(actor);
            stage.addActor(actor);
        }
        //stage.addActor(bucketObjectGroup);
        stage.addActor(treeDropSpotObjectGroup);
        //stage.addActor(treeObjectGroup);
        input.setInputProcessor(stage);
        //stage.setDebugAll(SpikeQuestGame.debugMode);
    }

    private void resetObjectsPos () {
        int posX = 3;
        for(Actor actor : appleBucketObjectList) {
            actor.setPosition(posX++, 2);
            actor.setScale(0.25f);
        }
        bigMac.setPosition(VIEWPORT_METERS_WIDTH - bigMac.getWorldWidth(), appleTreeObjectList.get(0).getY());
    }

    private void convertSizeToMeters(Actor... actors) {
        for (Actor actor : actors) {
            actor.setSize(actor.getWidth() / PPM_WIDTH, actor.getHeight() / PPM_HEIGHT);
        }
    }

    private void convertSizeToMeters(List<Actor> actors) {
        for (Actor actor : actors) {
            actor.setSize(actor.getWidth() / PPM_WIDTH, actor.getHeight() / PPM_HEIGHT);
        }
    }

    private void buildBox2DWorld() {
        Gdx.app.debug(TAG, "Building Box2D");
        if(b2world != null)
            b2world.dispose();
        //create world with no gravity
        b2world = new World(new Vector2(0, 0), true);

        Gdx.app.debug(TAG, "Creating Box2D for Player");
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(player.getX(), player.getY());
        bodyDef.fixedRotation = true;
        player.body = b2world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape playerShape = SpikeQuestBox2D.buildRectangleShape(player.getWorldWidth(), player.getWorldHeight());
        fixtureDef.shape = playerShape;
        fixtureDef.density = 1;
        fixtureDef.restitution = 0.1f;
        fixtureDef.friction = 0.1f;
        player.body.createFixture(fixtureDef);
        playerShape.dispose();

        Gdx.app.debug(TAG, "Creating Box2D for Apple Trees");
        for(Actor actor : appleTreeObjectList) {
            AppleTreeObject appleTreeObject = (AppleTreeObject) actor;
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(actor.getX() + 0.95f, appleTreeObject.getY() + appleTreeObject.getWorldHeight()/10);
            //creating the body adds it to the world
            Body body = b2world.createBody(bodyDef);
            PolygonShape rectShape = SpikeQuestBox2D.buildRectangleShape(0.70f, 0.15f);
            body.createFixture(rectShape, 0.0f);
            rectShape.dispose();
        }

        Gdx.app.debug(TAG, "Creating Box2D for Collision Map Polygons");
        for(Polygon polygon : SpikeQuestTiles.getPolygons(tileMap, COLLISION_LAYER)) {
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(0, 0);
            Body body = b2world.createBody(bodyDef);
            PolygonShape polygonShape = SpikeQuestBox2D.buildPolygonShape(polygon.getTransformedVertices(), PPM_WIDTH);
            body.createFixture(polygonShape, 0.0f);
            polygonShape.dispose();
        }

    }

    private void handleCollisions() {
       /* Rectangle playerRect = player.getBounds();
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
                player.moveBy(mtv.normal.x * (mtv.depth + 1), mtv.normal.y * (mtv.depth+1));
            return;
        }*/

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
        safeDispose(stage, currentBackdropTexture, b2world);
        if(b2debugRenderer != null)
            b2debugRenderer.dispose();
        stage.clear();
        super.dispose();
    }
}
