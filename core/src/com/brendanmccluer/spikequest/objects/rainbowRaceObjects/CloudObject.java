package com.brendanmccluer.spikequest.objects.rainbowRaceObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.interfaces.RainbowRaceObject;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;
import com.brendanmccluer.spikequest.objects.StandardObject;

/**
 * Created by brend on 8/6/2016.
 */
public class CloudObject extends StandardObject implements RainbowRaceObject {
    private static final String[] filePaths = {"object/clouds/white/CloudStand.atlas"};
    private static final String[] fileTypes = {"TextureAtlas"};
    private static final int[] maxFrames = {9};
    private static final float SIZE = 0.5f;
    public Vector2 position;

    public CloudObject() {
        super(filePaths, fileTypes, maxFrames, SIZE, false);
        position = new Vector2();
        gravity = 0;
    }

    @Override
    public Vector2 position() {
        return new Vector2(currentPositionX, currentPositionY);
    }

    @Override
    public void setPosition(float xPos, float yPos) {
        if (currentSprite == null)
            spawn(xPos, yPos);
        else
            setCurrentPositionXY(xPos, yPos);
    }

    @Override
    public void update(float speed) {
        standStill();
    }

    @Override
    public void render(SpriteBatch batch, SpikeQuestCamera camera) {
        draw(batch);
    }

    @Override
    public boolean isColliding(Rectangle rectangle) {
    return getCollisionRectangle().overlaps(rectangle);
}

    @Override
    public void resetSize() {
        currentSize = SIZE;
    }
}
