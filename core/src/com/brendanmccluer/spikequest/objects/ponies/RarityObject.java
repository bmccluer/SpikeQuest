package com.brendanmccluer.spikequest.objects.ponies;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brendanmccluer.spikequest.objects.StandardObject;

public class RarityObject extends StandardObject {
	private static final String[] filePaths = {"rarity/stand/RarityStand.atlas", "rarity/walk/RarityWalk.atlas", "rarity/talk/RarityTalk.atlas", 
		"rarity/stand/RarityStandMagic.atlas", "rarity/walk/RarityWalkMagic.atlas", "rarity/talk/RarityTalkMagic.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "TextureAtlas","TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {12,6,12,12,6,12}; 
	private static final float STARTING_SIZE = 0.33f;
	private boolean usingMagic = false;
	private static final int[] soundLengths = {10};
	private static final String[] soundPaths = {"rarity/talk/SpikeyWikey.wav"}; 
	private static final String[] soundNames = {"spikeyWikey"};
	
	public RarityObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, true);
		setSoundEffects(soundPaths, soundLengths, soundNames);
	}
	
	/**
	 * Set to true if Rarity should talk, stand, and move with her magic on her horn.
	 * NOTE: The animation will be set to standStill() if changed
	 * 
	 * @param aBoolean
	 */
	public void setUsingMagic (boolean aBoolean) {
		
		if (usingMagic != aBoolean) {
			
			usingMagic = aBoolean;
			
			if (usingMagic) {
				setTalkAtlas((TextureAtlas)getAsset(filePaths[5], "TextureAtlas"), maxFrames[5]);
				setMoveAtlas((TextureAtlas)getAsset(filePaths[4], "TextureAtlas"), maxFrames[4]);
				setStillAtlas((TextureAtlas)getAsset(filePaths[3], "TextureAtlas"), maxFrames[3]);
				return;
			}
			
			setTalkAtlas((TextureAtlas)getAsset(filePaths[2], "TextureAtlas"), maxFrames[2]);
			setMoveAtlas((TextureAtlas)getAsset(filePaths[1], "TextureAtlas"), maxFrames[1]);
			setStillAtlas((TextureAtlas)getAsset(filePaths[0], "TextureAtlas"), maxFrames[0]);
			
		}
		
	}
	
	public boolean isUsingMagic () {
		return usingMagic;
	}
	
	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);	
	}

}
