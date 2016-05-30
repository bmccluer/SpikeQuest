package com.brendanmccluer.spikequest.objects.animals;

public class BeaverObject extends AbstractAnimalObject {
	private static final String[] filePaths = {"animals/beaver/BeaverStand.atlas", "animals/beaver/BeaverWalk.atlas", "animals/beaver/BeaverPopup.atlas", "animals/beaver/beaverSound.wav"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "Sound"};
	private static final int[] maxFrames = {1,3,1};
	private static float size = 0.40f;
	
	public BeaverObject() {
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
