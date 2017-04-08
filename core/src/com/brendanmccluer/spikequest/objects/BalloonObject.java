package com.brendanmccluer.spikequest.objects;

import java.util.Random;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;

public class BalloonObject extends AbstractSpikeQuestSpriteObject {
	private static final String[] filePaths = {"object/balloons/BlueBalloon.atlas", "object/balloons/RedBalloon.atlas", "object/balloons/YellowBalloon.atlas",
			"object/balloons/BlueDiscordBalloon.atlas", "object/balloons/RedDiscordBalloon.atlas","object/balloons/YellowDiscordBalloon.atlas", "sounds/BalloonPop.wav"}; 
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "TextureAtlas", "TextureAtlas", "TextureAtlas", "Sound"};
	private static final float BALLOON_HORIZONTAL_OFFSET = 10f;
	private static final float BALLOON_VERTICAL_OFFSET = 15F;
	private static final float BALLOON_GROUND_OFFSET = 15f;
	private static final float HIT_SPEED = 35;
	private int timerMax = 600;
	
	private static float size = 0.33f;
	private Random randomGenerator = new Random();
	//private TextureAtlas currentTextureAtlas = null;
	private boolean isPopped = true;
	private boolean isSpawned = false;
	private boolean isHit = false;
	private boolean isRegularBalloon = false;
	private boolean isBadPop = false; //if balloon popped on it's own while it was discord balloon
	
	private float groundBeforeAdjustment = 0;
	private float spawnPositionY = 0;
	
	private int balloonTimer = timerMax;
	
	private SpikeQuestSoundEffect popSoundEffect = null;
	
	public BalloonObject() {
		super(filePaths, fileTypes);
		setWeight(1);
		currentSize = size;
	}
	
	/**
	 * I spawn a random balloon
	 * @param xPos
	 * @param yPos
	 */
	public void spawn (float xPos, float yPos, int popTime) {
		super.spawn(xPos, yPos, getRandomTexture(), "", 1);
		spawnPositionY = yPos;
		timerMax = popTime;
		
		if (popSoundEffect == null)
			popSoundEffect = new SpikeQuestSoundEffect((Sound) getAsset(filePaths[6], "Sound"), 10);
		
		isHit = false;
		isPopped = false;
		isSpawned = true;
		isJumping = false;
		isBadPop = false;
	}
	
	public boolean isRegularBalloon() {
		return isRegularBalloon;
	}
	
	public boolean isPopped () {
		return isPopped;
	}
	
	/**
	 * pop the balloon and return if successful
	 * @return
	 */
	public boolean pop (boolean playSound) {
		isPopped = true;
		isSpawned = false;
		isJumping = false;
		
		//in case gravity was not reset
		resetGravity();
		
		//reset timer
		balloonTimer = timerMax;
		
		//pop sound effect
		if (playSound)
			popSoundEffect.playSound(true);
		
		//set animation here
		return true;
	}
	
	public boolean isHit() {
		return isHit;
	}
	
	public boolean isSpawned () {
		return isSpawned;
	}
	
	/**
	 * I return if the pop was bad. This sets to false in case called twice before next
	 * spawn.
	 * @return
	 */
	public boolean isBadPop () {
		boolean returnBoolean = isBadPop;
		isBadPop = false;
		
		return returnBoolean;
	}
	
	/**
	 * I set the balloon to a different balloon
	 */
	public void setRandomBalloon () {
		try {
			currentSprite.setCurrentTextureAtlas(getRandomTexture(), 1);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	/**
	 * keep calling this to make balloon animate and fall to gravity
	 */
	@Override
	public void draw (SpriteBatch batch) {
		
		if (currentPositionY <= ground)
			balloonTimer--;
		
		if (balloonTimer < 0) {
			
			if (!isPopped && !isRegularBalloon) {
				isBadPop = true;
				pop(true);
			}
			else
				pop(false);
			
			balloonTimer = timerMax;
		}
		
		//pop if past spawn position 
		else if (currentPositionY > spawnPositionY) {
			pop(false);
			return;
		}
		
		//continue moving up if hit
		else if (isHit) {
			currentPositionY += HIT_SPEED;
		}
		
		rotate();
		ground = groundBeforeAdjustment - BALLOON_GROUND_OFFSET;
		super.standStill();
		
		//don't draw for blink
		if (balloonTimer < 150 && balloonTimer % 4 == 0) {
			return;
		}
		super.draw(batch);
			
	}
	
	private void rotate() {
		float currentRotation = Math.round(currentSprite.getRotation());
		
		//rotate balloon slightly (positive is counter-clockwise)
		if (currentPositionY > ground)
			currentSprite.rotate(5);
		else {
			
			//rotate and move until balloon is on it's side
			if (currentRotation != 270 && currentRotation != 90){
			
				if (currentSprite.getRotation() > 180) {
					currentSprite.rotate(5);
					
					//stops moving if we overshoot 270
					if (currentSprite.getRotation() > 270)
						currentSprite.setRotation(270);
						
					//super.moveLeft(5);
				}
				else {
					currentSprite.rotate(-5);
					
					//stops moving if we overshoot 90
					if (currentSprite.getRotation() < 90)
						currentSprite.setRotation(90);
					
					//super.moveRight(5);
				}
			}
			
		}
	}

	/**
	 * Balloon moves constantly up and pop when reached the 
	 * point where spawned. (I return if regular balloon)
	 */
	public void hitOffScreen () {
		currentPositionY += HIT_SPEED;
		
		isHit = true;
	}
	
	
	@Override
	public void setGroundPosition(float groundPosition) {
		super.setGroundPosition(groundPosition);
		groundBeforeAdjustment = ground;
	}
	
	
	/**
	 * I return points via rectangle where the balloon would collide 
	 * at different points depending on it's orientation
	 * @return
	 */
	public Rectangle getCollisionRectangle () {
		Rectangle areaRectangle = null;
		Rectangle newRectangle = null;
		float currentRotation = 0;
		
		if (currentSprite != null && !isPopped) {
		
			currentRotation = Math.round(currentSprite.getRotation());
	
			areaRectangle = currentSprite.getSpriteBoundingRectangle();
			newRectangle = new Rectangle();
			
			//balloon is facing vertical
			if ((currentRotation > 45 && currentRotation <  315) || 
					(currentRotation > 135 && currentRotation < 225)) {
				
				//will have same height as sprite rectangle 
				newRectangle.setHeight(areaRectangle.getHeight());
				
				//will have less width than sprite rectangle
				newRectangle.setWidth(areaRectangle.width-BALLOON_VERTICAL_OFFSET);
			}
			//balloon facing horizontal
			else {
				
				//will have less height than sprite rectangle
				newRectangle.setHeight(areaRectangle.height-BALLOON_HORIZONTAL_OFFSET);
				//will have same width as sprite rectangle
				newRectangle.setWidth(areaRectangle.width);
			}
			newRectangle.setX(areaRectangle.getX());
			newRectangle.setY(areaRectangle.getY());
			areaRectangle.fitInside(newRectangle);
			
			newRectangle = null;
		}
		else {
			areaRectangle = new Rectangle(0,0,0,0);
		}
			
			return areaRectangle;
	}
	
	/**
	 * I grab a random texture atlas
	 * @return
	 */
	private TextureAtlas getRandomTexture () {
		//75% chance of getting discord balloon 
		int balloonType = randomGenerator.nextInt(4);
		int index = 0;
		
		//random regular balloon (range is 0-2)
		if (balloonType == 0) {
			index = randomGenerator.nextInt(3);
			isRegularBalloon = true;
		}
		else {
			//random discord balloon (max-min+1) + min. (range 3-5)
			index = randomGenerator.nextInt(3) + 3;
			isRegularBalloon = false;
		}
		
		return (TextureAtlas) getAsset(filePaths[index], "TextureAtlas");
	}

	@Override
	/**
	 * Not implemented!
	 */
	public void resetSize() {}
	
}
