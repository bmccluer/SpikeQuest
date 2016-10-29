package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

/**
 * Create a spikeQuest sprite with
 * a spritesheet.atlas file for animation. Create TextureAtlas("filepath.atlas")
 * and pass to SpikeQuestSprite along with total number of frames
 * @author Brendan
 *
 */
public class SpikeQuestSprite {
	private Sprite sprite = null;
	private TextureAtlas currentTextureAtlas = null;
	private int currentFrame = 1;
	private int maxFrames = 1;
	private int speed = 10; //default change animation every 1000 steps
	private int speedIndex = 0;
	private final String UNABLE_TO_LOAD_TEXTURE_ATLAS_MESSAGE = "Could not find initial region in TextureAtlas provided ";
	private Exception textureAtlasException = null;
	private boolean isFlipped = false;
	private float currentSize = 1;
    private boolean isPaused = false;
	
	public SpikeQuestSprite(TextureAtlas textureAtlas, int frames, float size) throws Exception {
		sprite = new Sprite();
		
		currentTextureAtlas = textureAtlas;
		maxFrames = frames;
		setSpriteRegion(1);
		sprite.setPosition(0, 0);
		
		currentSize = size;
		setSize(currentSize);
		setOriginCenter();
	}
	
	/*
	public SpikeQuestSprite(TextureAtlas textureAtlas, int frames, float size, boolean isAnimated) throws Exception {
		sprite = new Sprite();
		
		currentTextureAtlas = textureAtlas;
		sprite.setOriginCenter();
		maxFrames = frames;
		setSpriteRegion(1);
		sprite.setPosition(0, 0);
		currentSize = size;
		setSize(currentSize);
	}*/
	
	public Rectangle getSpriteBoundingRectangle () {
		return sprite.getBoundingRectangle();
	}
	
	public void setPosition(float x,float y) {
		sprite.setPosition(x, y);
	}
	
	public void rotate (float degrees) {
		
		if (sprite.getRotation() > 360) {
			sprite.setRotation(sprite.getRotation() - 360);
		}
		sprite.rotate(degrees);
		setOriginCenter();
		
	}
	
	public void setRotation (float degrees) {
		sprite.setRotation(degrees);
	}
	
	public float getRotation () {
		return sprite.getRotation();
	}
	
	/**
	 * I reverse the direction
	 */
	public void reverseDirection () {
		
		sprite.flip(true, false);
		
		if (isFlipped) {
			isFlipped = false;
		}
		else {
			isFlipped = true;
		}
	}
	
	
	/**
	 * I change the spritesheet for a different
	 * animation
	 * @throws Exception 
	 */
	public void setCurrentTextureAtlas (TextureAtlas textureAtlas, int frames) throws Exception {
		currentTextureAtlas = textureAtlas;
		maxFrames = frames;
		
		//set first frame
		setSpriteRegion(1);
		
		//check if needed to be flipped
		if (isFlipped) {
			sprite.flip(true, false);
		}
	}
	
	public float getOriginX () {
		return sprite.getOriginX();
	}
	
	public float getOriginY () {
		return sprite.getOriginY();
	}
	
	public void setOriginCenter () {
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
	}
	
	public void setBoundsToRectangle (Rectangle aRectangle){
		sprite.setBounds(aRectangle.getX(), aRectangle.getY(), aRectangle.getWidth(), aRectangle.getHeight());
	}
	public float getPositionX () {
		return sprite.getX();
	}
	
	public float getPositionY () {
		return sprite.getY();
	}
	
	/**
	 * I draw the sprite on it's current
	 * frame at the current speed
	 * @param batch
	 */
	public void drawSprite (SpriteBatch batch) throws Exception {
		speedIndex++;
		
		//Update animation depending on speed
		if (speedIndex >= speed) {
			
			speedIndex = 0;
			
			if (currentFrame > maxFrames) {
				currentFrame = 1;
			}
			
			setSpriteRegion(currentFrame);
			if (!isPaused)
                currentFrame++;
			
			//adjust size to match new region and set origin
			setSize(currentSize);
			setOriginCenter();
			
			
			//check if needed to be flipped
			if (isFlipped) {
				sprite.flip(true, false);
			}
			
		}
		
		
		
		//draw animation
		sprite.draw(batch);
	}

	public boolean isPaused() {
		return isPaused;
	}
	
	
	public int getFrame () {
		return currentFrame;
	}
	
	/**
	 * I change the size of the sprite by multiplying the ratio passed 
	 * @param size
	 */
	public void setSize (float size) {
		float height = sprite.getRegionHeight()*size;
		float width = sprite.getRegionWidth()*size;

		currentSize = size;
		
		sprite.setBounds(sprite.getX(), sprite.getY(), width, height);
	}

	/**
	 * I take a frame and set the region for the sprite to match that frame. 
	 * Throws exception
	 * if the region does not exist
	 * @throws Exception
	 */
	public void setSpriteRegion (int frame) throws Exception {
		try{
			//Regions are anywhere from "0001"-"9999". Format the frame to have leading zeros
			sprite.setRegion(currentTextureAtlas.findRegion(String.format("%04d", frame)));
			
		}
		catch (Exception e) {
			textureAtlasException = new Exception(UNABLE_TO_LOAD_TEXTURE_ATLAS_MESSAGE + String.format("%04d", frame));
			throw textureAtlasException;
		}
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

    public void pauseAnimation() {
        isPaused = true;
    }

    public void playAnimation() {
        isPaused = false;
    }

	
}
