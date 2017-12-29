package com.brendanmccluer.spikequest.tiles;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import com.brendanmccluer.spikequest.SpikeQuestShapeRenderer;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

    public static List<Polygon> getPolygons(TiledMap tiledMap, int objectLayerNum) {
        MapLayer objectLayer =  tiledMap.getLayers().get(objectLayerNum);
        MapObjects objects = objectLayer.getObjects();
        List<Polygon> polygonList = new ArrayList<Polygon>();
        for (PolygonMapObject polygonMapObject : objects.getByType(PolygonMapObject.class)) {
            polygonList.add(polygonMapObject.getPolygon());
        }
        return polygonList;
    }

    public static void drawPolygons(TiledMap aMap, int anObjectLayer, SpikeQuestCamera gameCamera) {
        SpikeQuestShapeRenderer renderer = new SpikeQuestShapeRenderer();
        MapLayer objectLayer =  aMap.getLayers().get(anObjectLayer);
        MapObjects objects = objectLayer.getObjects();
        for (PolygonMapObject polygonMapObject : objects.getByType(PolygonMapObject.class)) {
            Polygon polygon = polygonMapObject.getPolygon();
            renderer.drawPolygon(polygon, gameCamera);
        }
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
     * I get all rectangle objects in the layer passed to the method
     * and sort them from x position least to greatest
     *
     * @param aMap
     * @param anObjectLayer
     * @return
     *
     */
    public static List<Rectangle> getTileMapRectanglesSortedXAscending(TiledMap aMap, int anObjectLayer) {
        List<Rectangle> rectangleList = getTileMapRectangles(aMap, anObjectLayer);
        Rectangle[] locations = new Rectangle[rectangleList.size()];
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (((Rectangle)o1).getX() > ((Rectangle)o2).getX())
                    return 1;
                return -1;
            }
        };
        //sort method must have array type and not list type
        rectangleList.toArray(locations);
        Sort.instance().sort(locations,comparator);
        return Arrays.asList(locations);
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
