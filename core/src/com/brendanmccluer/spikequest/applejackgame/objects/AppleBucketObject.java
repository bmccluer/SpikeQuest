package com.brendanmccluer.spikequest.applejackgame.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.interfaces.CreatableObject;
import com.brendanmccluer.spikequest.interfaces.LoadableObject;

/**
 * Created by brend on 11/5/2017.
 */

public class AppleBucketObject extends Actor implements LoadableObject {
    private static final String APPLE_BUCKET_TAG = "AppleBucketObject";
    private static final String APPLE_BUCKET_PNG = "object/appleBucket/appleBucket.png";
    private Sprite sprite;
    public boolean containsGoodApples, containsBadApples;
    public float percentFull = 0;

    public AppleBucketObject() {
        SpikeQuestGame.instance.assetManager.setAsset(APPLE_BUCKET_PNG, "Texture");
    }

    public void fillMixedApples(float percentage) {
        if(!isFull()) {
            Gdx.app.debug(APPLE_BUCKET_TAG, "Filling apple bucket. Percent Full = " + percentFull + " + " + percentage);
            percentFull += percentage;
            containsBadApples = true;
            containsGoodApples = true;
        }
        else {
            Gdx.app.debug(APPLE_BUCKET_TAG, "Apple bucket is full. Apples are overflowing!!!");
        }
    }

    public void emptyBucket() {
        Gdx.app.debug(APPLE_BUCKET_TAG, "Apple bucket emptied");
        percentFull = 0;
        containsBadApples = false;
        containsGoodApples = false;
    }

    public boolean isEmp() {
        return  percentFull <= 0;
    }

    public boolean isFull() {
        return percentFull >= 1;
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
        Gdx.app.debug(APPLE_BUCKET_TAG, "Loading assets");
        sprite = new Sprite((Texture) SpikeQuestGame.instance.assetManager.loadAsset("object/appleBucket/appleBucket.png", "Texture"));
        setSize((sprite.getWidth()/2) * 0.5f, (sprite.getHeight()/2) * 0.5f);
        return true;
    }

    @Override
    public void dispose() {
        Gdx.app.debug(APPLE_BUCKET_TAG, "Disposing assets");
        SpikeQuestGame.instance.assetManager.disposeAsset(APPLE_BUCKET_PNG);
    }
}
