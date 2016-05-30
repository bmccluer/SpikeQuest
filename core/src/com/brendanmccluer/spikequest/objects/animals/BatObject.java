package com.brendanmccluer.spikequest.objects.animals;

public class BatObject extends AbstractAnimalObject {
	private static final String[] filePaths = {"animals/bat/BatStand.atlas", "animals/bat/BatWalk.atlas", "animals/bat/BatPopup.atlas", "animals/bat/batSound.wav"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "Sound"};
	private static final int[] maxFrames = {1,4,1};
	private static float size = 0.35f;
	
	public BatObject() {
		super(filePaths, fileTypes, maxFrames, size);
	}

	@Override
	public void resetSize() {
		setSize(size);
	}

	@Override
	public boolean isFlyingAnimal() {
		return true;
	}
}
