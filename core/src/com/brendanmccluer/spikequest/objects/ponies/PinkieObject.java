package com.brendanmccluer.spikequest.objects.ponies;

import com.brendanmccluer.spikequest.objects.StandardObject;

public class PinkieObject extends StandardObject {
	private static final String[] filePaths = {"pinkie/Pinkie.atlas", "pinkie/walk/PinkieWalk.atlas", "pinkie/talk/PinkieTalk.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {13,6,17};
	private static final float STARTING_SIZE = 0.33f;
	private static final String[] soundPaths = {"pinkie/talk/lala.wav", "pinkie/talk/oh.wav", "pinkie/talk/watchaDoing.wav"};
	private static final int[] soundLengths = {10, 10, 10};
	private static final String[] soundNames = {"lala", "oh", "watchaDoing"};
	
	public PinkieObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, true);
		setSoundEffects(soundPaths, soundLengths, soundNames);
	}
	
	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);	
	}

}
