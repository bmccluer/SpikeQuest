package com.brendanmccluer.spikequest.tiles;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    public static Rectangle getRectangleCollision(TiledMap aMap, AbstractSpikeQuestSpriteObject anObject, int anObjectLayer) {
        MapLayer objectLayer =  aMap.getLayers().get(anObjectLayer);
        MapObjects objects = objectLayer.getObjects();

        // there are several other types, Rectangle is probably the most common one
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            //if (Intersector.overlaps(rectangle, anObject.getCollisionRectangle())) {
            if (rectangle.overlaps(anObject.getCollisionRectangle())) {
                return rectangle;
            }
        }
        return null;
    }

    public static Polygon getPolygonObjectCollision(TiledMap aMap, Rectangle rectangle, int anObjectLayer) {
        MapLayer objectLayer =  aMap.getLayers().get(anObjectLayer);
        MapObjects objects = objectLayer.getObjects();

        // there are several other types, Rectangle is probably the most common one
        for (PolygonMapObject polygonMapObject : objects.getByType(PolygonMapObject.class)) {
            Polygon polygon = polygonMapObject.getPolygon();
            if (isCollision(polygon, rectangle)) {
                return polygon;
            }
        }
        return null;
    }

    /**
     * I get all rectangle objects in the layer passed to the method
     * @param aMap
     * @param anObjectLayer
     * @return
     */
    public static List<Rectangle> getTileMapRectangles(TiledMap aMap, int anObjectLayer) {
        MapLayer objectLayer =  aMap.getLayers().get(anObjectLayer);
        MapObjects objects = objectLayer.getObjects();
        List<Rectangle> locations = new ArrayList<Rectangle>();
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            locations.add(rectangle);
        }
        return locations;
    }

    /**
     * I return the number of objects in the layer
     * @param aMap
     * @param anObjectLayer
     * @return
     */
    public static int getTileMapObjectCount(TiledMap aMap, int anObjectLayer) {
        MapLayer objectLayer =  aMap.getLayers().get(anObjectLayer);
        return objectLayer.getObjects().getCount();
    }

    /**
     * I mix the maps passed into a random ordered list
     * @param maps
     * @return
     */
    public static List<TiledMap> getRandomTileMapList(TiledMap... maps) {
        List<TiledMap> tiledMapList = new ArrayList<TiledMap>();
        List<TiledMap> newTiledMapList = new ArrayList<TiledMap>();
        Random random = new Random();
        int numMaps = maps.length;
        int index = 0;

        for (TiledMap map : maps)
            tiledMapList.add(map);

        for (int i=0; i < numMaps; i++) {
            index = random.nextInt(tiledMapList.size());
            newTiledMapList.add(tiledMapList.remove(index));
        }

        return  newTiledMapList;
    }


    // Check if Polygon intersects Rectangle
    //from http://stackoverflow.com/questions/28522313/java-libgdx-how-to-check-polygon-collision-with-rectangle-or-circle
    // Check if Polygon intersects Rectangle
    private static boolean isCollision(Polygon p, Rectangle r) {
        Polygon rPoly = new Polygon(new float[] { 0, 0, r.width, 0, r.width,
                r.height, 0, r.height });
        rPoly.setPosition(r.x, r.y);
        if (Intersector.overlapConvexPolygons(rPoly, p))
            return true;
        return false;
    }

}
