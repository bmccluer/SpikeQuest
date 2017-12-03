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

public class AppleTreeObject extends Actor implements LoadableObject {
    private static final String TAG = "AppleTreeObject";
    private static final String TREE_PNG = "object/appleTree/appleTree.png";
    private Sprite sprite;

    public AppleTreeObject() {
        SpikeQuestGame.instance.assetManager.setAsset(TREE_PNG, "Texture");
    }

    public void shake() {
        Gdx.app.debug(TAG, "Shake animation started");
        //TODO CHANGE ANIMATION
    }

    public void stopShaking() {
        Gdx.app.debug(TAG, "Shake animation stopped");
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
        Gdx.app.debug(TAG, "Loading assets");
        sprite = new Sprite((Texture) SpikeQuestGame.instance.assetManager.loadAsset(TREE_PNG, "Texture"));
        setSize((sprite.getWidth()/2) * 0.5f, (sprite.getHeight()/2) * 0.5f);
        return true;
    }

    @Override
    public void dispose() {
        Gdx.app.debug(TAG, "Disposing assets");
        SpikeQuestGame.instance.assetManager.disposeAsset(TREE_PNG);
    }
}
