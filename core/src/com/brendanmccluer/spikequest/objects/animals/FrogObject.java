package com.brendanmccluer.spikequest.objects.animals;

public class FrogObject extends AbstractAnimalObject {
	private static final String[] filePaths = {"animals/frog/FrogStand.atlas", "animals/frog/FrogWalk.atlas", "animals/frog/FrogPopup.atlas", "animals/frog/frogSound.wav"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "Sound"};
	private static final int[] maxFrames = {1,4,1};
	private static float size = 0.25f;
	
	public FrogObject() {
		super(filePaths, fileTypes, maxFrames, size);
	}
	
	@Override
	public void resetSize() {
		setSize(size);
	}

	@Override
	public boolean isFlyingAnimal() {
		return false;
	}
}
