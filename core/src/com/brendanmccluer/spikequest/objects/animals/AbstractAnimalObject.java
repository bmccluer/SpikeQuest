package com.brendanmccluer.spikequest.objects.animals;

import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brendanmccluer.spikequest.common.objects.TimerObject;
import com.brendanmccluer.spikequest.objects.AbstractPopUpObject;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;

public abstract class AbstractAnimalObject extends AbstractPopUpObject {
	protected TimerObject timerObject = null;
	protected boolean isRandomlyMoving = false;
	protected boolean isRandomlyMovingRight = false;
	protected boolean isMovingTowardsPoint = false;
	protected float randomMoveDirection = 0;
	protected float movePointX = 0;
	protected float movePointY = 0;
	protected float movePointSpeed = 0;
	protected SpikeQuestSoundEffect animalSound = null;
	
	
	/**
	 * Pass in order still animation, walk animation, popup animation, soundEffect
	 * @param filePaths
	 * @param fileTypes
	 * @param maxFrames
	 * @param size
	 * @throws Exception 
	 */
	protected AbstractAnimalObject(String[] filePaths, String[] fileTypes, int[] maxFrames, float size) {
		super(filePaths, fileTypes, maxFrames, size);
		if (filePaths.length < 4)
			System.err.print("Warning: Invalid number of file paths passed to the abstract animal object");
	}
	
	@Override
	public boolean isLoaded() {
		boolean isLoaded = super.isLoaded();
		if (isLoaded)
			animalSound = new SpikeQuestSoundEffect((Sound) getAsset(FILE_PATHS[3], "Sound"), 10);
		return isLoaded;
	}
	
	/**
	 * I return if the animal can fly
	 * @return
	 */
	public abstract boolean isFlyingAnimal();
	
	/**
	 * When I draw, I set move animation if randomly moving
	 */
	public void draw(SpriteBatch batch) {
		
		if (isRandomlyMoving) {
			
			//moving right or left?
			if (isRandomlyMovingRight)
				moveRight(randomMoveDirection);
			else
				moveLeft(randomMoveDirection);
		}
		else if (isMovingTowardsPoint) {
			super.moveTowardsPoint(movePointX, movePointY, movePointSpeed);
		}
			
		super.draw(batch);
	}
	
	/**
	 * I return an animal texture for drawing a static picture of the animal
	 * @return
	 */
	public Texture getAnimalTexture() {
		return ((TextureAtlas) getAsset(FILE_PATHS[2], "TextureAtlas")).getTextures().first();
	}
	
	public void resetRotation() {
		currentSprite.setRotation(0);
	}
	
	/**
	 * I return the animal's timer object
	 * @return
	 */
	public TimerObject getTimerObject() {
		if (timerObject == null)
			timerObject = new TimerObject();
		
		return timerObject;
	}
	
	/**
	 * I move the animal in a random direction across the screen from a random location Y
	 * if yMax <= yMin, the y position will be yMin
	 * @param anIndicator
	 */
	public void moveRandomDirectionX(float leftStartPosition, float rightStartPosition, float yMin, float yMax, float speed) {
		Random aRandomGenerator = new Random();
		isRandomlyMoving = true;
		isMovingTowardsPoint = false;
		randomMoveDirection = speed;
		float ySpawn = yMax > yMin ? (float) aRandomGenerator.nextInt((int) (yMax - yMin + 1)) : yMin;
		
		//move right or left
		if (aRandomGenerator.nextBoolean()) {
			isRandomlyMovingRight = true;
			spawn(leftStartPosition, ySpawn);
		}
			
		else {
			isRandomlyMovingRight = false;
			spawn(rightStartPosition, ySpawn);
		}
			
	}
	
	public void moveTowardsPoint(float pointX, float pointY, float moveSpeed) {
		movePointSpeed = moveSpeed;
		movePointX = pointX;
		movePointY = pointY;
		isMovingTowardsPoint = true;
		isRandomlyMoving = false;
	}
	
	public void stopMovingRandomly() {
		isRandomlyMoving = false;
	}
	
	public boolean isMovingRandomly() {
		return isRandomlyMoving;
	}

	public boolean isMovingRandomlyRight() {
		return isRandomlyMoving && isRandomlyMovingRight;
	}
	
	public boolean isMovingTowardsPoint() {
		return isMovingTowardsPoint;
	}
	
	/**
	 * Determine if point set in moveTowardsPoint has been reached
	 * @return
	 */
	public boolean isAtPoint() {
		return currentPositionX == movePointX && currentPositionY == movePointY;
	}
	
	public void stopMovingTowardsPoint() {
		isMovingTowardsPoint = false;
	}
	
	/**
	 * I play the animal's noise
	 */
	public void playSound() {
		animalSound.playSound(true);
	}
}
