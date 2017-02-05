package com.brendanmccluer.spikequest.objects;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;

/**
 * I implement a generic object for SpikeQuest
 * @author Brendan
 *
 */
public abstract class StandardObject extends AbstractSpikeQuestSpriteObject {
	protected final String[] FILE_PATHS; 
	protected final int[] MAX_FRAMES; //Each index corresponds to index in file paths
	private final String STILL_ANIMATION = "still";
	private final String TALK_ANIMATION = "talk";
	private final String MOVE_ANIMATION = "move";
	private TextureAtlas talkAtlas = null;
	private TextureAtlas stillAtlas = null;
	private TextureAtlas moveAtlas = null;
	
	private int talkFrames = 0;
	private int stillFrames = 0;
	private int moveFrames = 0;
	private boolean canTalk = false;
	private String[] soundPaths = null;
	private int[] soundEffectLengths = null;
	private String[] soundEffectNames = null;
	private SpikeQuestSoundEffect soundEffect = null;
	
	
	/**
	 * FilePaths must have first three in order of (still, move, talk,...) or 
	 * (still, move,...). The rest can be whatever you like
	 * you like.
	 * @param filePaths
	 * @param fileTypes
	 * @param maxFrames
	 * @param size
	 */
	protected StandardObject (String[] filePaths, String[] fileTypes, int[] maxFrames, float size, boolean isTalkingObject) {
		super(filePaths, fileTypes);
		
		FILE_PATHS = filePaths;
		MAX_FRAMES = maxFrames;
		
		/*
		if (FILE_PATHS.length != MAX_FRAMES.length) {
			System.err.println("Arrays must match!");
		}*/
	
		currentSize = size;
		canTalk = isTalkingObject;
	}
	
	/**
	 * I set the sound effects that will be loaded in the object when spawned
	 * 
	 * @param aQuoteArray
	 */
	protected void setSoundEffects (String[] soundpaths, int[] lengths, String[] names) {
		soundPaths = soundpaths;
		soundEffectLengths = lengths;
		soundEffectNames = names;
	}
	
	/**
	 * Pass in sounds of names this object should load (names located in object). Must call this before attempting to play Sound Effect
	 * @param soundNames
	 */
	public void loadSounds(String ... soundNames) {
		soundEffect = new SpikeQuestSoundEffect(); //because sound effects will be used
		
		for (int i = 0; i < soundEffectNames.length; i++) {
			
			for (int z = 0; z < soundNames.length; z++) {
				
				if (soundEffectNames[i].equalsIgnoreCase(soundNames[z])) {
					
					//check that asset has not already been loaded
					if (getAsset(soundPaths[i],"Sound") == null)
						setAsset(soundPaths[i],"Sound");
					
					break;
				}
			}
			
		}
	}

	/**
	 * I play the given sound effect (if it exists in the sound effect library)
	 * 
	 * @param aName
	 */
	public void playSoundEffect (String aName) {
		
		for (int i = 0; i < soundEffectNames.length; i++) {
			
			if (soundEffectNames[i].equalsIgnoreCase(aName)) {
				
				try {
					soundEffect.setSoundEffect((Sound) getAsset(soundPaths[i],"Sound"), soundEffectLengths[i]);
					soundEffect.playSound(true);
				}
				catch (Exception e){
					System.err.println("Error playing sound. Exception: " + e.getMessage());
				}
			
				return;
			}
			
		}
		
		System.err.println("Did not find sound effect: " + aName);
	}
	
	/**
	 * Call this right after playSoundEffect to set the volume
	 * @param volume
	 */
	public void setSoundVolume (float aVolume) {
		soundEffect.setVolume(aVolume);
	}
	
	public void spawn (float xPos, float yPos) {
		
		if (stillAtlas == null) {
			stillAtlas = (TextureAtlas)getAsset(FILE_PATHS[0], "TextureAtlas");
			stillFrames = MAX_FRAMES[0];
		}
			
		if (FILE_PATHS.length > 1 && moveAtlas == null) {
			moveAtlas = (TextureAtlas)getAsset(FILE_PATHS[1], "TextureAtlas");
			moveFrames = MAX_FRAMES[1];
		}
		
		if (canTalk && talkAtlas == null) {
			talkAtlas = (TextureAtlas)getAsset(FILE_PATHS[2], "TextureAtlas");
			talkFrames = MAX_FRAMES[2];
		}
		
		super.spawn(xPos, yPos, stillAtlas, STILL_ANIMATION, stillFrames);
		
	}
	
	/**
	 * I keep object on currentX and currentY position
	 */
	@Override
	public void standStill () {
		checkChangeAnimation(STILL_ANIMATION,stillFrames,stillAtlas);
		super.standStill();
	}
	
	@Override
	public void moveTowardsObject (AbstractSpikeQuestSpriteObject anObject, float moveSpeed)  {
		checkChangeAnimation(MOVE_ANIMATION,moveFrames,moveAtlas);
		super.moveTowardsObject(anObject, moveSpeed);
	}
	
	@Override
	public void moveTowardsPoint(float pointX, float pointY, float moveSpeed) {
		checkChangeAnimation(MOVE_ANIMATION,moveFrames,moveAtlas);
		super.moveTowardsPoint(pointX, pointY, moveSpeed);
	}
	
	public void moveRight (int moveSpeed) {
		checkChangeAnimation(MOVE_ANIMATION,moveFrames,moveAtlas);
		super.moveRight(moveSpeed);
	}
	
	
	public void moveLeft (int moveSpeed) {
		checkChangeAnimation(MOVE_ANIMATION,moveFrames,moveAtlas);
		super.moveLeft(moveSpeed);
	}
	
	@Override
	public void moveRight (float moveSpeed) {
		checkChangeAnimation(MOVE_ANIMATION,moveFrames,moveAtlas);
		super.moveRight(moveSpeed);
	}
	
	@Override
	public void moveLeft (float moveSpeed) {
		checkChangeAnimation(MOVE_ANIMATION,moveFrames,moveAtlas);
		super.moveLeft(moveSpeed);
	}
	
	@Override
	public void talk () {
		if (talkAtlas != null)
			checkChangeAnimation(TALK_ANIMATION,talkFrames,talkAtlas);
		//keep sprite in same position in case of offset
		super.standStill();
	}
	
	/**
	 * Override talk animation OBJECT WILL CHANGE TO THE ANIMATION PASSED
	 * @param aTextureAtlas
	 */
	protected void setTalkAtlas (TextureAtlas aTextureAtlas, int maxFrames){
		talkAtlas = aTextureAtlas;
		talkFrames = maxFrames;
		
		checkChangeAnimation("new", maxFrames, aTextureAtlas);
		
	}
	
	protected TextureAtlas getTalkAtlas () {
		return talkAtlas;
	}
	
	/**
	 * Override move animation OBJECT WILL CHANGE TO THE ANIMATION PASSED
	 * @param aTextureAtlas
	 */
	protected void setMoveAtlas (TextureAtlas aTextureAtlas, int maxFrames){
		 moveAtlas = aTextureAtlas;
		 moveFrames = maxFrames;
		 
		checkChangeAnimation("new", maxFrames, aTextureAtlas);
	}

	protected TextureAtlas getMoveAtlas () {
		return moveAtlas;
	}

	/**
	 * Override still animation OBJECT WILL CHANGE TO THE ANIMATION PASSED
	 * @param aTextureAtlas
	 */
	protected void setStillAtlas (TextureAtlas aTextureAtlas, int maxFrames){
		 stillAtlas = aTextureAtlas;
		 stillFrames = maxFrames;
		 
		checkChangeAnimation("new", maxFrames, aTextureAtlas);
		 
	}
	
	protected TextureAtlas getSillAtlas () {
		return stillAtlas;
	}
	
	@Override
	public void discard () {
		
		if (soundEffect != null)
			soundEffect.discard();
		
		soundEffectLengths = null;
		soundPaths = null;
		soundEffectNames = null;
		
		if (talkAtlas != null) {
			talkAtlas.dispose();
			talkAtlas = null;
		}
		
		if (stillAtlas != null) {
			stillAtlas.dispose();
			talkAtlas = null;
		}
		
		if (moveAtlas != null) {
			moveAtlas.dispose();
			moveAtlas = null;
		}
		
		super.discard();
	}

}
