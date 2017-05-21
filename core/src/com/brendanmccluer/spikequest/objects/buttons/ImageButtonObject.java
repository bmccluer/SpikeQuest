package com.brendanmccluer.spikequest.objects.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.interfaces.ButtonObjectAction;
import com.brendanmccluer.spikequest.interfaces.ButtonObjectInterface;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;

/**
 * Created by brend on 11/20/2016.
 */

public class ImageButtonObject implements ButtonObjectInterface {
    protected Texture imageTexture = null;
    protected Sprite buttonSprite = null;
    protected AbstractSpikeQuestScreen screen;
    protected ButtonObjectAction buttonAction;
    private boolean isClicked = false;

    public ImageButtonObject(Texture imageTexture, AbstractSpikeQuestScreen screen) {
        this.imageTexture = imageTexture;
        buttonSprite = new Sprite(imageTexture);
        this.screen = screen;
    }

    @Override
    public boolean isTouching(float xPos, float yPos) {
        if (buttonSprite.getBoundingRectangle().contains(xPos, yPos))
            return true;
        return false;
    }

    @Override
    public void update(float delta, SpikeQuestCamera gameCamera) {
        if (gameCamera != null) {
            if (isTouching(gameCamera.getMousePositionX(), gameCamera.getMousePositionY())) {
                buttonSprite.setAlpha(0.5f);
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    buttonAction.handle(screen);
                    isClicked = true;
                }

            }
            else
                buttonSprite.setAlpha(1);
        }
    }

    @Override
    public void draw(SpriteBatch aSpriteBatch) {
        buttonSprite.draw(aSpriteBatch);
    }

    @Override
    public void setPosition(float xPos, float yPos) {
        buttonSprite.setPosition(xPos, yPos);
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public boolean isClicked() {
        return isClicked;
    }

    @Override
    public void setButtonAction(ButtonObjectAction action) {
        buttonAction = action;
    }

    @Override
    public void discard() {
        imageTexture.dispose();
        imageTexture = null;
        buttonSprite = null;
    }
}
