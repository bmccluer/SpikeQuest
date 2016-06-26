package com.brendanmccluer.spikequest.objects.ponies;

import com.brendanmccluer.spikequest.objects.StandardObject;

public class RainbowDashObject extends StandardObject {
	private static final String[] filePaths = {"twilight/still/TwilightStill.atlas", "twilight/still/TwilightStill.atlas", "twilight/talk/TwilightTalk.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {10,10,10};
	private static final float STARTING_SIZE = 0.33f;
	private static final int[] soundLengths = {10};
	private static final String[] soundPaths = {"twilight/talk/TwilightSpike.wav"};
	private static final String[] soundNames = {"spike"};

	public RainbowDashObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, true);
		setSoundEffects(soundPaths, soundLengths, soundNames);
	}

	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);	
	}
}
