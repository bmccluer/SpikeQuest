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
    private int fadingDirection = 1;
    private long msToFade = 1;
    private long startTime = 0;
    private SpikeQuestShapeRenderer shapeRenderer = null;
    private boolean startFading = false;
    public SpikeQuestCamera camera = null;


    public SpikeQuestFadingEffect(SpikeQuestCamera camera) {
        this.camera = camera;
    }

    /**
     * Will fade to black. If already faded, will undo fade. The more levels passed, the longer it takes to fade
     * @param seconds
     */
    public void setFade(int seconds) {
        msToFade = seconds * 1000;
        startTime = System.currentTimeMillis();
        if(fadingAlpha >= 1)
            fadingDirection = -1;
        else
            fadingDirection = 1;
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
        long currentTime = System.currentTimeMillis();
        float time = (float)(currentTime - startTime);
        float fadeRatio = ((time)/msToFade);
        System.out.println(fadeRatio);
        //return if time is up
        if(fadeRatio >= 1) {
            if(fadingDirection == 1)
                fadingAlpha = 1;
            else
                fadingAlpha = 0;
            startFading = false;
            return;
        }
        //set fading alpha for reverse fading
        if(fadingDirection == -1) {
            fadingAlpha = 1 - fadeRatio;
            return;
        }
        fadingAlpha = fadeRatio;
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
