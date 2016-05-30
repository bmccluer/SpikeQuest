package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class HatObject extends StandardObject {
	private static final String[] filePaths = {"object/hat/PinkHat.atlas", "object/hat/PinkHat.atlas", "object/hat/BlueHat.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {12,6,11,12};
	private static float size = 0.33f;
	
	
	public HatObject() {
		//PinkHat for move and still animation
		super(filePaths, fileTypes, maxFrames, size, false);
	}
	
	/**
	 * set the color
	 * NOTE: Will change animation to standStill()
	 * 
	 */
	public void setHatBlue () {
		
		setMoveAtlas((TextureAtlas)getAsset(filePaths[2], "TextureAtlas"), maxFrames[2]);
		setStillAtlas((TextureAtlas)getAsset(filePaths[2], "TextureAtlas"), maxFrames[2]);
		
	}
	
	/**
	 * set the color
	 * NOTE: Will change animation to standStill()
	 */
	public void setHatPink () {
		
		setMoveAtlas((TextureAtlas)getAsset(filePaths[0], "TextureAtlas"), maxFrames[0]);
		setStillAtlas((TextureAtlas)getAsset(filePaths[0], "TextureAtlas"), maxFrames[0]);
		
	}

	@Override
	/**
	 * NOT IMPLEMENTED
	 */
	public void resetSize() {}
}
