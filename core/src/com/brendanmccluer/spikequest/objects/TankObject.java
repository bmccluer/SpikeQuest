package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brend on 6/25/2016.
 */
public class TankObject extends StandardObject {
    private static final String[] filePaths = {"animals/tank/TankStand.atlas", "animals/tank/TankStand.atlas"};
    private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas"};
    private static final int[] maxFrames = {1,1};
    private static final float STARTING_SIZE = 0.075f;
    private float speed = 50;
    private float speedLimit = 500;
    private float boostLimit = 1000;
    private Vector2 velocity = new Vector2();
    private float friction = 10;


    public TankObject() {
        super(filePaths, fileTypes, maxFrames, STARTING_SIZE, false);

    }

    /***
     * I update the position based on keyboard input
     * and return the distance moved
     */
    public Vector2 update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && velocity.y < speedLimit)
            velocity.y += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && velocity.y > -speedLimit)
            velocity.y -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && velocity.x > -speedLimit)
            velocity.x -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && velocity.x < speedLimit)
            velocity.x += speed;

        //apply friction
        if (velocity.x > 0)
            velocity.x -= friction;
        else if (velocity.x != 0)
            velocity.x += friction;
        if (velocity.y > 0)
            velocity.y -= friction;
        else if (velocity.y != 0)
            velocity.y += friction;

        Vector2 scale = velocity.cpy();
        scale.scl(delta);
        setCurrentPositionXY(currentPositionX + scale.x, currentPositionY + scale.y);
        return scale;
    }

    public void stop() {
        velocity = Vector2.Zero;
    }

    @Override
    public void resetSize() {
        setSize(STARTING_SIZE);
    }
}
