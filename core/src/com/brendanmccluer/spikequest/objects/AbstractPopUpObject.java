package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public abstract class AbstractPopUpObject extends StandardObject {
	protected static final String POPUP_ANIMATION = "popup";
	protected boolean isBehindCover = false;
	protected boolean isHidden = false;
	
	/**
	 * pass in order: {still animation, move animation, popup animation}
	 * @param filePaths
	 * @param fileTypes
	 * @param maxFrames
	 * @param size
	 */
	protected AbstractPopUpObject(String[] filePaths, String[] fileTypes, int[] maxFrames, float size) {
		super(filePaths, fileTypes, maxFrames, size, false);
		
		//this.popUpMax = popUpMax;
	}
	
	/**
	 * I won't draw the object if it is hidden
	 *//*
	@Override
	public void draw (SpriteBatch batch) {
		
		if (hidden)
			return;
		
		if (isPoppingUp) {
			
			if (currentPositionY + POPUP_SPEED >= popUpStart + popUpMax) {
				
				setCurrentPositionY(popUpStart + popUpMax);
			}
			else
				setCurrentPositionY(getCurrentPositionY() + POPUP_SPEED);
		}
		
		super.draw(batch);
	}*/
	
	public boolean isHidden() {
		return isHidden;
	}
	
	public void setHidden(boolean anIndicator) {
		isHidden = anIndicator;
	}
/*
	public boolean isPoppingUp() {
		return isPoppingUp;
	}*/

	/**
	 * 
	 */
	public void popup(float directionX, float directionY) {
		
		changeAnimation(POPUP_ANIMATION, MAX_FRAMES[2], (TextureAtlas) getAsset(FILE_PATHS[2], "TextureAtlas"));
		currentSprite.setPosition(currentPositionX += directionX, currentPositionY += directionY);	
	}

	/*public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}*/

	@Override
	public void spawn (float xPos, float yPos) {
		
		//popUpStart = yPos;
		
		super.spawn(xPos, yPos);
	}
	
	
	public void setBehindCover(boolean anIndicator) {
		isBehindCover = anIndicator;
	}
	
	public boolean isBehindCover() {
		return isBehindCover;
	}
	
	
}
