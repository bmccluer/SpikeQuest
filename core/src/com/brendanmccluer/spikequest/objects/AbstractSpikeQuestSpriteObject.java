package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Object that uses a Sprite and Texture Atlases. 
 * @author Brendan
 *
 */
public abstract class AbstractSpikeQuestSpriteObject extends AbstractSpikeQuestObject {
	protected float currentPositionX = 0;
	protected float currentPositionY = 0;
	protected String currentAnimation = "";
	protected SpikeQuestSprite currentSprite = null;
	protected float currentSize = 1;
	protected boolean objectFacingRight = true;
	protected boolean isSpawned = false;
	protected float gravity = 0.1f;
	protected float gravityWeight = 20;
	protected float ground = 0;
	protected float jumpPower = 1750;
	protected boolean isMidair = false;
	protected boolean firstJump = true;
	protected boolean isJumping = false;
	protected boolean animationSet = false;
	protected Vector3 moveVector = null;
	
	public AbstractSpikeQuestSpriteObject (String[] assetPaths, String[] assetTypes) {
		super(assetPaths, assetTypes);
	}
	
	public AbstractSpikeQuestSpriteObject (String[] assetPaths, String[] assetTypes, boolean isFacingRight) {
		super(assetPaths, assetTypes);
		objectFacingRight = isFacingRight;
	}
	
	public abstract void resetSize();
	
	public void rotate (float degrees) {
		currentSprite.rotate(degrees);
	}
	
	public float getRotation() {
		return currentSprite.getRotation();
	}
	
	public float getCurrentPositionX() {
		return currentPositionX;
	}


	public void setCurrentPositionX(float currentPositionX) {
		this.currentPositionX = currentPositionX;
		currentSprite.setPosition(currentPositionX, currentPositionY);
	}


	public float getCurrentPositionY() {
		return currentPositionY;
	}


	public void setCurrentPositionY(float currentPositionY) {
		this.currentPositionY = currentPositionY;
		currentSprite.setPosition(currentPositionX, currentPositionY);
	}
	
	public void setCurrentPositionXY(float currentPositionX, float currentPositionY) {
		this.currentPositionX = currentPositionX;
		this.currentPositionY = currentPositionY;
		currentSprite.setPosition(currentPositionX, currentPositionY);
	}
	
	/**
	 * AVOID USING IF OBJECT IS USING CONSITENT 
	 * JUMP AND MOVE METHODS. AFTER RESIZE, JUMP AND MOVE WILL
	 * NEED ALTERED
	 * 
	 * I resize the object to by multiplying the current size
	 *  times the ratio. If you use this, make sure to set size of other objects
	 *  to this ratio so everything stays in proportion.
	 * @param ratio
	 */
	public void resize (float ratio) {
		currentSize *= ratio;
		currentSprite.setSize(currentSize);
	}
	
	/**
	 * AVOID USING IF OBJECT IS USING CONSITENT 
	 * JUMP AND MOVE METHODS. AFTER RESIZE, JUMP AND MOVE WILL
	 * NEED ALTERED
	 * 
	 * I resize the object. Use resize if wanting to multiply size times ratio 
	 * 
	 * @param size
	 */
	public void setSize (float size) {
		currentSize = size;
		if (currentSprite != null)
			currentSprite.setSize(currentSize);
	}
	
	/**
	 * I spawn the sprite using the animation passed
	 * @param xPos
	 * @param yPos
	 * @param anAnimationName
	 * @param aTextureAtlas
	 * @param aMaxFrames
	 */
	protected void spawn (float xPos, float yPos, TextureAtlas aTextureAtlas, String anAnimationName,  int aMaxFrames) {
		currentPositionX = xPos;
		currentPositionY = yPos;
		currentAnimation = anAnimationName;
	
		try {
			
			//create the sprite still
			if (currentSprite == null)
				currentSprite = new SpikeQuestSprite(aTextureAtlas, aMaxFrames, currentSize);
			else {
				currentSprite.setCurrentTextureAtlas(aTextureAtlas, aMaxFrames);
			}
			
			currentSprite.setPosition(currentPositionX, currentPositionY);
			
			//set the ground height
			ground = currentPositionY;
			isSpawned = true;
		} 

		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean isSpawned() {
		return isSpawned;
	}

	/*public void update(float delta) {

		//x and y are coordinates. z is speed
		if (moveVector != null)
			moveTowardsPoint(moveVector.x, moveVector.y, moveVector.z * delta);
	}*/
	
	/**
	 * I move sprite to the right at a defined speed (negative values are converted to positive)
	 * @param moveSpeed
	 */
	public void moveRight (float moveSpeed) {
		
		//set direction to right
		if (!objectFacingRight) {
			currentSprite.reverseDirection();
			objectFacingRight = true;
		}
		
		adjustToGravity();
		currentSprite.setPosition(currentPositionX += Math.abs(moveSpeed), currentPositionY);
	}
	
	
	
	/**
	 * I move sprite to the left at a defined speed (negative values are converted to positive)
	 * @param moveSpeed
	 */
	public void moveLeft (float moveSpeed) {
		//set direction to left
		if (objectFacingRight) {
			currentSprite.reverseDirection();
			objectFacingRight = false;
		}
		
		adjustToGravity();
		currentSprite.setPosition(currentPositionX -= Math.abs(moveSpeed), currentPositionY);
	}
	
	/**
	 * I move sprite towards a point
	 * @param moveSpeed
	 */
	public void moveTowardsPoint (float pointX, float pointY, float moveSpeed) {
		//adjust X
		if (currentPositionX < pointX) {
			//set direction to right
			if (!objectFacingRight) {
				currentSprite.reverseDirection();
				objectFacingRight = true;
			}
			if ((currentPositionX + moveSpeed) > pointX) 
				currentPositionX = pointX;
			else
				currentPositionX += Math.abs(moveSpeed);
		}
		else if (currentPositionX > pointX) {
			//set direction left
			if (objectFacingRight) {
				currentSprite.reverseDirection();
				objectFacingRight = false;
			}
			if ((currentPositionX - moveSpeed) < pointX) 
				currentPositionX = pointX;
			else
				currentPositionX -= Math.abs(moveSpeed);
		}
		//adjust Y
		if (currentPositionY < pointY) {
			if ((currentPositionY + moveSpeed) > pointY) 
				currentPositionY = pointY;
			else
				currentPositionY += Math.abs(moveSpeed);
		}
		else if (currentPositionY > pointY) {
			if ((currentPositionY - moveSpeed) < pointY) 
				currentPositionY = pointY;
			else
				currentPositionY -= Math.abs(moveSpeed);
		}

		adjustToGravity();
		currentSprite.setPosition(currentPositionX, currentPositionY);
	}
	
	/**
	 * I move sprite towards an object
	 * @param moveSpeed
	 */
	public void moveTowardsObject (AbstractSpikeQuestSpriteObject anObject, float moveSpeed) {
		if (anObject != null)
			moveTowardsPoint(anObject.getCenterX(), anObject.getCenterY(), moveSpeed);
	}
	
	/**
	 * I keep reset the sprite to currentPositionX and currentPositionY
	 */
	public void standStill () {
		
		currentSprite.setPosition(currentPositionX, currentPositionY);
		adjustToGravity();
	}
	
	/**
	 * I make sprite jump and return true
	 * when done jumping. You must stop calling jump when
	 * true is returned or sprite will jump again in the next
	 * call.
	 * 
	 * @return
	 */
	public boolean jump () {
		
		//initial jump
		if (firstJump) {
			setCurrentPositionY(getCurrentPositionY() + jumpPower * Gdx.graphics.getDeltaTime());
			firstJump = false;
		}
		//in the middle of jump
		else if (adjustToGravity()) {
			setCurrentPositionY(getCurrentPositionY() + (jumpPower-100) * Gdx.graphics.getDeltaTime());
		}
		//done jumping
		else {
			firstJump = true;
			isJumping = false;
			return isJumping;
		}
		isJumping = true;
		return isJumping;
		
	}
	
	public boolean isJumping () {
		return isJumping;
	}
	/**
	 * I check if an animation has been changed and change the atlas if true. I return true
	 * if animation was changed
	 * 
	 */
	protected boolean changeAnimation(String animationIndicator, int animationMaxFrames, TextureAtlas atlas) {
		animationSet = true;
		if (animationIndicator != null && !(animationIndicator.equalsIgnoreCase(currentAnimation))) {
			currentAnimation = animationIndicator;
			try {
				currentSprite.setCurrentTextureAtlas(atlas, animationMaxFrames);
				currentSprite.setSize(currentSize);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return true;
		}
		return false;
	}
	
	/**
	 * I make the sprite fall if in midair and return if 
	 * it is still falling
	 */
	protected boolean adjustToGravity () {
		if (currentPositionY > ground) {
		
			currentPositionY = currentPositionY-(gravity);
			gravity += gravityWeight*Gdx.graphics.getDeltaTime();

			//check if at ground position
			if (currentPositionY <= ground) {
				resetGravity ();
				currentPositionY = ground;
				return false;
			}
			return true;
		}
		//hit the ground
		return false;
		
	}

	
	protected void resetGravity() {
		gravity = 0.1f;
	}

	/**
	 * Use this method if you want the object to fall to a certain position
	 * @param groundPosition
	 */
	public void setGroundPosition (float groundPosition) {
		ground = groundPosition;
	}
	
	public float getGroundPosition () {
		return ground;
	}
	
	public Rectangle getCollisionRectangle () {
		Rectangle spriteRectangle = null; //return copy of rectangle so it can't be manipulated
		
		if (currentSprite != null) 
			spriteRectangle = currentSprite.getSpriteBoundingRectangle();
		
		return spriteRectangle;
	}
	
	/**
	 * I make the object heavier or lighter (at zero I will be weightless)
	 * @param weight
	 */
	public void setWeight(float weight) {
		
		if (weight < 0) {
			weight = 0;
		}
		
		gravityWeight = weight;
	}
	
	public void draw(SpriteBatch batch) {
		if (objectLoaded) {
			drawSprite(currentSprite, batch);
		}
		animationSet = false;
	}

	/**
	 * Check if animation was set before
	 * next draw
     */
	public boolean isAnimationSet() {
		return animationSet;
	}
	
	/**
	 * to be overridden and used by other objects
	 */
	public void talk() {
		
	}
	
	/**
	 * I return center of object X
	 * 
	 * @return
	 */
	public float getCenterX() {
		
		if (currentSprite != null)
			return currentSprite.getSpriteBoundingRectangle().getX() + currentSprite.getSpriteBoundingRectangle().getWidth()/2;
		
		return currentPositionX;
	}
	
	/**
	 * I return center of object Y
	 * 
	 * @return
	 */
	public float getCenterY() {
		
		if (currentSprite != null)
			return currentSprite.getSpriteBoundingRectangle().getY() + currentSprite.getSpriteBoundingRectangle().getHeight()/2;
		
		return currentPositionY;
	}
	
	public float getCurrentSize() {
		return currentSize;
	}
	
	@Override
	public void dispose() {
		currentSprite = null;
		super.dispose();
	}
}
