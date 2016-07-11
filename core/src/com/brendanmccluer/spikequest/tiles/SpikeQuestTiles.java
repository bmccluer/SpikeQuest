package com.brendanmccluer.spikequest.tiles;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;

/**
 * Created by brend on 7/7/2016.
 */
public class SpikeQuestTiles {

    /**
     * I return a rectangle that is colliding with the object (null if no collisions)
     * From http://stackoverflow.com/questions/20063281/libgdx-collision-detection-with-tiledmap
     * @param aMap
     * @param anObject
     * @param anObjectLayer
     * @return
     */
    public static Rectangle getCollisionsFromTileMap (TiledMap aMap, AbstractSpikeQuestSpriteObject anObject, int anObjectLayer) {
        TiledMapTileLayer collisionObjectLayer = (TiledMapTileLayer) aMap.getLayers().get(anObjectLayer);
        MapObjects objects = collisionObjectLayer.getObjects();

        // there are several other types, Rectangle is probably the most common one
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, anObject.getCollisionRectangle())) {
                return rectangle;
            }
        }
        return null;
    }


}
