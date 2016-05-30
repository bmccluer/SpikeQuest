package com.brendanmccluer.spikequest.objects.animals;

public class MouseObject extends AbstractAnimalObject {
	private static final String[] filePaths = {"animals/mouse/MouseStand.atlas", "animals/mouse/MouseWalk.atlas", "animals/mouse/MousePopup.atlas", "animals/mouse/mouseSound.wav"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "Sound"};
	private static final int[] maxFrames = {1,3,1};
	private static float size = 0.50f;
	
	public MouseObject() {
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
