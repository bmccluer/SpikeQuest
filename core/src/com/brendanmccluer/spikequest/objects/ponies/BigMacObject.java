package com.brendanmccluer.spikequest.objects.ponies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.applejackgame.objects.AppleTreeObject;
import com.brendanmccluer.spikequest.interfaces.LoadableObject;
import com.brendanmccluer.spikequest.applejackgame.objects.AppleBucketObject;

/**
 * Created by brend on 11/4/2017.
 */

public class BigMacObject extends Actor implements LoadableObject {
    private static final String BIG_MAC_PNG_FILE = "bigMac/BigMac.png";
    private static final String BIG_MAC_TAG = "BigMacObject";
    private static final float strength = 0.01f;
    private Texture texture;
    private Sprite sprite;
    public boolean isBucking, isAtTarget, isTargeting;
    public float percentComplete = 0;

    public BigMacObject() {
        SpikeQuestGame.instance.assetManager.setAsset(BIG_MAC_PNG_FILE, "Texture");
    }

    public void buck() {
        Gdx.app.debug(BIG_MAC_TAG, "Bucking Animation started");
        isBucking = true;
    }

    public void stopBucking() {
        //TODO: CHANGE ANIMATION
        Gdx.app.debug(BIG_MAC_TAG, "Bucking Animation stopped");
        isBucking = false;
    }

    public boolean isFinishedBucking() {
        return percentComplete >= 1;
    }

    public void reset() {
        isBucking = false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(isBucking) {
            //TODO BUCK ANIMATION
            percentComplete += delta * strength;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public boolean isLoaded() {
        Gdx.app.debug(BIG_MAC_TAG, "Loading assets");
        sprite = new Sprite((Texture)SpikeQuestGame.instance.assetManager.loadAsset(BIG_MAC_PNG_FILE, "Texture"));
        setSize((sprite.getWidth()/5) * 0.5f, (sprite.getHeight()/5) * 0.5f);
        return true;
    }

    @Override
    public void dispose() {
        Gdx.app.debug(BIG_MAC_TAG, "Disposing assets");
        SpikeQuestGame.instance.assetManager.disposeAsset(BIG_MAC_PNG_FILE);
    }
}
