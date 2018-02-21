package com.brendanmccluer.spikequest.applejackgame.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.brendanmccluer.spikequest.actors.SpikeQuestAbstractAnimatedActor;

/**
 * Created by brend on 11/12/2017.
 */

public class ApplejackGamePlayerActor extends SpikeQuestAbstractAnimatedActor {
    public Animation animStand, animWalk, animDrop, animPickup, animSwat;
    public float maxVelocity = 1;
    public Vector2 velocity;
    public Body body;
    public Body heldBody;

    public ApplejackGamePlayerActor(String tag) {
        super(tag);
        velocity = new Vector2();
    }

    @Override
    public void update(float delta) {
        if(body == null) {
            setPosition(getX() + (velocity.x * delta), getY() + (velocity.y * delta));
        }
        else {
            setPosition(body.getPosition().x - getWorldWidth()/2, body.getPosition().y - getWorldHeight()/2);
            //setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        }
    }

    @Override
    protected void setAnimations(TextureAtlas textureAtlas, String regionName) {
        animStand = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_stand"), Animation.PlayMode.LOOP_PINGPONG);
        animWalk = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_walk"), Animation.PlayMode.LOOP);
        animDrop = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_drop"), Animation.PlayMode.LOOP_PINGPONG);
    }

    @Override
    protected Animation getDefaultAnimation() {
        return animStand;
    }
}
