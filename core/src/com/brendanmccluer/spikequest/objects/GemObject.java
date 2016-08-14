package com.brendanmccluer.spikequest.objects;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;

public class GemObject extends AbstractSpikeQuestSpriteObject {
	private static final String[] filePaths = {"object/gems/GreenGem.atlas", "object/gems/BlueGem.atlas", "object/gems/YellowGem.atlas", "object/gems/RedGem.atlas",
		"object/gems/PurpleGem.atlas", "object/gems/LightBlueGem.atlas", "object/gems/BlackGem.atlas", "sounds/Gem.wav", "sounds/BadBuzzer.mp3"}; 
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "TextureAtlas", 
		"TextureAtlas", "TextureAtlas", "TextureAtlas", "Sound", "Sound"};
	private static final int COLLECTION_SOUND_LENGTH = 10; 
	private static final int[] GEM_VALUES = {1,5,10,20,50,100,-100};
	private static final float STARTING_SIZE = 0.5f;
	
	private SpikeQuestSoundEffect aCollectionSoundEffect = null;
	private SpikeQuestSoundEffect aBadGemSoundEffect = null;

	private boolean isActive = false;
	private int timerMax = 600;
	private int timerIndex = 0;
	private int currentIndex;
	private Random randomGenerator = new Random();
	public boolean canExpire = true;
	
	public GemObject() {
		super(filePaths, fileTypes);
	}
	
	/**
	 * I spawn a random gem. If isBadGem is true, I always spawn a bad gem
	 * @param xPos
	 * @param yPos
	 */
	public void spawn (float xPos, float yPos, boolean isBadGem) {
		
		//determine which to spawn
		if (isBadGem) {
			super.spawn(xPos,yPos,(TextureAtlas) getAsset(filePaths[6], "TextureAtlas"), "", 1);
			currentIndex = 6;
		}
		else
			super.spawn(xPos, yPos, getRandomTexture(), "", 1);
		//spawnPositionY = yPos;
		
		isActive = true;
		setSize(STARTING_SIZE);
	}
	
	@Override
	public boolean isLoaded () {
		boolean isLoaded = super.isLoaded();
		
		if (isLoaded && aCollectionSoundEffect == null) {
			
			aCollectionSoundEffect = new SpikeQuestSoundEffect((Sound) getAsset(filePaths[7], "Sound"), COLLECTION_SOUND_LENGTH);
			aBadGemSoundEffect = new SpikeQuestSoundEffect((Sound) getAsset(filePaths[8], "Sound"), COLLECTION_SOUND_LENGTH);
		}
		
		return isLoaded;
	}
	
	/**
	 * I return if the gem is still active on the screen
	 * 
	 * @return
	 */
	public boolean getIsActive ()
	{
		return isActive || !canExpire;
	}
	
	/**
	 * I draw the gem (ONLY IF IT IS ACTIVE). Active timer counts each time this is called
	 * 
	 */
	@Override
	public void draw (SpriteBatch batch) {
		
		if (isActive || !canExpire) {
		
			timerIndex++;
			
			if (!canExpire || timerIndex < 300 || timerIndex % 2 == 0){
				super.draw(batch);
			}
			
			if (timerIndex >= timerMax) {
				isActive = false;
				timerIndex = 0;
			}
		
			super.standStill();		
		}
	}
	
	/**
	 * I turn isActive off and return the value of the gem 
	 * 
	 * @return
	 */
	public int collectGem () {
		
		isActive = false;
		canExpire = true;
		timerIndex = 0;
		
		//bad gem. Play bad sound
		if (currentIndex == 6) {
			aBadGemSoundEffect.playSound(true);
		}
		else {
		
			aCollectionSoundEffect.playSound(true);
			aCollectionSoundEffect.setVolume(0.1f);
			aCollectionSoundEffect.setPitch(0.5f + randomGenerator.nextFloat());
		}
			
		return GEM_VALUES[currentIndex];
		
	}
	
	/**
	 * I grab a random texture atlas
	 * green=0,blue=1,yellow=2,red=3,purple=4,lightBlue=5,black=6 (black not used here)
	 * 
	 * @return
	 */
	private TextureAtlas getRandomTexture () {
		
		//90% of getting green, blue, yellow, or red
		if (randomGenerator.nextInt(10) != 0) {
			
			//25% chance of blue, yellow, and red
			if (randomGenerator.nextInt(4) != 0)
				currentIndex = randomGenerator.nextInt(3) + 1; //1/3 chance of blue, yellow, OR red
			
			//75% chance of getting green
			else 
				currentIndex = 0;
		}
		//10% chance of purple and lightBlue
		else {
			
			//25% chance of lightBlue
			if (randomGenerator.nextInt(4) == 0)
				currentIndex = 5;
			else
				currentIndex = 4;
			
		}
		
		return (TextureAtlas) getAsset(filePaths[currentIndex], "TextureAtlas");
	}
	
	//STATIC METHODS----------------------------
	/**
	 * I spawn a Gem that is not active and return it
	 * 
	 */
	public static GemObject spawnRandomGem(List<GemObject> aGemObjectList, float xPos, float yPos, float groundPosition, boolean isBadGem) {
		GemObject aGemObject = null;
		
		//only spawn gem if it is not active
		for (int i = 0; i < aGemObjectList.size(); i++) {
			if (aGemObjectList.get(i) != null && !aGemObjectList.get(i).getIsActive()) {
				aGemObjectList.get(i).spawn(xPos, yPos, isBadGem);
				aGemObjectList.get(i).setGroundPosition(groundPosition);
				aGemObject = aGemObjectList.get(i);
				break;
			}
		}
		return aGemObject;
	}
	
	/**
	 * I check if all of the gem objects are loaded into memory
	 * 
	 * @return
	 */
	public static boolean gemsLoaded(List<GemObject> aGemObjectList) {
		boolean isLoaded = true;
		
		for (int i = 0; i < aGemObjectList.size(); i++) {
			if (aGemObjectList.get(i) != null && !aGemObjectList.get(i).isLoaded())
				isLoaded = false;
		}
		
		return isLoaded;
	}
	
	/**
	 * I draw all gem objects that are active
	 * 
	 * @param aGemObjectList
	 */
	public static void drawGemObjects(List<GemObject> aGemObjectList, SpriteBatch batch) {
		
		for (int i = 0; i < aGemObjectList.size(); i++) {
			if (aGemObjectList.get(i) != null && aGemObjectList.get(i).getIsActive())
				aGemObjectList.get(i).draw(batch);
		}
		
	}
	
	/**
	 * I return the total value of gems collected by the object
	 * 
	 * @param aGemObjectList
	 */
	public static int collectGemsTouched (List<GemObject> aGemObjectList, AbstractSpikeQuestSpriteObject anObject) {
		int total = 0;
		
		for (int i = 0; i < aGemObjectList.size(); i++) {
			if (aGemObjectList.get(i) != null && aGemObjectList.get(i).getIsActive() 
					&& aGemObjectList.get(i).getCollisionRectangle() != null 
						&& anObject.getCollisionRectangle().contains(aGemObjectList.get(i).getCenterX(), aGemObjectList.get(i).getCenterY()))
						
				total += aGemObjectList.get(i).collectGem();
		}
		
		return total;
		
	}
	//-------------------------------------
	
	@Override
	public void discard () {
		
		aCollectionSoundEffect = null;
		super.discard();
		
	}

	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);
	}
}
