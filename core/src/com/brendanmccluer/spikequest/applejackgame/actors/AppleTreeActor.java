package com.brendanmccluer.spikequest.applejackgame.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brendanmccluer.spikequest.actors.SpikeQuestAbstractAnimatedActor;

/**
 * Created by brend on 11/5/2017.
 */

public class AppleTreeActor extends SpikeQuestAbstractAnimatedActor {
    public State state = State.REST;
    public Animation restAnimation, shakeAnimation;

    public AppleTreeActor(String tag) {
        super(tag);
    }

    public enum State {
        REST,
        SHAKE
    }

    @Override
    protected void update(float delta) {
        if (state == State.SHAKE && currentAnimation != shakeAnimation) {
            setCurrentAnimation(shakeAnimation);
        }
        else if (state == State.REST && currentAnimation != restAnimation) {
            setCurrentAnimation(restAnimation);
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
    protected void setAnimations(TextureAtlas textureAtlas, String regionName) {
        restAnimation = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_rest"), Animation.PlayMode.LOOP_PINGPONG);
        shakeAnimation = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_shake"), Animation.PlayMode.LOOP);
    }

    @Override
    protected Animation getDefaultAnimation() {
        return restAnimation;
    }
}
