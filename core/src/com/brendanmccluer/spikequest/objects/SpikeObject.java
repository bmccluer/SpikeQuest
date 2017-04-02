package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;

public class SpikeObject extends StandardObject {
	public final int SPIKE_STANDARD_SPEED = 10;
	
	private static final String[] assetPaths = {"spike/stand/SpikeStand.atlas", "spike/walk/SpikeWalk.atlas", "spike/talk/SpikeTalk.atlas", "spike/fire/SpikeFire.atlas", "sounds/fireloop.wav"}; 
	private static final String[] assetTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "TextureAtlas", "Sound"};
	private static final int[] maxFrames = {6,4,13};
	private static final String[] soundPaths = {"spike/talk/EasyPeasy.wav"};
	private static final int[] soundLengths = {10};
	private static final String[] soundNames = {"easyPeasy"};
	private static final float TOP_OF_HEAD_HEIGHT = 50f;
	private static final float STARTING_SIZE = 0.33f;
	//private static final float TOP_OF_HEAD_WIDTH = 5f;
	private static final float FIRE_WIDTH = 50f;
	private final int SPIKE_FIRE_MAX_FRAMES = 3;
	private final String SPIKE_FIRE_ANIMATION_INDICATOR = "FIRE";
	private TextureAtlas spikeFireAtlas = null;
	private SpikeQuestSoundEffect spikeFireSound = null;
	private boolean breathingFire = false;
	//private SpikeQuestTextBalloon spikeTextBalloon = null;
	//private boolean spikeTalk = false;
	
	public SpikeObject () {
		super(assetPaths, assetTypes, maxFrames, STARTING_SIZE, true);
		setSoundEffects(soundPaths, soundLengths, soundNames);
	}
	
	/*
	@Override
	public boolean isLoaded () {
		return super.isLoaded() && spikeTextBalloon.isLoaded();
	}/
	/**
	 * draw Spike Sprite at World position x and World position y
	 * @param batch
	 * @param xPos
	 * @param yPos
	 */
	@Override
	public void spawn (float xPos, float yPos) {
		
			spikeFireAtlas = (TextureAtlas)getAsset(assetPaths[3], assetTypes[3]);
			breathingFire = false;
			
			super.spawn(xPos, yPos);
	}
	
	@Override
	public boolean isLoaded() {
		boolean isLoaded = super.isLoaded();
		
		if (spikeFireSound == null && isLoaded)
			spikeFireSound = new SpikeQuestSoundEffect ((Sound)getAsset(assetPaths[4], assetTypes[4]), 20);
		
		return isLoaded;
	}
		
	/**
	 * I move spike to the right at a defined speed
	 * @param speed
	 * 
	 */
	public void moveRight (int moveSpeed) {
		//stop fire sound
		spikeFireSound.stopSound();
		breathingFire = false;
		
		super.moveRight(moveSpeed);
	}
	
	
	/**
	 * I move spike to the left at a defined speed
	 * 
	 */
	public void moveLeft (int moveSpeed) {
		//stop fire sound
		spikeFireSound.stopSound();
		breathingFire = false;
		
		super.moveLeft(moveSpeed);
	}
	
	
	/**
	 * I return if the object is facing to the right
	 */
	public boolean getIsFacingRight () {
		return objectFacingRight;
	}
	
	
	/**
	 * I return a rectangle for the top of Spike's head. Use for colliding him with
	 * objects from above.
	 * @return
	 */
	public Rectangle getCollisionHeadRectangle () {
		//only want to return a copy
		Rectangle spriteRectangle = currentSprite.getSpriteBoundingRectangle();
		
		//left bottom corner of area
		spriteRectangle.setY(spriteRectangle.getHeight() - TOP_OF_HEAD_HEIGHT);
		//spriteRectangle.setWidth(width)
		
		return spriteRectangle; 
	}
	
	/**
	 * 
	 */
	@Override
	public Rectangle getCollisionRectangle () {
		Rectangle spriteRectangle = currentSprite.getSpriteBoundingRectangle();
		
		//don't include fire in collision zone
		if (breathingFire) {
			if (objectFacingRight) {
				//set collision zone right
				spriteRectangle.setWidth(spriteRectangle.getWidth() - FIRE_WIDTH);
			}
			
			spriteRectangle.setX(spriteRectangle.getX() + FIRE_WIDTH);
			
			
		}
		
		return spriteRectangle;
	}
	
	/**
	 * I move spike to the right at a defined speed
	 * @param speed
	 * 
	 */
	@Override
	public void moveRight (float moveSpeed) {
		//stop fire sound
		spikeFireSound.stopSound();
		breathingFire = false;
		
		super.moveRight(moveSpeed);
	}
	
	
	/**
	 * I move spike to the left at a defined speed
	 * 
	 */
	@Override
	public void moveLeft (float moveSpeed) {
		//stop fire sound
		spikeFireSound.stopSound();
		breathingFire = false;
		
		super.moveLeft(moveSpeed);
	}
	
	/**
	 * I breathe fire and return a rectangle indicating the 
	 * collision zone of the fire
	 * @return
	 */
	public void breatheFire () {
		
		if (!adjustToGravity()) {
			breathingFire = true;
			
			//change animation if not set
			if (changeAnimation(SPIKE_FIRE_ANIMATION_INDICATOR, SPIKE_FIRE_MAX_FRAMES, spikeFireAtlas) && !objectFacingRight) {
				//offset to match where spike is standing if he is facing left
				currentSprite.setPosition(currentPositionX-80, currentPositionY);
				
			}
			else if (objectFacingRight) {
				currentSprite.setPosition(currentPositionX, currentPositionY);
			}
			
			spikeFireSound.playSound(false);
		}
		
	}
	
	
	/**
	 * I set the collision zone for fire
	 * @return
	 */
	public Rectangle getFireCollisionZone() {
		Rectangle spriteRectangle = currentSprite.getSpriteBoundingRectangle();
		
		if (breathingFire) {
			if (objectFacingRight) {
				//set collision zone right
				spriteRectangle.setX((spriteRectangle.getX() + spriteRectangle.getWidth()) - FIRE_WIDTH);
			}
			
			spriteRectangle.setWidth(FIRE_WIDTH);
			
		}
		else {
			spriteRectangle.set(0, 0, 0, 0);
		}
		
		return spriteRectangle;
	}
		

	@Override
	public void standStill () {
		//stop fire sound
		spikeFireSound.stopSound();
		breathingFire = false;
		
		super.standStill();
	}
	
	/**
	 * I make spike jump and return true
	 * when done jumping. You must stop calling jump when
	 * true is returned or spike will jump again in the next
	 * call.
	 * 
	 * @return
	 */
	@Override
	public boolean jump () {
		spikeFireSound.stopSound();
		breathingFire = false;
		
		return super.jump();
	}
	
	@Override
	public void discard () {
		//spikeTextBalloon.discard();
		spikeFireSound = null;
		super.discard();
	}
	
	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);
	}
	
}
