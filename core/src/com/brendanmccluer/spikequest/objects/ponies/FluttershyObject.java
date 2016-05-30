package com.brendanmccluer.spikequest.objects.ponies;

import com.brendanmccluer.spikequest.objects.StandardObject;

public class FluttershyObject extends StandardObject {
	private static final String[] filePaths = {"fluttershy/stand/FluttershyStand.atlas", "fluttershy/stand/FluttershyStand.atlas", "fluttershy/talk/FluttershyTalk.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {12,12,12};
	private static final float STARTING_SIZE = 0.33f;
	private static final int[] soundLengths = {10,10,10,10};
	private static final String[] soundPaths = {"fluttershy/talk/ComeOn.wav","fluttershy/talk/It'sOk.wav","fluttershy/talk/NothingToBeAfraidOf.wav", "fluttershy/talk/Yay.wav"}; 
	private static final String[] soundNames = {"comeOn","it'sOk","nothingToBeAfraidOf","yay"};
	
	public FluttershyObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, true);
		setSoundEffects(soundPaths, soundLengths, soundNames);
	}
	
	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);	
	}
}
