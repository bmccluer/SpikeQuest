package com.brendanmccluer.spikequest.objects.animals;

//TexturePacker.process("C:\\Users\\Brendan\\Documents\\SpikeQuestAssets\\ShyAndSeekGame\\Owl\\Walk", "C:\\Users\\Brendan\\Documents\\SpikeQuest\\android\\assets\\animals\\owl", "OwlWalk");
public class OwlObject extends AbstractAnimalObject {
	private static final String[] filePaths = {"animals/owl/OwlStand.atlas", "animals/owl/OwlWalk.atlas", "animals/owl/OwlPopup.atlas", "animals/owl/owlSound.wav"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "Sound"};
	private static final int[] maxFrames = {1,4,1};
	private static float size = 0.25f;
	
	public OwlObject() {
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
