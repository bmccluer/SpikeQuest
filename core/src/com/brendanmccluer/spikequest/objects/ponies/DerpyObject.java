package com.brendanmccluer.spikequest.objects.ponies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.common.objects.SpikeQuestBannerObject;
import com.brendanmccluer.spikequest.objects.StandardObject;


public class DerpyObject extends StandardObject {
	private static final String[] filePaths = {"derpy/Derpy.atlas", "derpy/Derpy.atlas"}; //Two to define still and move
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas"};
	
	private static final int BANNER_OFFSET_POSITION_X_FACING_RIGHT = 5;
	private static final int BANNER_OFFSET_POSITION_X_FACING_LEFT = 5;
	//private static final int BANNER_OFFSET_POSITION_Y = 30;
	
	private static final int[] maxFrames = {5,5};
	private static final float STARTING_SIZE = 0.33f;
	private SpikeQuestBannerObject aBannerObject = null;
	
	public DerpyObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, false);
	}
	
	@Override
	public boolean isLoaded () {
		
		//load banner if exists
		if (aBannerObject != null)
			return aBannerObject.isLoaded() && super.isLoaded();
		
		return super.isLoaded();
	}
	
	/**
	 * set a banner for Derpy. 
	 * SET THIS BEFORE LOADING ASSETS
	 * 
	 * @param aBannerText
	 */
	public void setBanner(String aBannerText) {
		
		if (aBannerObject == null)
			aBannerObject = new SpikeQuestBannerObject(aBannerText);
			
		aBannerObject.setBannerText(aBannerText);
		
	}
	
	/**
	 * change the banner text (if a banner was set)
	 * 
	 * @param aBannerText
	 */
	public void setBannerText(String aBannerText) {
		
		if (aBannerObject != null)
			aBannerObject.setBannerText(aBannerText);
		
	}
	
	public void setBannerTextSize(float aSize) {
		
		if (aBannerObject != null)
			aBannerObject.setBannerTextSize(aSize);
		
	}

	
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		
		if (aBannerObject != null) {
			
			//set positions of banner object
			if (super.objectFacingRight)
				aBannerObject.setCurrentXPos(currentPositionX - aBannerObject.getWidth() + BANNER_OFFSET_POSITION_X_FACING_RIGHT);
			
			else
				aBannerObject.setCurrentXPos(currentPositionX  + currentSprite.getSpriteBoundingRectangle().width + BANNER_OFFSET_POSITION_X_FACING_LEFT);
				
			aBannerObject.setCurrentYPos(currentPositionY + aBannerObject.getHeight()/2);
			
			aBannerObject.draw(batch);
			
		}
	}
	
	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);
	}
	

}
