package com.brendanmccluer.spikequest.applejackgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.actors.SpikeQuestActor;
import com.brendanmccluer.spikequest.interfaces.LoadableObject;

/**
 * Created by brend on 11/12/2017.
 */

public class ApplejackGamePlayer extends SpikeQuestActor implements LoadableObject {
    private final static String TAG = "ApplejackGamePlayer";
    public Animation animStand, animWalk, animDrop, animPickup, animSwat;
    public float maxVelocity = 1;
    public Vector2 velocity;

    public ApplejackGamePlayer(String textureAtlasPath, String regionName) {
        super(TAG, textureAtlasPath, regionName);
        velocity = new Vector2();
    }

    @Override
    public void act(float delta) {
        //update movement
        setPosition(getX() + (velocity.x * delta), getY() + (velocity.y * delta));
        //act will update the sprite
        super.act(delta);
    }

    @Override
    public boolean isLoaded() {
        if(super.isLoaded()) {
            animStand = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_stand"), Animation.PlayMode.LOOP_PINGPONG);
            animWalk = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_walk"), Animation.PlayMode.LOOP);
            animDrop = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_drop"), Animation.PlayMode.LOOP_PINGPONG);
            currentAnimation = animStand;
        }
        return true;
    }

    @Override
    public void dispose() {
        Gdx.app.debug(TAG, "Disposing assets");
        SpikeQuestGame.instance.assetManager.disposeAsset(textureAtlasPath);
    }

}
