package com.brendanmccluer.spikequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * Created by brend on 12/28/2017.
 */

public class SpikeQuestBox2D {
    private static final String TAG = "SpikeQuestBox2D";

    public static PolygonShape buildPolygonShape(float[] vertices) {
        return buildPolygonShape(vertices, 0);
    }

    public static PolygonShape buildPolygonShape(float[] vertices, float pixelsPerMeter) {
        PolygonShape polygon = new PolygonShape();
        float[] worldVertices = new float[vertices.length];
        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i];
            if (pixelsPerMeter >= 0)
                worldVertices[i] /= pixelsPerMeter;
        }
        Gdx.app.debug(TAG, String.format("Built Box2D polygon shape from vertices = %s\n Transformed vertices = %s", vertices, worldVertices));
        polygon.set(worldVertices);
        return polygon;
    }

    public  static PolygonShape buildRectangleShape(float rectWidth, float rectHeight) {
        return buildRectangleShape(rectWidth, rectHeight, 0);
    }

    public static PolygonShape buildRectangleShape(float rectWidth, float rectHeight, float pixelsPerMeter) {
        PolygonShape polygon = new PolygonShape();
        float width = (rectWidth * 0.5f);
        float height = (rectHeight * 0.5f);
        if (pixelsPerMeter > 0) {
            width /= pixelsPerMeter;
            height /= pixelsPerMeter;
        }
        polygon.setAsBox(width, height);
        Gdx.app.debug(TAG, String.format("Built Box2D rectangle shape from width %s and height %s. " +
                "Transformed rectangle has width %s and height %s", rectWidth, rectHeight, width, height));
        return polygon;
    }
}
