package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.interfaces.RainbowRaceObject;

import java.util.Random;

/**
 * Created by brend on 6/28/2016.
 */
public class RingObject extends AbstractSpikeQuestObject implements RainbowRaceObject {
    public enum RING_TYPE {
        RED,
        YELLOW,
        GREEN;
    }
    private static final String[] filePaths = {"object/rings/FrontRingRed.png",
            "object/rings/RingBackRed.png",
            "object/rings/RingFrontGreen.png",
            "object/rings/RingBackGreen.png",
            "object/rings/RingFrontYellow.png",
            "object/rings/RingBackYellow.png"};
    private static final String[] fileTypes = {"Texture", "Texture", "Texture", "Texture", "Texture", "Texture"};
    private static final float STARTING_SIZE = 0.33f;
    private Random random;
    private Texture ringFront;
    private Texture ringBack;
    public Vector2 position;
    public RING_TYPE ringType;

    public RingObject() {
        super(filePaths, fileTypes);
        random = new Random();
        position = new Vector2();
    }

    @Override
    public boolean isLoaded() {
        boolean loaded = super.isLoaded();
        if (ringFront == null && ringBack == null && loaded)
            getRandomRing();
        return loaded;
    }

    @Override
    public boolean isColliding(Rectangle rectangle) {
        return rectangle.overlaps(new Rectangle(position.x + ringBack.getWidth(),position.y,position.x + ringBack.getWidth(),ringBack.getHeight()));
    }

    public Vector2 position() {
        return  position;
    }

    public void renderRingBack(SpriteBatch batch, SpikeQuestCamera camera) {
        if (position.x > (camera.getCameraPositionX() - camera.getCameraWidth()/2 -ringBack.getWidth())
                && position.x < (camera.getCameraPositionX() + camera.getCameraWidth()/2 + ringBack.getWidth()))
            drawTexture(ringBack, batch, position.x, position.y);
    }

    public void renderRingFront(SpriteBatch batch, SpikeQuestCamera camera) {
        if (position.x > (camera.getCameraPositionX() - camera.getCameraWidth()/2 -ringFront.getWidth())
                && position.x < (camera.getCameraPositionX() + camera.getCameraWidth()/2 + ringFront.getWidth()))
            drawTexture(ringFront, batch, position.x, position.y);
    }

    /**
     * Do not use! Use renderRingBack and renderRingFront
     * @param batch
     */
    public void render(SpriteBatch batch, SpikeQuestCamera camera) {
        System.err.println("Error: render on ring was called when renderRingBack or renderRingFront should have been called");
    }

    /**
     * I move the ring to the left by the speed passed
     * @param speed
     */
    public void update(float speed) {
        position.x -= speed;
    }

    private void getRandomRing() {
        switch (random.nextInt(3)) {
            //Red Ring
            case 0 : ringFront = (Texture) getAsset(filePaths[0], "Texture");
                ringBack = (Texture) getAsset(filePaths[1], "Texture");
                ringType = RING_TYPE.RED;
                break;
            case 1 : ringFront = (Texture) getAsset(filePaths[2], "Texture");
                ringBack = (Texture) getAsset(filePaths[3], "Texture");
                ringType = RING_TYPE.GREEN;
                break;
            case 2 : ringFront = (Texture) getAsset(filePaths[4], "Texture");
                ringBack = (Texture) getAsset(filePaths[5], "Texture");
                ringType = RING_TYPE.YELLOW;
                break;
            default: System.err.println("Error: Ring not loaded for Ring Object");
        }
    }



}
