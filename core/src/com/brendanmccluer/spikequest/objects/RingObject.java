package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by brend on 6/28/2016.
 */
public class RingObject extends AbstractSpikeQuestObject {
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

    public void renderRingBack(SpriteBatch batch) {
        drawTexture(ringBack, batch, position.x, position.y);
    }

    public void renderRingFront(SpriteBatch batch) {
        drawTexture(ringFront, batch, position.x, position.y);
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
                break;
            case 1 : ringFront = (Texture) getAsset(filePaths[2], "Texture");
                ringBack = (Texture) getAsset(filePaths[3], "Texture");
                break;
            case 2 : ringFront = (Texture) getAsset(filePaths[4], "Texture");
                ringBack = (Texture) getAsset(filePaths[5], "Texture");
                break;
            default: System.err.println("Error: Ring not loaded for Ring Object");
        }
    }



}
