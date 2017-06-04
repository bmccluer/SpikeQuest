package com.brendanmccluer.spikequest.objects.ponies;

import com.brendanmccluer.spikequest.objects.StandardObject;

public class ScootalooObject extends StandardObject {
	private static final String[] filePaths = {"scootaloo/stand/ScootalooStand.atlas", "scootaloo/walk/ScootalooWalk.atlas", "scootaloo/talk/ScootalooTalk.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {1,1,1};
	private static final float STARTING_SIZE = 0.5f;
	//private static final int[] soundLengths = {10};
	//private static final String[] soundPaths = {"twilight/talk/TwilightSpike.wav"};
	//private static final String[] soundNames = {"spike"};

	public ScootalooObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, true);
	}

	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);	
	}
}
