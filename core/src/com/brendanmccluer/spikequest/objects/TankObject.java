package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brend on 6/25/2016.
 */
public class TankObject extends StandardObject {
    private static final String[] filePaths = {"animals/tank/TankStand.atlas", "animals/tank/TankStand.atlas"};
    private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas"};
    private static final int[] maxFrames = {1,1};
    private static final float STARTING_SIZE = 0.075f;
    private static final int HIT_MAX = 3;
    private static final int HIT_ZONE_WIDTH = 1;
    private float speed = 50;
    private float speedLimit = 500;
    private float boostLimit = 1000;
    private Vector2 velocity = new Vector2();
    private float friction = 10;
    public boolean tankHit = false;
    public boolean controlsDisabled = false;
    private int hitCounter = HIT_MAX;
    private long hitTime = 0;
    private boolean blink = false;
    private boolean isShocked = false;


    public TankObject() {
        super(filePaths, fileTypes, maxFrames, STARTING_SIZE, false);

    }

    /***
     * I update the position based on keyboard input
     * and return the distance moved
     */
    public Vector2 update(float delta) {

        if (!isShocked && !controlsDisabled) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && velocity.y < speedLimit)
                velocity.y += speed;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && velocity.y > -speedLimit)
                velocity.y -= speed;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && velocity.x > -speedLimit)
                velocity.x -= speed;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && velocity.x < speedLimit)
                velocity.x += speed;
        }

        //apply friction
        if (velocity.x > 0)
            velocity.x -= friction;
        else if (velocity.x != 0)
            velocity.x += friction;
        if (velocity.y > 0)
            velocity.y -= friction;
        else if (velocity.y != 0)
            velocity.y += friction;

        //blink every second
        if (tankHit && (System.currentTimeMillis() - hitTime >= 500)) {
            hitTime = System.currentTimeMillis();
            blink = !blink;
            if (hitCounter-- <= 0) {
                hitCounter = HIT_MAX;
                tankHit = false;
                isShocked = false;
                blink = false;
            }
        }
        Vector2 scale = velocity.cpy();
        scale.scl(delta);
        setCurrentPositionXY(currentPositionX + scale.x, currentPositionY + scale.y);
        return scale;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!blink)
            super.draw(batch);
    }

    public void stop() {
        velocity = Vector2.Zero;
    }

    /**
     * I bounce tank back by the impact passed and play crash animation
     * //TODO add crash animation
     * @param impact
     */
    public void crash(float impact) {
        velocity.x = -impact;
        //currentPositionX -= getCollisionRectangle().getWidth();
        tankHit = true;
        hitTime = System.currentTimeMillis();

    }

    //I disable Tank's movements for a while and play shock animation
    //TODO add shock animation
    public void shock() {
        if (!isShocked() && !tankHit) {
            isShocked = true;
            velocity = new Vector2(0, -speedLimit*1.5f);
            tankHit = true;
            hitTime = System.currentTimeMillis();
        }
    }

    public boolean isShocked() { return isShocked; }

    /**
     * I return the front collision area of Tank
     * @return
     */
    public Rectangle getCollisionRectangleFront() {
        Rectangle rectangle = super.getCollisionRectangle();
        rectangle.x = rectangle.x + rectangle.getWidth() - HIT_ZONE_WIDTH;
        rectangle.setWidth(HIT_ZONE_WIDTH);
        return rectangle;
    }

    @Override
    public void resetSize() {
        setSize(STARTING_SIZE);
    }
}
