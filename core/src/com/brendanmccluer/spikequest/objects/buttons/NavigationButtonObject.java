package com.brendanmccluer.spikequest.objects.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.interfaces.ButtonObjectAction;
import com.brendanmccluer.spikequest.interfaces.ButtonObjectInterface;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;

/**
 * Created by brend on 11/20/2016.
 */

public class NavigationButtonObject implements ButtonObjectInterface {
    protected Texture imageTexture = null;
    protected Sprite buttonSprite = null;
    protected AbstractSpikeQuestScreen screen;
    protected ButtonObjectAction buttonAction;
    private int gdxInputInt = -1;
    private String text = null;
    private boolean isClicked, showText;
    private boolean drawWhenNotTouched = false;
    private AbstractSpikeQuestSpriteObject object;
    private float animateVelocity = -100;
    private int hoverBounds = 50;
    private Vector2 originalPos = Vector2.Zero;
    private BitmapFont font;


    public NavigationButtonObject(Direction direction, AbstractSpikeQuestScreen screen, String text) {
        imageTexture = new Texture("buttons/navigationButtons/Arrow.png");
        font = new BitmapFont();
        if (imageTexture != null)
            buttonSprite = new Sprite(imageTexture);
        rotateArrow(direction);
        if (text != null)
            this.text = text;
        this.screen = screen;
    }

    /**
     * Use object instead of mouse
     * @param object
     */
    public void setObjectDetection(AbstractSpikeQuestSpriteObject object) {
        this.object = object;
    }

    public void setDrawWhenNotTouched(boolean drawWhenNotTouched) {
        this.drawWhenNotTouched = drawWhenNotTouched;
    }

    public void setGdxInputInt(int gdxInputInt) {
        this.gdxInputInt = gdxInputInt;
    }

    private void rotateArrow(Direction direction) {
        switch (direction) {
            case UP: return;
            case DOWN: buttonSprite.rotate(180);
            case LEFT: buttonSprite.rotate(270);
            case RIGHT: buttonSprite.rotate(90);
        }
    }

    @Override
    public boolean isTouching(float xPos, float yPos) {
        if (buttonSprite.getBoundingRectangle().contains(xPos, yPos))
            return true;
        return false;
    }

    public boolean isTouching(AbstractSpikeQuestSpriteObject spriteObject) {
        Rectangle rectangle = buttonSprite.getBoundingRectangle();
        float top = rectangle.getY() + rectangle.getHeight();
        rectangle.setY(0); //set so object collision works below the object
        rectangle.setHeight(top);
        return rectangle.overlaps(spriteObject.getCollisionRectangle());
    }

    @Override
    public void update(float delta, SpikeQuestCamera gameCamera) {
        boolean isTouching = false;
        if (gdxInputInt == -1)
            gdxInputInt = Input.Buttons.LEFT;
        if (gameCamera != null) {
            if (object != null)
                isTouching = isTouching(object);
            else
                isTouching = isTouching(gameCamera.getMousePositionX(), gameCamera.getMousePositionY());
            if (isTouching) {
                buttonSprite.setPosition(buttonSprite.getX(), buttonSprite.getY() + (delta * animateVelocity));
                if (buttonSprite.getY() >= originalPos.y + hoverBounds || buttonSprite.getY() < -hoverBounds + originalPos.y) {
                    animateVelocity *= -1;
                    buttonSprite.setPosition(buttonSprite.getX(), buttonSprite.getY() + (delta * animateVelocity));
                }

                if (Gdx.input.isButtonPressed(gdxInputInt) || Gdx.input.isKeyPressed(gdxInputInt)) {
                    buttonAction.handle(screen);
                    isClicked = true;
                }
            }
            else {
                buttonSprite.setPosition(originalPos.x, originalPos.y);
                animateVelocity = Math.abs(animateVelocity);
            }

        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (drawWhenNotTouched || isTouching(object)) {
            buttonSprite.draw(batch);
            if (!(text == null || text.isEmpty()))
                font.draw(batch, text, originalPos.x + buttonSprite.getWidth()/2, originalPos.y + 60);
        }
    }

    @Override
    public void setPosition(float xPos, float yPos) {
        buttonSprite.setPosition(xPos, yPos);
        originalPos = new Vector2(xPos, yPos);
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
        font.dispose();
    }
}
