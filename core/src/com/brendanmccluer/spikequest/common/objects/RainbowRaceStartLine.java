package com.brendanmccluer.spikequest.common.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;

import org.w3c.dom.css.Rect;

/**
 * Created by brend on 10/1/2016.
 */

public class RainbowRaceStartLine extends AbstractSpikeQuestObject {
    private static final String[] filePaths = {"object/rainbowRaceStartLine/StartLineBack.png", "object/rainbowRaceStartLine/StartLineFront.png"};
    private static final String[] fileTypes = {"Texture", "Texture"};
    private Texture startLineFront, startLineBack;
    public Vector2 position = Vector2.Zero;

    public RainbowRaceStartLine() {
        super(filePaths, fileTypes);
    }

    @Override
    public boolean isLoaded() {
        boolean loaded = super.isLoaded();

        if (startLineBack == null && loaded) {
            startLineBack = (Texture) getAsset(filePaths[0], "Texture");
            startLineFront = (Texture) getAsset(filePaths[1], "Texture");
        }

        return loaded;
    }

    public Rectangle getCollisionRectangle() {
        return  new Rectangle(position.x, position.y, startLineBack.getWidth(), startLineFront.getHeight());
    }

    public void drawBack(SpriteBatch batch) {
        batch.draw(startLineBack, position.x, position.y);
    }

    public void drawFront(SpriteBatch batch) {
        batch.draw(startLineFront, position.x, position.y);
    }
}
