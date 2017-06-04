package com.brendanmccluer.spikequest.objects.ponies;

import com.brendanmccluer.spikequest.objects.StandardObject;

public class SweetieBelleObject extends StandardObject {
	private static final String[] filePaths = {"sweetieBelle/stand/SweetieBelleStand.atlas", "sweetieBelle/walk/SweetieBelleWalk.atlas", "sweetieBelle/talk/SweetieBelleTalk.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {1,1,1};
	private static final float STARTING_SIZE = 0.5f;
	//private static final int[] soundLengths = {10};
	//private static final String[] soundPaths = {"twilight/talk/TwilightSpike.wav"};
	//private static final String[] soundNames = {"spike"};

	public SweetieBelleObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, true);
	}

	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);	
	}
}
