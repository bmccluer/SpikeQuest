package com.brendanmccluer.spikequest.objects.ponies;

import com.brendanmccluer.spikequest.objects.StandardObject;

public class AppleBloomObject extends StandardObject {
	private static final String[] filePaths = {"appleBloom/stand/AppleBloomStand.atlas", "appleBloom/walk/AppleBloomWalk.atlas", "appleBloom/talk/AppleBloomTalk.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {1,1,1};
	private static final float STARTING_SIZE = 0.5f;

	public AppleBloomObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, true);
	}

	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);	
	}
}
