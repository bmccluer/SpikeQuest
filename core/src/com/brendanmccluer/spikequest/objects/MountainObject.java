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
public class MountainObject extends AbstractSpikeQuestObject implements RainbowRaceObject {
    private static final String[] filePaths = {"textures/MountainTextureLg.png", "textures/MountainTextureSm.png"};
    private static final String[] fileTypes = {"Texture", "Texture"};
    private static final float STARTING_SIZE = 0.33f;
    private Texture texture;
    public Vector2 position;

    public MountainObject() {
        super(filePaths, fileTypes);
        setAsset(filePaths[0], "Texture");
        setAsset(filePaths[1], "Texture");
        position = new Vector2();
        position.y = -10;
    }

    public Vector2 position() {
        return  position;
    }

    @Override
    public boolean isLoaded() {
        boolean loaded = super.isLoaded();
        if (texture == null)
            texture = (Texture) getAsset(new Random().nextBoolean() ? filePaths[0] : filePaths[1], "Texture");
        return loaded;
    }

    @Override
    public boolean isColliding(Rectangle rectangle) {
        return rectangle.overlaps(new Rectangle(position.x,position.y,texture.getWidth(),texture.getHeight()));
    }

    public void render(SpriteBatch batch, SpikeQuestCamera camera) {
        if (position.x > (camera.getCameraPositionX() - camera.getCameraWidth()/2 -texture.getWidth())
                && position.x < (camera.getCameraPositionX() + camera.getCameraWidth()/2 + texture.getWidth()))
            batch.draw(texture, position.x, position.y);
    }

    /**
     * I move the mountain to the left by the speed passed
     * @param speed
     */
    public void update(float speed) {
        position.x -= speed;
    }


}