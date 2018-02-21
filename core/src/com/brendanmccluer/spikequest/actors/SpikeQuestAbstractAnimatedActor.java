package com.brendanmccluer.spikequest.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by brend on 11/12/2017.
 */

public abstract class SpikeQuestAbstractAnimatedActor extends Actor {
    public Animation currentAnimation;
    public Sprite sprite;
    protected final String tag;
    protected float stateTime;
    public boolean isFlipX, isFlipY;

    public SpikeQuestAbstractAnimatedActor(String tag) {
        this.tag = tag;
        sprite = new Sprite();
    }

    public void setFlip(boolean isFlipX, boolean isFlipY) {
        this.isFlipX = isFlipX;
        this.isFlipY = isFlipY;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        update(delta);
        updateSpriteCurrentFrame();
    }

    protected abstract void update(float delta);

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

    protected abstract void setAnimations(TextureAtlas textureAtlas, String regionName);

    protected abstract Animation getDefaultAnimation();

    public void reload(TextureAtlas textureAtlas, String regionName) {
        Gdx.app.debug(tag, "Refreshing assets");
        setAnimations(textureAtlas, regionName);
        setCurrentAnimation(getDefaultAnimation());
        updateSpriteCurrentFrame();
        setSize(getDefaultAnimation().getKeyFrame(0).getRegionWidth(), getDefaultAnimation().getKeyFrame(0).getRegionHeight());
    }

    public void setCurrentAnimation(Animation currentAnimation) {
        if (this.currentAnimation != currentAnimation) {
            this.currentAnimation = currentAnimation;
            stateTime = 0;
        }
    }
}
