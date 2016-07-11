package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by brend on 6/28/2016.
 */
public class MountainObject extends AbstractSpikeQuestObject {
    private static final String[] filePaths = {"textures/MountainTextureLg.png", "textures/MountainTextureSm.png"};
    private static final String[] fileTypes = {"Texture", "Texture"};
    private static final float STARTING_SIZE = 1;
    private Texture texture;
    public Vector2 position;

    public MountainObject() {
        super(filePaths, fileTypes);
        position = new Vector2();
        position.y = -10;
    }

    @Override
    public boolean isLoaded() {
        boolean loaded = super.isLoaded();
        if (texture != null)
            texture = (Texture) getAsset(new Random().nextBoolean() ? filePaths[0] : filePaths[1], "Texture");
        return loaded;
    }

    public void draw(SpriteBatch batch) {
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
