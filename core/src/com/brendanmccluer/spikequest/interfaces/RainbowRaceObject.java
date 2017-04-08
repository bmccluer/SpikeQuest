package com.brendanmccluer.spikequest.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;
import com.brendanmccluer.spikequest.objects.SpikeQuestSprite;

/**
 * Created by brend on 7/10/2016.
 * contains standard methods rainbow race objects should have
 */
public interface RainbowRaceObject extends Disposable {
    /**
     * WARNING: USE setPosition to change
     * @return
     */
    public Vector2 position();

    public void setPosition(float xPos, float yPos);

    public void update(float speed);

    /**
     * I render if set in the camera location
     * @param batch
     * @param camera
     */
    public void render(SpriteBatch batch, SpikeQuestCamera camera);

    public boolean isLoaded();

    public boolean isColliding(Rectangle rectangle);

}
