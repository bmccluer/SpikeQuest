package com.brendanmccluer.spikequest.common.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;
import com.brendanmccluer.spikequest.objects.SpikeQuestSprite;

/**
 * Created by brend on 9/25/2016.
 */

public class RainbowRaceFinishLine extends AbstractSpikeQuestSpriteObject {
    private static final String[] filePaths = {"object/rainbowRaceFinishLine/RibbonFront.atlas", "object/rainbowRaceFinishLine/FinishLineBack.png", "object/rainbowRaceFinishLine/FinishLineFront.png",
            "object/rainbowRaceFinishLine/RibbonBack.atlas"};
    private static final String[] fileTypes = {"TextureAtlas", "Texture", "Texture", "TextureAtlas"};
    private static final int STARTING_SIZE = 1;
    private static final int RIBBON_OFFSET_X = 72;
    private static final int RIBBON_OFFSET_Y = 60;
    private Texture finishBackTexture, finishFrontTexture;
    private TextureAtlas ribbonFrontAtlas, ribbonBackAtlas;
    private SpikeQuestSprite ribbonBackSprite;

    public RainbowRaceFinishLine() {
        super(filePaths, fileTypes);
    }

    @Override
    public void resetSize() {
        currentSize = STARTING_SIZE;
    }

    @Override
    public boolean isLoaded() {
        boolean loaded = super.isLoaded();

        if (finishBackTexture == null && loaded) {
            ribbonBackAtlas = (TextureAtlas) getAsset(filePaths[3], "TextureAtlas");
            finishBackTexture = (Texture) getAsset(filePaths[1], "Texture");
            finishFrontTexture = (Texture) getAsset(filePaths[2], "Texture");
            ribbonFrontAtlas = (TextureAtlas) getAsset(filePaths[0], "TextureAtlas");
            try {
                spawn(0, 0, ribbonFrontAtlas, "Break", 5);
                ribbonBackSprite = new SpikeQuestSprite(ribbonBackAtlas, 5, 1);
                ribbonBackSprite.pauseAnimation();
                ribbonBackSprite.setSpeed(5);
                currentSprite.pauseAnimation();
                currentSprite.setSpeed(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return loaded;
    }

    public void breakRibbon() {
        currentSprite.playAnimation();
        ribbonBackSprite.playAnimation();
    }

    @Override
    public void setCurrentPositionXY(float currentPositionX, float currentPositionY) {
        super.setCurrentPositionXY(currentPositionX, currentPositionY);
        currentSprite.setPosition(currentPositionX + RIBBON_OFFSET_X, currentPositionY + RIBBON_OFFSET_Y);
        ribbonBackSprite.setPosition(currentPositionX + RIBBON_OFFSET_X, currentPositionY + RIBBON_OFFSET_Y);
    }

    @Override
    public void setCurrentPositionX(float currentPositionX) {
        super.setCurrentPositionX(currentPositionX);
        currentSprite.setPosition(currentPositionX + RIBBON_OFFSET_X, currentPositionY);
        ribbonBackSprite.setPosition(currentPositionX + RIBBON_OFFSET_X, currentPositionY);
    }

    @Override
    public void setCurrentPositionY(float currentPositionY) {
        super.setCurrentPositionY(currentPositionY);
        currentSprite.setPosition(currentPositionX, currentPositionY + RIBBON_OFFSET_Y);
        ribbonBackSprite.setPosition(currentPositionX, currentPositionY + RIBBON_OFFSET_Y);
    }

    public void drawBack (SpriteBatch batch) {
        if (!currentSprite.isPaused()) {
            if (currentSprite.getFrame() == 5) {
                currentSprite.pauseAnimation();
                ribbonBackSprite.pauseAnimation();
            }
        }
        drawSprite(currentSprite, batch);
        drawSprite(ribbonBackSprite, batch);
        drawTexture(finishBackTexture, batch, getCurrentPositionX(), getCurrentPositionY());
    }

    @Override
    public Rectangle getCollisionRectangle() {
        return new Rectangle(currentPositionX, currentPositionY, finishFrontTexture.getWidth(), finishFrontTexture.getHeight());
    }

    @Override
    public float getCenterX() {
        return currentPositionX + finishFrontTexture.getWidth()/2;
    }

    @Override
    public float getCenterY() {
        return currentPositionY + finishFrontTexture.getHeight()/2;
    }

    public void drawFront(SpriteBatch batch) {
        drawTexture(finishFrontTexture, batch, getCurrentPositionX(), getCurrentPositionY());
    }

}
