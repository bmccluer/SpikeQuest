package com.brendanmccluer.spikequest.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.interfaces.LoadableObject;

/**
 * Created by brend on 11/12/2017.
 */

public abstract class SpikeQuestActor extends Actor implements LoadableObject {
    public final String textureAtlasPath;
    public final String regionName;
    public Animation currentAnimation;
    public Sprite sprite;
    public TextureAtlas textureAtlas;
    protected final String tag;
    protected float stateTime;
    public boolean isFlipX, isFlipY;
    public Vector2 previousPosition;

    public SpikeQuestActor(String tag, String textureAtlasPath, String regionName) {
        this.tag = tag;
        this.textureAtlasPath = textureAtlasPath;
        this.regionName = regionName;
        SpikeQuestGame.instance.assetManager.setAsset(textureAtlasPath, "TextureAtlas");
        previousPosition = new Vector2();
    }

    public void setFlip(boolean isFlipX, boolean isFlipY) {
        this.isFlipX = isFlipX;
        this.isFlipY = isFlipY;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        updateSpriteCurrentFrame();
    }

    public void updateSpriteCurrentFrame() {
        TextureRegion region = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(region);
        sprite.setFlip(isFlipX, isFlipY);
        sprite.setSize(getWidth(), getHeight());
        sprite.setPosition(getX(), getY());
        sprite.setOrigin(getOriginX(), getOriginY());
        sprite.setRotation(getRotation());
        sprite.setScale(getScaleX(), getScaleY());
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public float getWorldWidth() {
        return getWidth() * getScaleX();
    }

    public float getWorldHeight() {
        return getHeight() * getScaleY();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        sprite.setColor(color);
        sprite.draw(batch);
    }

    @Override
    public boolean isLoaded() {
        Gdx.app.debug(tag, "Loading assets");
        textureAtlas = (TextureAtlas) SpikeQuestGame.instance.assetManager.loadAsset(textureAtlasPath, "TextureAtlas");
        sprite = new Sprite();
        //set as default
        setSize(textureAtlas.getRegions().first().getRegionWidth(), textureAtlas.getRegions().first().getRegionHeight());
        return true;
    }

    public void setCurrentAnimation(Animation currentAnimation) {
        if (this.currentAnimation != currentAnimation) {
            this.currentAnimation = currentAnimation;
            stateTime = 0;
        }
    }

    @Override
    public void dispose() {
        Gdx.app.debug(tag, "Disposing assets");
        SpikeQuestGame.instance.assetManager.disposeAsset(textureAtlasPath);
    }
}
