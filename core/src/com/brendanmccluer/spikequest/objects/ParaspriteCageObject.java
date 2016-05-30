package com.brendanmccluer.spikequest.objects;

public class ParaspriteCageObject extends StandardObject {
	private static final String[] filePaths = {"object/wagon/Wagon.atlas", "object/wagon/Wagon.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {1,2};	//use one frame for same texture atlas to keep still
	private static final float STARTING_SIZE = 0.33f;

	public ParaspriteCageObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, false);
	}

	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);
	}

}
