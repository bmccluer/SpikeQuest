package com.brendanmccluer.spikequest.applejackgame.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by brend on 11/5/2017.
 */

public class AppleBucketActor extends Actor {
    private TextureRegion emptyRegion, partialMixedRegion, partialBadRegion, partialGoodRegion,
            fullMixedRegion, fullBadRegion, fullGoodRegion;
    public float percentFull = 0;
    public final String tag;
    public Body body;

    public enum State {
        EMPTY,
        FILLING_MIXED_APPLES,
        FILLING_BAD_APPLES,
        FULL_MIXED_APPLES,
        FULL_BAD_APPLES,
        REMOVING_BAD_APPLES,
        FULL_GOOD_APPLES
    }

    public State state = State.EMPTY;

    public AppleBucketActor(String tag) {
        this.tag = tag;
    }

    public void fillMixedApples(float percentage) {
        if (state != State.FULL_BAD_APPLES || state != State.FULL_GOOD_APPLES || state != State.FULL_MIXED_APPLES) {
            Gdx.app.debug(tag, "Filling apple bucket. Percent Full = " + percentFull + " + " + percentage);
            percentFull += percentage;
            if (percentFull >= 1) {
                if (state == State.FILLING_BAD_APPLES)
                    state = State.FULL_BAD_APPLES;
                else
                    state = State.FULL_MIXED_APPLES;
            }
        } else {
            Gdx.app.debug(tag, "Apple bucket is full. Apples are overflowing!!!");
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (body != null)
            setPosition(body.getPosition().x - getWorldWidth() / 2, body.getPosition().y - getWorldHeight() / 2);
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public void emptyBucket() {
        Gdx.app.debug(tag, "Apple bucket emptied");
        percentFull = 0;
        state = State.EMPTY;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion region = getRegionToDraw();
        setSize(region.getRegionWidth(), region.getRegionHeight());
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    private TextureRegion getRegionToDraw() {
        switch (state) {
            case FILLING_BAD_APPLES:
                if (percentFull > .85f)
                    return fullBadRegion;
                else if (percentFull > .25f)
                    return partialBadRegion;
                else
                    return emptyRegion;
            case FILLING_MIXED_APPLES:
                if (percentFull > .85f)
                    return fullMixedRegion;
                if (percentFull > .25f)
                    return partialMixedRegion;
                else
                    return emptyRegion;
            case FULL_BAD_APPLES:
                return fullBadRegion;
            case FULL_GOOD_APPLES:
                return fullGoodRegion;
            case FULL_MIXED_APPLES:
                return fullMixedRegion;
            case REMOVING_BAD_APPLES:
                if (percentFull > .85f) {
                    return fullMixedRegion;
                } else if (percentFull > .25f) {
                    return partialMixedRegion;
                } else
                    return emptyRegion;
            default:
                return emptyRegion;
        }
    }

    public float getWorldWidth() {
        return getWidth() * getScaleX();
    }

    public float getWorldHeight() {
        return getHeight() * getScaleY();
    }

    public void reload(TextureAtlas textureAtlas, String regionName) {
        Gdx.app.debug(tag, "Refreshing assets");
        emptyRegion = textureAtlas.findRegion(regionName + "_empty");
        partialBadRegion = textureAtlas.findRegion(regionName + "_partial_bad");
        partialGoodRegion = textureAtlas.findRegion(regionName + "_partial_good");
        partialMixedRegion = textureAtlas.findRegion(regionName + "_partial_mixed");
        fullBadRegion = textureAtlas.findRegion(regionName + "_full_bad");
        fullGoodRegion = textureAtlas.findRegion(regionName + "_full_good");
        fullMixedRegion = textureAtlas.findRegion(regionName + "_full_mixed");
        setSize(emptyRegion.getRegionWidth(), emptyRegion.getRegionHeight());
    }
}
