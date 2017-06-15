package com.brendanmccluer.spikequest;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;

/**
 * Created by brend on 6/11/2017.
 */

public class SpikeQuestFadingEffect implements Disposable {
    private float fadingAlpha = 0;
    private float fadeIncrement = 0;
    private SpikeQuestShapeRenderer shapeRenderer = null;
    private boolean startFading = false;
    public SpikeQuestCamera camera = null;


    public SpikeQuestFadingEffect(SpikeQuestCamera camera) {
        this.camera = camera;
    }

    /**
     * Will fade to black. If already faded, will undo fade. The more levels passed, the longer it takes to fade
     * @param fadeLevels
     */
    public void setFade(int fadeLevels) {
        int fadeDirection = 1;
        if(fadingAlpha >= 1)
            fadeDirection = -1;
        fadeIncrement = ((float)1/fadeLevels) * fadeDirection;
        startFading = true;
    }

    public void draw(SpriteBatch batch) {
        if(startFading) {
            updateFading();
        }
        if(fadingAlpha > 0) {
            getShapeRenderer().drawTransparentRectangle(
                    new Rectangle(camera.getCameraPositionX() - camera.getCameraWidth()/2, camera.getCameraPositionY() - camera.getCameraHeight()/2, camera.getCameraWidth(), camera.getCameraHeight()), camera, fadingAlpha);
        }
    }

    private void updateFading() {
        fadingAlpha += fadeIncrement;
        if(fadingAlpha >= 1) {
            fadingAlpha = 1;
            startFading = false;
            return;
        }
        else if(fadingAlpha <= 0) {
            fadingAlpha = 0;
            startFading = false;
            return;
        }
    }

    private SpikeQuestShapeRenderer getShapeRenderer() {
        if(shapeRenderer == null)
            shapeRenderer = new SpikeQuestShapeRenderer();
        return shapeRenderer;
    }

    @Override
    public void dispose() {
        if(shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
