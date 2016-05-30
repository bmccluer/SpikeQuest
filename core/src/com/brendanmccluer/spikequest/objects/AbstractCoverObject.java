package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.brendanmccluer.spikequest.common.objects.TimerObject;

public abstract class AbstractCoverObject extends AbstractSpikeQuestObject {
	protected static final float POPUP_SPEED = 0.5f;
	protected static final int ROTATE_RIGHT_DEGREES = -90;
	protected static final int ROTATE_LEFT_DEGREES = 90;
	protected static final int ROTATE_FLIP_DEGREES = 180;
	protected static final float POP_UP_SIZE_SML = 0.25f;
	protected static final float POP_UP_SIZE_SMLMED = 0.50f;
	protected static final float POP_UP_SIZE_MED = 0.75f;
	protected static final float POP_UP_SIZE_MEDLRG = 1f;
	protected static final float POP_UP_SIZE_LRG = 1.25f;	
	protected Texture texture = null;
	protected AbstractPopUpObject popUpObject;
	protected String filePath = null;
	protected Rectangle rectangle = null;
	protected float xPos = 0;
	protected float yPos = 0;
	protected float popUpDirectionX = 0;
	protected float popUpDirectionY = 0;
	protected boolean objectHiding = true;
	protected boolean objectPoppingUp = true;
	protected float popUpRotation = 0;
	protected float popUpSize = 0;
	protected float popUpStopPositionX = 0;
	protected float popUpStopPositionY = 0;
	protected int popUpDirection = 1;
	protected TimerObject popUpTimer = null;
	protected int popUpTimerMinutes = 0;
	protected int popUpTimerSeconds = 2;
	protected float distanceToCornerY = 0;
	protected float distanceToCornerX = 0;
	
	/**
	 * Create a cover object passing a texture file path
	 * The cover object can hide PopUpObjects and reveal them in the direction coordinates passed
	 * Also pass the rotation (in degrees) the object should face during popup
	 * 
	 * @param filePath
	 */
	public AbstractCoverObject(String filePath, float xPos, float yPos, float popUpDirectionX, float popUpDirectionY, 
			float popUpRotation, float popUpSize) {
		super(filePath, "Texture");
		
		this.filePath = filePath;
		this.xPos = xPos;
		this.yPos = yPos;
		this.popUpDirectionX = popUpDirectionX;
		this.popUpDirectionY = popUpDirectionY;
		this.popUpRotation = popUpRotation;
		this.popUpSize = popUpSize;
		
		rectangle = new Rectangle(xPos, yPos, 0, 0);
		
	}
	
	/**
	 * I draw the cover object and continue popup object animation if applicable
	 * @param batch
	 */
	public void draw (SpriteBatch batch) {
		
		if (popUpObject != null) {
			
			if (isObjectPoppingUp()) {
				popUpObject.popup(popUpDirectionX * popUpDirection, popUpDirectionY * popUpDirection);
				popUpObject.setHidden(isPopUpHidden());
			}
			else if (popUpTimer.isTimerFinished()) {
				popUpObject();
			}
			
		}		
	
		batch.draw(texture, xPos, yPos);
	}
	
	/**
	 * I stop the popup and reset the timer to pop back up again
	 * @param xPos
	 * @param yPos
	 */
	protected void stopPopUp() {
		
		objectPoppingUp = false;
		popUpTimer.startTimer(popUpTimerMinutes, popUpTimerSeconds);
	}

	/**
	 * get the collision rectangle. You can modify the rectangle using this as well
	 * 
	 * @return
	 */
	public Rectangle getCollisionRectangle () {
		return rectangle;
	}
	
	/**
	 * use this to get the object and play its animation, set properties, etc.
	 * @return
	 */
	public AbstractPopUpObject getPopUpObject () {
		return popUpObject;		
	}
	
	public void removePopUpObject () {
		objectPoppingUp = false;
		popUpObject = null;
	}

	public Texture getTexture () {
		return texture;
	}

	public float getXPos() {
		return xPos;
	}

	public float getYPos() {
		return yPos;
	}

	@Override
	public boolean isLoaded() {
		boolean isLoaded = super.isLoaded();
		
		if (isLoaded && texture == null) {
			
			texture = (Texture) getAsset(filePath, "Texture");
			rectangle.setWidth(texture.getWidth());
			rectangle.setHeight(texture.getHeight());
			
		}
			
		return super.isLoaded();
	}

	public boolean isObjectHidden() {
		return objectHiding;
	}
	
	public boolean isObjectPoppingUp() {
		return popUpObject != null && objectPoppingUp;
	}

	/**
	 * I set the object to pop up on the next draw
	 */
	public void popUpObject () {
		
		objectHiding = false;
		objectPoppingUp = true;
		
	}
	
	public void setCollisionRectangle (Rectangle aRectangle) {
		rectangle = aRectangle;
	}
	
	protected float getTopOfPopUp () {
		return popUpObject.getCollisionRectangle().getY() + popUpObject.getCollisionRectangle().getHeight();
	}

	/**
	 * set object to hide behind cover (Make sure it is loaded and spawned first!). Override this method in subclasses!
	 * 
	 * @param anObject
	 */
	public void setPopUpObject (AbstractPopUpObject aPopUpObject) {
		popUpObject = aPopUpObject;
		//set popup animation
		popUpObject.popup(0, 0);
		popUpObject.resize(popUpSize);
		rotate();
		popUpTimer = new TimerObject();
		objectHiding = true;
		popUpObject.setBehindCover(true);
		popUpObject.setHidden(true);
	}	
	
	/**
	 * I calculate the corners of the popup object after rotation
	 * @return
	 */
	public void rotate() {
		
		if (popUpObject != null) {
			
			//determine where left corner will be after rotation
			// translate point to origin
			float tempX = popUpObject.getCurrentPositionX() - popUpObject.getCenterX();
			float tempY = popUpObject.getCurrentPositionY() - popUpObject.getCenterY();
			float oldX = popUpObject.getCurrentPositionX();
			float oldY = popUpObject.getCurrentPositionY();
			
			// now apply rotation
			popUpObject.rotate(popUpRotation);
			float rotatedX = (float) (tempX*Math.cos(popUpRotation) - tempY*Math.sin(popUpRotation));
			float rotatedY = (float) (tempX*Math.sin(popUpRotation) + tempY*Math.cos(popUpRotation));

			// translate back
			distanceToCornerY = Float.compare((rotatedY + popUpObject.getCenterY()),oldY);
			distanceToCornerX = Float.compare(rotatedX + popUpObject.getCenterX(),oldX);
		}
		
		
	}
	
	public abstract boolean isPopUpHidden();
	
	public abstract int getPoints();
}
