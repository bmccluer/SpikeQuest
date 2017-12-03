package com.brendanmccluer.spikequest.cameras;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class SpikeQuestCamera  {
	public OrthographicCamera camera = null;
	private float cameraSize = 30; //default
	private int worldWidth = 0;
	private int worldHeight = 0;
	
	/**
	 * pass the size of the camera, the height of the world, and 
	 * the width of the world
	 * @param size
	 * @param height
	 * @param width
	 */
	public SpikeQuestCamera(float size, int width, int height) {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		worldWidth = width;
		worldHeight = height;
		cameraSize = size;
		camera =  new OrthographicCamera(size, size*(h/w));

		
		//set camera looking at middle of viewport
		setPositionBottomLeft();
	}
	
	/**
	 * Set postion of the camera to the bottom left of the 
	 * @param cam
	 */
	public void setPositionBottomLeft () {
		camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0);
	}
	
	/**
	 *  
	 * @param positionX
	 * @param positionY
	 */
	public void setPosition (float positionX, float positionY) {
		camera.position.x = positionX;
		camera.position.y = positionY;
	}
	
	
	/**
	 * I resize the camera
	 * @param width
	 * @param height
	 */
	public void resize(float width, float height) {
		camera.viewportWidth = cameraSize;
		camera.viewportHeight = cameraSize * (height/width);
		camera.update();
	}
	
	/**
	 * I set the size of the camera
	 * @param size
	 */
	public void setCameraSize(float size) {
		cameraSize = size;
	}
	
	public float getCameraSize() {
		return cameraSize;
	}

	/** I set the projection matrix for the batch to
	 * match the camera
	 * @param batch
	 */
	public void attachToBatch(SpriteBatch batch) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}

	public Matrix4 getProjectionMatrix() {
		camera.update();
		return camera.combined;
	}


	/**
	 * I set the mapRenderer to the camera
	 * @param mapRenderer
     */
	public void attachToTileMapRenderer(TiledMapRenderer mapRenderer) {
		camera.update();
		mapRenderer.setView(camera);
	}
	
	/**
	 * I zoom the camera in (only to certain point)
	 */
	public void zoomCameraIn () {
		camera.zoom += 0.02;
		adjustBounds();
		camera.update();
	}
	
	/**
	 * I zoom the camera out (only to certain point)
	 */
	public void zoomCameraOut () {
		camera.zoom -= 0.02;
		adjustBounds();
		camera.update();
	}
	
	/**
	 * I move the camera
	 */
	public void translateCamera (float xPos, float yPos) {
		camera.translate(xPos, yPos);
		adjustBounds();
		camera.update();
	}
	
	/**
	 * I return the middle of the camera position
	 * @return
	 */
	public float getCameraPositionX() {
		return camera.position.x;
	}
	
	/**
	 * I return the middle of the camera position
	 * @return
	 */
	public float getCameraPositionY() {
		return camera.position.y;
	}
	
	public float getCameraWidth() {
		return camera.viewportWidth;
	}
	
	public float getCameraHeight() {
		return camera.viewportHeight;
	}
	
	public int getWorldHeight() {
		return worldHeight;
	}

	public void setWorldHeight(int worldHeight) {
		this.worldHeight = worldHeight;
	}

	public int getWorldWidth() {
		return worldWidth;
	}

	/**
	 * I return the area the mouse is located adjusted to the camera pixels
	 * @param mousePositionX
	 * @return
	 */
	public float getMousePositionX () {
		
		return (camera.viewportWidth/Gdx.graphics.getWidth())*Gdx.input.getX();
		
	}
	
	/**
	 * I return the area the mouse is located adjusted to the camera pixels
	 * @param mousePositionX
	 * @return
	 */
	public float getMousePositionY () {
		
		//scale, multiple -1 (because Gdx graphics are inverted), and add viewport height
		return (camera.viewportHeight/Gdx.graphics.getHeight())*Gdx.input.getY()*-1 + camera.viewportHeight;
		
	}
	
	

	/** I make sure the camera stays in bounds of the world and
	 * does not zoom too far in or out.
	 * see https://github.com/libgdx/libgdx/wiki/Orthographic-camera
	 */
	private void adjustBounds() {
		 float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
	     float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, worldWidth/camera.viewportWidth);
        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, worldWidth - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, worldHeight - effectiveViewportHeight / 2f);
	}
	
	/**
	 * set new bounds in pixels for a different map (camera won't go outside of these bounds).
	 * @param width
	 * @param height
	 */
	public void setNewBounds (int width, int height) {
		worldWidth = width;
		worldHeight = height;
	}
	
	public void printMousePos() {
		System.out.println("X: " + getMousePositionX() + " Y: " + getMousePositionY());
	}
	
	public void discard() {
		camera = null;
	}
		
}
