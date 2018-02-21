package com.brendanmccluer.spikequest.applejackgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.brendanmccluer.spikequest.SpikeQuestAssets;
import com.brendanmccluer.spikequest.SpikeQuestBox2D;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.actors.SpikeQuestActorComparator;
import com.brendanmccluer.spikequest.applejackgame.actors.AppleBucketActor;
import com.brendanmccluer.spikequest.applejackgame.actors.AppleTreeActor;
import com.brendanmccluer.spikequest.applejackgame.actors.ApplejackGamePlayerActor;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.applejackgame.objects.TreeDropSpotObject;
import com.brendanmccluer.spikequest.applejackgame.actors.BigMacActor;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.tiles.SpikeQuestTiles;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.badlogic.gdx.Gdx.input;

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
    private ApplejackGamePlayerActor player;
    private BigMacActor bigMac;
    private static final float BIG_MAC_SPEED = 100;
    private static final float PLAYER_SPEED = 5;
    private static final float PLAYER_RUN_SPEED = 600;
    private Group treeDropSpotObjectGroup;
    private TiledMap tileMap;
    private TiledMapRenderer tiledMapRenderer;
    private List<Rectangle> collisionRects;
    private List<AppleBucketActor> appleBucketObjectList;
    private List<AppleTreeActor> appleTreeObjectList;
   // private int[] renderLayers = { RENDER_LAYER }; //layers that are rendered in the tile map
    private Action buckTreeAction;
    private Action dropHitDetectionAction;

    private World b2world;
    private Joint playerBucketJoint;
    private Box2DDebugRenderer b2debugRenderer;
    private static final short PLAYER_ENTITY = 0x1;    // 0001
    private static final short SENSOR_ENTITY = 0x1 << 1; //0010
    private static Body bodyToPickup = null;

    //target objects
    private AppleBucketActor bigMacTargetedBucket;
    private AppleTreeActor bigMacTargetedTree;

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
            reload();
            buildStage();
            resetObjectsPos();
            buildBox2DWorld();
            buildBox2DContactListeners();
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
       /* if(Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            if(player.heldBody == null) {
                player.heldBody = bodyToPickup;
                player.heldBody.setTransform(player.getX() + player.getWorldWidth() + pickupBucket.getWorldWidth() / 2, player.getY() + pickupBucket.getWorldHeight() / 2, pickupBucket.body.getAngle());
                player.heldBody.setType(BodyDef.BodyType.StaticBody);
                b2world.destroyJoint(playerBucketJoint);
                playerBucketJoint = null;
            }
            else if(pickupBucket != null) {
                pickupBucket.body.setType(BodyDef.BodyType.DynamicBody);
                pickupBucket.body.setTransform(player.getX() + player.getWorldWidth()/2, player.getY() + player.getWorldHeight() + pickupBucket.getWorldHeight()/2, pickupBucket.body.getAngle());
                WeldJointDef weldJointDef = new WeldJointDef();
                weldJointDef.initialize(player.body, pickupBucket.body, new Vector2(player.getX(), player.getY() + player.getWorldHeight()));
                playerBucketJoint = b2world.createJoint(weldJointDef);
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
        /*if(bigMac.isBucking && bigMacTargetedBucket != null && bigMacTargetedTree != null) {
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
                if(dropSpotObject.heldActor instanceof AppleBucketActor) {
                    AppleBucketActor appleBucketObject = (AppleBucketActor) dropSpotObject.heldActor;
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
        }*/

    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.debug(TAG, "Resizing Game");
    }

    public void initialize() {
        Gdx.app.debug(TAG, "Initializing Game");
        game.assetManager.setAsset(SpikeQuestAssets.BIG_MAC_TEXTURE_ATLAS, "TextureAtlas");
        game.assetManager.setAsset(SpikeQuestAssets.AJGAME_OBJECTS_TEXTURE_ATLAS, "TextureAtlas");
        game.assetManager.setAsset(SpikeQuestAssets.SPIKE_MAIN_TEXTURE_ATLAS, "TextureAtlas");
        gameCamera = new SpikeQuestCamera(Gdx.graphics.getWidth(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage();
        bigMac = new BigMacActor("BigMacActor");
        player = new ApplejackGamePlayerActor("AppleJackGamePlayer");
        appleTreeObjectList = new ArrayList<>();
        //map must be loaded to get object layer counts
        reloadMap();
        for(int i=0; i < SpikeQuestTiles.getTileMapObjectCount(tileMap, TREE_DROP_OBJECT_LAYER); i++) {
            AppleTreeActor appleTreeObject = new AppleTreeActor("AppleTree " + i);
            appleTreeObjectList.add(appleTreeObject);
        }
        appleBucketObjectList = new ArrayList<>();
        for(int i=0; i < NUM_BUCKETS; i++) {
            AppleBucketActor bucketObject = new AppleBucketActor("AppleBucket " + i);
            appleBucketObjectList.add(bucketObject);
        }
    }

    ///Reload the actors and assets
    private void reload() {
        Gdx.app.debug(TAG, "Reloading Game");
        currentBackdropTexture = new Texture(Gdx.files.internal("applejackGame/applejackGameBackground.png"));
        reloadMap();
        player.reload((TextureAtlas) game.assetManager.loadAsset(SpikeQuestAssets.SPIKE_MAIN_TEXTURE_ATLAS, "TextureAtlas"), "spike");
        player.setScale(0.25f);
        bigMac.reload((TextureAtlas) game.assetManager.loadAsset(SpikeQuestAssets.BIG_MAC_TEXTURE_ATLAS, "TextureAtlas"), "bigMac");
        for (AppleBucketActor appleBucket : appleBucketObjectList) {
            appleBucket.reload((TextureAtlas) game.assetManager.loadAsset(SpikeQuestAssets.AJGAME_OBJECTS_TEXTURE_ATLAS, "textureAtlas"), "appleBucket");
        }
        for (AppleTreeActor appleTree : appleTreeObjectList) {
            appleTree.reload((TextureAtlas) game.assetManager.loadAsset(SpikeQuestAssets.AJGAME_OBJECTS_TEXTURE_ATLAS, "textureAtlas"), "appleTree");
        }
        convertSizeToMeters(bigMac, player);
        appleTreeObjectList.forEach(this::convertSizeToMeters);
        appleBucketObjectList.forEach(this::convertSizeToMeters);
    }

    private void reloadMap() {
        TmxMapLoader mapLoader = new TmxMapLoader();
        tileMap = mapLoader.load("tileMaps/AppleJackGame.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);
    }

    @Override
    public void show() {
        initialize();
        if(SpikeQuestGame.debugMode)
            b2debugRenderer = new Box2DDebugRenderer();
    }

    private void buildStage() {
        Gdx.app.debug(TAG, "Building Stage");
        stage.clear();
        //IMPORTANT: STAGE IS SET IN TERMS OF METERS, NOT PIXELS!
        OrthographicCamera stageCamera = new OrthographicCamera(VIEWPORT_METERS_WIDTH, VIEWPORT_METERS_HEIGHT);
        stageCamera.position.set(stageCamera.viewportWidth/2f, stageCamera.viewportHeight/2f, 0);
        stage.setViewport(new StretchViewport(VIEWPORT_METERS_WIDTH, VIEWPORT_METERS_HEIGHT, stageCamera));
        stage.addActor(bigMac);
        stage.addActor(player);
        Iterator<AppleTreeActor> treeIterator = appleTreeObjectList.iterator();
        treeDropSpotObjectGroup = new Group();
        //treeObjectGroup = new Group();
        for(Rectangle rect : SpikeQuestTiles.getTileMapRectangles(tileMap, TREE_DROP_OBJECT_LAYER)){
            //rectangles are in pixels. Must convert to meters
            rect.set(rect.getX() / PPM_WIDTH, rect.getY() / PPM_HEIGHT, rect.getWidth() / PPM_WIDTH, rect.getHeight() / PPM_HEIGHT);
            TreeDropSpotObject object = new TreeDropSpotObject();
            AppleTreeActor appleTreeObject = treeIterator.next();
            object.setPosition(rect.getX(), rect.getY());
            object.setSize(rect.getWidth(), rect.getHeight());
            treeDropSpotObjectGroup.addActor(object);
            object.treeObject = appleTreeObject;
            object.treeObject.setPosition(object.getX() + 0.5f, object.getY());
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
        }
        bigMac.setPosition(VIEWPORT_METERS_WIDTH - bigMac.getWorldWidth(), appleTreeObjectList.get(0).getY());
    }

    private void convertSizeToMeters(Actor... actors) {
        for (Actor actor : actors) {
            convertSizeToMeters(actor);
        }
    }

    private void convertSizeToMeters(Actor actor) {
        actor.setSize(actor.getWidth() / PPM_WIDTH, actor.getHeight() / PPM_HEIGHT);
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
        player.body.setUserData(player);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape playerShape = SpikeQuestBox2D.buildRectangleShape(player.getWorldWidth(), player.getWorldHeight());
        fixtureDef.shape = playerShape;
        fixtureDef.density = 1;
        fixtureDef.restitution = 0.0f;
        fixtureDef.friction = 0.1f;
        fixtureDef.filter.categoryBits = PLAYER_ENTITY;
        player.body.createFixture(fixtureDef);
        playerShape.dispose();

        Gdx.app.debug(TAG, "Creating Box2D for Apple Trees");
        for(Actor actor : appleTreeObjectList) {
            AppleTreeActor appleTreeObject = (AppleTreeActor) actor;
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

        Gdx.app.debug(TAG, "Creating Box2D for Apple Buckets");
        for(AppleBucketActor bucketObject : appleBucketObjectList) {
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(bucketObject.getX(), bucketObject.getY());
            bodyDef.fixedRotation = true;
            bucketObject.body = b2world.createBody(bodyDef);
            bucketObject.body.setUserData(bucketObject);
            fixtureDef = new FixtureDef();
            PolygonShape bucketShape = SpikeQuestBox2D.buildRectangleShape(bucketObject.getWorldWidth(), bucketObject.getWorldHeight());
            fixtureDef.shape = bucketShape;
            fixtureDef.density = 5;
            fixtureDef.restitution = 0.1f;
            fixtureDef.friction = 3f;
            bucketObject.body.createFixture(fixtureDef);
            bucketShape.dispose();
            //create sensor for picking object up
            fixtureDef = new FixtureDef();
            PolygonShape bucketSensorShape = SpikeQuestBox2D.buildRectangleShape(bucketObject.getWorldWidth() + 0.5f, bucketObject.getWorldHeight() + 0.5f);
            fixtureDef.shape = bucketSensorShape;
            fixtureDef.isSensor = true;
            fixtureDef.filter.categoryBits = SENSOR_ENTITY;
            fixtureDef.filter.maskBits = PLAYER_ENTITY; //only collide with player
            bucketObject.body.createFixture(fixtureDef);
            bucketSensorShape.dispose();

        }

    }

    private void buildBox2DContactListeners() {
        b2world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if(player.heldBody != null)
                    return;
                Object userData1 = contact.getFixtureA().getBody().getUserData();
                Object userData2 = contact.getFixtureB().getBody().getUserData();
                Gdx.app.debug(TAG, String.format("Collision begin detected! Body1 = %s Body2 = %s", userData1, userData2));
                if (userData1 instanceof ApplejackGamePlayerActor) {
                    bodyToPickup = contact.getFixtureA().getBody();
                }
                else {
                    bodyToPickup = contact.getFixtureB().getBody();
                }
            }

            @Override
            public void endContact(Contact contact) {
                Object userData1 = contact.getFixtureA().getBody().getUserData();
                Object userData2 = contact.getFixtureB().getBody().getUserData();
                Gdx.app.debug(TAG, String.format("Collision end detected! Body1 = %s Body2 = %s", userData1, userData2));
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

   /* private void setBodyToPickup(Object userData1, Object userData2, boolean value) {
        AppleBucketActor bucketObject = null;
        if(userData1 instanceof ApplejackGamePlayerActor) {
            bodyToPickup = userData1;
        }
        else if(userData2 instanceof AppleBucketActor) {
            bucketObject = (AppleBucketActor) userData2;
        }
        if(bucketObject != null) {
            pickupBucket = bucketObject;
        }
    }*/

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
            AppleTreeActor treeObject = (AppleTreeActor)actor;
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
        Gdx.app.debug(TAG, "Game Hidden");
        dispose();
    }

    @Override
    public void pause() {
        Gdx.app.debug(TAG, "Game Paused");
    }

    @Override
    public void resume() {
        Gdx.app.debug(TAG, "Game Resumed");
        game.assetManager.manager.finishLoading();
        reload();
    }

    @Override
    public void dispose() {
        safeDispose(stage, currentBackdropTexture, b2world);
        if(b2debugRenderer != null)
            b2debugRenderer.dispose();
        stage.clear();
        super.dispose();
        game.assetManager.disposeAllAssets();
    }
}
