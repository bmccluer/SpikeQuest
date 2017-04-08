package com.brendanmccluer.spikequest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;

/**
 * Created by brend on 4/7/2017.
 */

public class SpikeQuestShapeRenderer implements Disposable {
    ShapeRenderer shapeRenderer = null;

    public SpikeQuestShapeRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    public void drawRectangle(Rectangle rectangle, SpikeQuestCamera camera) {
        if (rectangle != null && camera != null) {
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.setProjectionMatrix(camera.getProjectionMatrix());
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.begin();
            shapeRenderer.box(rectangle.x, rectangle.y, 0, rectangle.width, rectangle.height, 0);
            shapeRenderer.end();
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
