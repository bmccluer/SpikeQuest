package com.brendanmccluer.spikequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
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

    public void drawRectangle(Rectangle rectangle, Camera camera) {
        if (rectangle != null && camera != null) {
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.begin();
            shapeRenderer.box(rectangle.x, rectangle.y, 0, rectangle.width, rectangle.height, 0);
            shapeRenderer.end();
        }
    }

    public void drawRectangle(Rectangle rectangle, SpikeQuestCamera camera) {
        drawRectangle(rectangle, camera.camera);
    }

    public void drawTransparentRectangle(Rectangle rectangle, SpikeQuestCamera camera, float alpha) {
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(camera.getProjectionMatrix());
        shapeRenderer.setColor(new Color(0, 0, 0, alpha));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.box(rectangle.x, rectangle.y, 0, rectangle.width, rectangle.height, 0);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);
    }

    public void drawPolygon(Polygon polygon, SpikeQuestCamera camera) {
        if (polygon != null && camera != null) {
            shapeRenderer.setProjectionMatrix(camera.getProjectionMatrix());
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.polygon(polygon.getTransformedVertices());
            shapeRenderer.end();
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
