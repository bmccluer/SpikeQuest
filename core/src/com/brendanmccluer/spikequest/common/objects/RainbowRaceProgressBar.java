package com.brendanmccluer.spikequest.common.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;

/**
 * Created by brend on 9/10/2016.
 */
public class RainbowRaceProgressBar extends AbstractSpikeQuestObject{
    private static final String[] filePaths = {"object/rainbowRaceProgressBar/ProgressBarTexture.png", "object/rainbowRaceProgressBar/RainbowMarkerTexture.png", "object/rainbowRaceProgressBar/TankMarkerTexture.png"};
    private static final String[] fileTypes = {"Texture", "Texture", "Texture"};
    private static final int FINISH_FLAG_WIDTH = 20;
    private static final int TANK_OFFSET = -5; //OFFSET NEEDED TO KEEP TANK AND RAINBOW DASH ALIGNED
    private Texture progressBarTexture, rainbowMarkerTexture, tankMarkerTexture;
    public Vector2 position = new Vector2();
    private float tankPercent = 0, rainbowPercent = 0;
    public int tankPart = 0, rainbowPart = 0;
    private float partWidth = 0;

    public RainbowRaceProgressBar() {
        super(filePaths, fileTypes);
    }

    @Override
    public boolean isLoaded() {
        boolean loaded = super.isLoaded();
        if (progressBarTexture == null && loaded) {
            progressBarTexture = (Texture) getAsset(filePaths[0], fileTypes[0]);
            rainbowMarkerTexture = (Texture) getAsset(filePaths[1], fileTypes[1]);
            tankMarkerTexture = (Texture) getAsset(filePaths[2], fileTypes[2]);
            partWidth = (progressBarTexture.getWidth()/3) - FINISH_FLAG_WIDTH;
        }
        return loaded;
    }

    public void draw(SpriteBatch batch) {
        drawTexture(progressBarTexture, batch, position.x, position.y);
        drawTexture(rainbowMarkerTexture, batch, position.x - rainbowMarkerTexture.getWidth()/2 + (partWidth * rainbowPart) + rainbowPercent * partWidth, position.y + 35);
        drawTexture(tankMarkerTexture, batch, TANK_OFFSET + position.x + - tankMarkerTexture.getWidth()/2 + (partWidth * tankPart) + tankPercent * partWidth, position.y + 35);
    }

    public float getWidth() {
        return progressBarTexture.getWidth() - FINISH_FLAG_WIDTH;
    }

    public void updateTankMarker(float percent) {
       tankPercent = percent;
    }

    public void updateRainbowMarker(float percent) {
        rainbowPercent = percent;
    }

    public void dispose() {
        discard();
        progressBarTexture.dispose();
        rainbowMarkerTexture.dispose();
        tankMarkerTexture.dispose();
    }
}
