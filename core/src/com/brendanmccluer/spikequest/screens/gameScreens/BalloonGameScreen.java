package com.brendanmccluer.spikequest.screens.gameScreens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.brendanmccluer.spikequest.SpikeQuestController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.common.objects.ScoreBoardObject;
import com.brendanmccluer.spikequest.common.objects.ScoreControlObject;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.BalloonObject;
import com.brendanmccluer.spikequest.objects.GemObject;
import com.brendanmccluer.spikequest.objects.SpikeObject;
import com.brendanmccluer.spikequest.objects.ponies.DerpyObject;
import com.brendanmccluer.spikequest.objects.ponies.PinkieObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;

public class BalloonGameScreen extends AbstractSpikeQuestScreen {
	private final float SLOT_SIZE = 120f;
	private final int SPIKE_REGULAR_BALLOON_POINTS = 100;
	private final int SPIKE_DISCORD_BALLOON_BEFORE_GROUND_POINTS = 50;
	private final int SPIKE_DISCORD_BALLOON_POINTS = 10;
	public final int FIRST_PLAY_POINTS_MAX = 2000;
	private final int TOTAL_BALLOONS = 15;
	private final int INCREASE_BALLOON_SCORE = 500;
	private final int INCREASE_POP_SCORE = 700;
	private final int LOWESET_POP_TIME = 200;
	private final int BACKDROP_WIDTH = 1630;
	private final int BACKDROP_HEIGHT = 1830;
	
	private boolean drawDerpy = true;
	
	private SpikeObject aSpikeObject = null;
	private PinkieObject aPinkieObject = null;
	private List<BalloonObject> aBalloonObjectList = null;
	private List<GemObject> aGemObjectList = null;
	private List slotList = null;
	private List slotIsFilled = null;
	private SpikeQuestController aController = null;
	private Random aRandomGenerator = null;
	private ScoreControlObject aScoreControl = null;
	private ScoreBoardObject aScoreBoardObject = null;
	private DerpyObject aDerpyObject = null;
	private SpikeQuestMusic backgroundMusic = null;
	
	private int numberOfBalloons = 5;
	private int moreBalloonPoints = INCREASE_BALLOON_SCORE;
	private int shorterPopPoints = INCREASE_POP_SCORE;
	private int popTimer = 1000;
	private int gameOverTimer = 240;
	private SpikeQuestSoundEffect spikeAlarm = null;
	
	public BalloonGameScreen(SpikeQuestGame game, String aScreenType) {
		super(game);
		setScreenType(aScreenType);
	}

	@Override
	public void initialize() {
		aSpikeObject = new SpikeObject();
		aPinkieObject = new PinkieObject();
		aBalloonObjectList = new ArrayList<>();
		aGemObjectList = new ArrayList<>();
		slotList = new ArrayList();
		slotIsFilled = new ArrayList();
		aController = new SpikeQuestController();
		aRandomGenerator = new Random();
		aScoreControl = new ScoreControlObject();
		aScoreBoardObject = new ScoreBoardObject();
		aDerpyObject = new DerpyObject();

		game.assetManager.setAsset(SpikeQuestStaticFilePaths.BALLOON_GAME_BACKDROP_PATH, "Texture");
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.BALLOON_GAME_MUSIC_PATH, "Music");
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.SPIKE_ALARM_SOUND_PATH, "Sound");
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.GREEN_BACKDROP_PATH, "Texture");

		game.bitmapFont = new BitmapFont();
		game.bitmapFont.scale(1.5f);

		gameCamera = new SpikeQuestCamera(1500, BACKDROP_WIDTH, BACKDROP_HEIGHT);
		createBalloonObjects(TOTAL_BALLOONS);
		createGemObjects(TOTAL_BALLOONS+5);

		if (("firstPlay").equals(screenType))
			aDerpyObject = new DerpyObject();
	}

	@Override
	public void render(float delta) {
		
		refresh();
		useLoadingScreen(delta);
		if (game.assetManager.loadAssets() && aScoreBoardObject.isLoaded() && aSpikeObject.isLoaded()
				&& aPinkieObject.isLoaded() && (aDerpyObject == null || aDerpyObject.isLoaded())
				&& GemObject.gemsLoaded(aGemObjectList)) {
			
			gameCamera.attachToBatch(game.batch);
			
			if (!screenStart) {
				
				screenStart = true;
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.BALLOON_GAME_BACKDROP_PATH, "Texture");
				
				backgroundMusic = new SpikeQuestMusic((Music) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.BALLOON_GAME_MUSIC_PATH, "Music"));
				backgroundMusic.playMusic(true);
				
				spikeAlarm = new SpikeQuestSoundEffect((Sound) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.SPIKE_ALARM_SOUND_PATH, "Sound"), 10);
				
				aSpikeObject.spawn(gameCamera.getCameraWidth()/2, 0);
				
				//spawn to create sprite
				aScoreBoardObject.spawn(10,0);
				
				//relocate
				aScoreBoardObject.setCurrentPositionY(gameCamera.getCameraHeight() - aScoreBoardObject.getCollisionRectangle().getHeight() - 10);
				
				
				
				createSlots();
				
				if (aDerpyObject != null) {
					aDerpyObject.spawn(-200, gameCamera.getCameraHeight() - 200);
					//aDerpyObject.resize();
					aDerpyObject.setBanner("Get 2,000 Points!");
					aDerpyObject.setBannerTextSize(2);
					
				}	
				
				
			}
			
			if (gameOverTimer <= 0) {
				gameOver();
				return;
			}
				
			else {
				
				//check Gems
				aScoreBoardObject.addGems(GemObject.collectGemsTouched(aGemObjectList, aSpikeObject));

				game.batch.begin();
				game.batch.draw(currentBackdropTexture, 0, 0);
				
				//draw the score and gems
				aScoreBoardObject.setCurrentPositionX(gameCamera.getCameraPositionX() 
						- gameCamera.getCameraWidth()/2 + 10);
				aScoreBoardObject.draw(game.batch);
				
				if (aDerpyObject != null) {
					controlDerpy();
					
					if (drawDerpy)
						aDerpyObject.draw(game.batch);
				}
				
				//check game over
				if (aScoreBoardObject.getLives() <= 0 || (("firstPlay").equalsIgnoreCase(screenType) && aScoreBoardObject.getScore() >= FIRST_PLAY_POINTS_MAX)) {
					
					gameOverTimer--;
					aSpikeObject.standStill();
					
				}
				//check first play goal
				else {
					
					aController.controlListener(aSpikeObject, gameCamera, aSpikeObject.SPIKE_STANDARD_SPEED);
					adjustDifficulty();
					
					//control and draw balloons
					controlBalloons(numberOfBalloons);
					
					GemObject.drawGemObjects(aGemObjectList, game.batch);
				}
				
				//Make Spike Blink if game over
				if (gameOverTimer % 4 == 0)
					aSpikeObject.draw(game.batch);
				
				aScoreControl.draw(game.batch);
				
				//check if spike was hit
				if (spikeAlarm.isPlaying()) {
					//continue to play sound until done
					spikeAlarm.playSound(false);
					game.batch.draw(((Texture)game.assetManager.loadAsset(SpikeQuestStaticFilePaths.GREEN_BACKDROP_PATH, "Texture")),0,0);
				}
					
				game.batch.end();
			}	
		}
		

	}
	

	/**
	 * I control Derpy
	 * 
	 */
	private void controlDerpy() {
		boolean isGameOver = aScoreBoardObject.getScore() >= FIRST_PLAY_POINTS_MAX || aScoreBoardObject.getLives() <= 0; 
		
		
		if (isGameOver) {
			
			if (!drawDerpy) {
				
				aDerpyObject.spawn(BACKDROP_WIDTH + 25, aDerpyObject.getCurrentPositionY());
				drawDerpy = true;
			}
			
			 if (aScoreBoardObject.getScore() >= FIRST_PLAY_POINTS_MAX) {
				 aDerpyObject.setBanner("SUCCESS!");
				 aDerpyObject.moveLeft(3);
			 }
			 
			 else if (aScoreBoardObject.getLives() <= 0) {
				 aDerpyObject.setBanner("Try Again");
				 aDerpyObject.moveLeft(3);
			 }
		}
		
		else if (aDerpyObject.getCurrentPositionX() < BACKDROP_WIDTH + 500)
			aDerpyObject.moveRight(3);
		
		else 
			drawDerpy = false;
		
		
	}

	/**
	 * I adjust the difficulty of the game by increasing number of balloons
	 * and decreasing the time in which they pop
	 */
	private void adjustDifficulty() {
		
		if (numberOfBalloons < TOTAL_BALLOONS && aScoreBoardObject.getScore() >= moreBalloonPoints) {
			numberOfBalloons++;
			moreBalloonPoints += INCREASE_BALLOON_SCORE;
		}
		
		if (popTimer > LOWESET_POP_TIME && aScoreBoardObject.getScore() >= shorterPopPoints) {
			popTimer -= 100;
			shorterPopPoints += INCREASE_POP_SCORE;
		}
		
	}

	private void gameOver() {
		/*game.batch.begin();
		game.bitmapFont.drawMultiLine(game.batch, "\n\nGAME OVER\n\n" + "Total Points: " + spikePoints + "\n\nPress \"s\" to play again.", aSpikeQuestCamera.getCameraPositionX()-100, aSpikeQuestCamera.getCameraPositionY()+300);
		game.batch.end();
		*/
		dispose();
		//if first play, check spikePoints
		if (!("firstPlay").equalsIgnoreCase(screenType) || aScoreBoardObject.getScore() >= FIRST_PLAY_POINTS_MAX) {
			SpikeQuestSaveFile.setBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_COMPLETE_KEY, true);
			SpikeQuestScreenManager.forwardScreen(this, new SaveScoreScreen(game, aScoreBoardObject.getScore(), aScoreBoardObject.getGems(), this), game);
		}
		else
			SpikeQuestScreenManager.forwardScreen(this, new BalloonGameScreen(game, "firstPlay"), game);
		
		
	}

	/**
	 * I set the slot array
	 */
	private void createSlots() {
		float slotPosition = SLOT_SIZE;
		
		do {
			slotList.add(slotPosition);
			slotIsFilled.add(false);
			
			slotPosition += SLOT_SIZE;
			
		}
		while (slotPosition < currentBackdropTexture.getWidth()); 
		
		
	}

	/**
	 * I control the number of balloons passed. If the balloons passed exceeds number in
	 * aBalloonObjectList, more will be spawned when they are loaded
	 * @param numberOfBalloons2
	 */
	private void controlBalloons(int numberOfBalloons) {
		//int newBalloons = numberOfBalloons - aBalloonObjectList.size()+1;
		
		
		//make more balloons
		/*if (newBalloons > 0) {
			createBalloonObjects(newBalloons);
		}*/
		
		for (int i = 0; i < numberOfBalloons; i++) {
			
			if (aBalloonObjectList.get(i).isLoaded()) {
				controlBalloon(aBalloonObjectList.get(i));
				
				if (aBalloonObjectList.get(i).isSpawned())
					aBalloonObjectList.get(i).draw(game.batch);
			}
		}
		
		
	}
	

	/**
	 * I create x amount of balloon objects
	 * 
	 * @param number
	 */
	private void createBalloonObjects(int number) {
		
		for (int i = 0; i < number; i++) {
			aBalloonObjectList.add(new BalloonObject());
		}
		
	}
	
	/**
	 * I create x amount of gem objects
	 * 
	 * @param number
	 */
	private void createGemObjects (int number) {
		
		for (int i = 0; i < number; i++) {
			aGemObjectList.add(new GemObject());
		}
		
	}
	
	/**
	 * I spawn x amount of balloons and add to aBalloonObjectList. (maximum numbers of balloons to spawn equals the size
	 * of aBalloonObjectList. Will create new BalloonObjects if number is larger than aBalloonObjectList
	 * 
	 * @param number
	 */
	/*private void spawnBalloonObjects() {
		
		for (int i = 0; i < aBalloonObjectList.size(); i++) {
			//spawn initially so all loading of the balloon is finished and no delays occur
			aBalloonObjectList.get(i).spawn(-500, -500, popTimer);
		}
		
	}
	*/

	private void spawnBalloon(BalloonObject aBalloonObject) {
		//float cameraHeight = aSpikeQuestCamera.getCameraHeight();
		//float cameraWidth = aSpikeQuestCamera.getCameraWidth();
		//float yPosition = 0;
		int slotPosition = aRandomGenerator.nextInt(slotList.size()); //pick random slot
		int neighborPosition = 1;
		int previousSlot = 0;
		int nextSlot = 0;
		
		//find unoccupied slot
		if (slotIsFilled.get(slotPosition).equals(true)) {
			
			while (neighborPosition < slotList.size()) {
				previousSlot = slotPosition - neighborPosition;
				nextSlot = slotPosition + neighborPosition;
					
				//check previous slot
				if (previousSlot >= 0 && slotIsFilled.get(previousSlot).equals(false)) {
					spawnInSlot(aBalloonObject, previousSlot, aRandomGenerator);
					break;
				}
				else if (nextSlot < slotList.size() && slotIsFilled.get(nextSlot).equals(false)) {
					spawnInSlot(aBalloonObject, nextSlot, aRandomGenerator);
					break;
				}
				neighborPosition++;
			}
		}
		else {
			spawnInSlot(aBalloonObject, slotPosition, aRandomGenerator);
		}
			
		/*aBalloonObject.spawn(aRandomGenerator.nextInt((int)(cameraWidth- 100) + 1) + 100, 
				aRandomGenerator.nextInt((int)(cameraHeight + 500 - cameraHeight + 300) + 1) + cameraHeight + 300);*/
		
		
		
		//readjust position around other balloons already spawned
		//for (int i=0; i < aBalloonObjectList.size(); i++) {
			
			//if (aBalloonObjectList.get(i).isSpawned()) {
				
				//if (aBalloonObject)
			//}
		//}
	}
	
	private void spawnInSlot (BalloonObject aBalloonObject, int slotPosition, Random aRandomGenerator) {
		float cameraHeight = gameCamera.getCameraHeight();
		
		aBalloonObject.spawn(Float.parseFloat(slotList.get(slotPosition).toString()), 
				aRandomGenerator.nextInt((int)(cameraHeight + 500 - cameraHeight + 300) + 1) + 
					cameraHeight + 300, popTimer);
		
		aBalloonObject.setGroundPosition(0);
		slotIsFilled.set(slotPosition, true);
	}
	
	/**
	 * I control an individual BalloonObject
	 * @param aBalloonObject
	 */
	private void controlBalloon(BalloonObject aBalloonObject) {
		
		if (aBalloonObject.isPopped()) {
			
			//bad balloon popped on its own
			if (aBalloonObject.isBadPop()) {
				spikeAlarm.playSound(true);
				aScoreBoardObject.subtractLife();
			}
			
			eraseSlot(aBalloonObject);
		
			//will continue to spawn the balloon until slot is open
			spawnBalloon(aBalloonObject);
			
			
			
		}
		
		//check if hit so no double points are added 
		if (!aBalloonObject.isHit() && (aSpikeObject.isJumping() && aBalloonObject.getCollisionRectangle().overlaps(aSpikeObject.getCollisionHeadRectangle()))) {

			if (aBalloonObject.isRegularBalloon()) {
				//Add points if regular balloon. Balloon will pop after passing area passed
				aBalloonObject.hitOffScreen();
				addPoints(SPIKE_REGULAR_BALLOON_POINTS, aBalloonObject);
			}
			
			else {
				aScoreBoardObject.subtractLife();
				spikeAlarm.playSound(true);
				aBalloonObject.pop(true);
			}
				
			
			
		}
		
		if (aBalloonObject.getCollisionRectangle().overlaps(aSpikeObject.getFireCollisionZone())) {
			
			aBalloonObject.pop(true);
			
			if (!aBalloonObject.isRegularBalloon()) {
				
				//add extra points if balloon has not hit the ground
				if (aBalloonObject.getCurrentPositionY() > 0)
					addPoints(SPIKE_DISCORD_BALLOON_BEFORE_GROUND_POINTS, aBalloonObject);
				else
					addPoints(SPIKE_DISCORD_BALLOON_POINTS, aBalloonObject);
				
				//spawn gem (50% chance)
				if (aRandomGenerator.nextInt(2) == 0)
					GemObject.spawnRandomGem(aGemObjectList, aBalloonObject.getCurrentPositionX(), aBalloonObject.getCenterY(), 0, false);
			}
			else {
				//addPoints(-SPIKE_DISCORD_BALLOON_POINTS, aBalloonObject);
				//spawn bad gem
				GemObject.spawnRandomGem(aGemObjectList, aBalloonObject.getCurrentPositionX(), aBalloonObject.getCenterY(), 0, true);
			}
			
		}
			
	}

	private void addPoints(int points, BalloonObject aBalloonObject) {
		aScoreBoardObject.addScore(points);
		aScoreControl.createNewScore(points, aBalloonObject.getCurrentPositionX(), aBalloonObject.getCurrentPositionY()+100);
	}

	private void eraseSlot(BalloonObject aBalloonObject) {
		
		if (slotList.indexOf(aBalloonObject.getCurrentPositionX()) != -1)
			slotIsFilled.set(slotList.indexOf(aBalloonObject.getCurrentPositionX()), false);
	}
	
	@Override
	public void dispose () {
		//game.assetManager.disposeAsset(SpikeQuestStaticFilePaths.BALLOON_GAME_BACKDROP_PATH);
		//game.assetManager.disposeAsset(SpikeQuestStaticFilePaths.BALLOON_GAME_MUSIC_PATH);
		//game.assetManager.disposeAsset(SpikeQuestStaticFilePaths.GREEN_BACKDROP_PATH);
		game.assetManager.disposeAllAssets();
		
		backgroundMusic.stopMusic();
		backgroundMusic.discard();
		backgroundMusic = null;
		
		for (int i=0; i < aBalloonObjectList.size(); i++) {
			aBalloonObjectList.get(i).discard();
			aBalloonObjectList.set(i, null);
			
		}
		
		aBalloonObjectList.clear();
		aBalloonObjectList = null;
		
		slotList.clear();
		slotList = null;
		slotIsFilled.clear();
		slotIsFilled = null;
		
		for (GemObject i : aGemObjectList)
			i.discard();
		
		aGemObjectList.clear();
		aGemObjectList = null;
		
		slotList = null;
		slotIsFilled = null;
		
		aController = null;
		aPinkieObject.discard();
		aPinkieObject = null;
		aSpikeObject.discard();
		aSpikeObject = null;
		aRandomGenerator = null;
		gameCamera.discard();
		gameCamera = null;
		
		if (aDerpyObject != null) {
			aDerpyObject.discard();
			aDerpyObject = null;
		}
		
		spikeAlarm.discard();
		spikeAlarm = null;
		
		super.dispose();
	}
	
}

	//private boolean spikeCollideWithBalloon(BalloonObject aBalloonObject) {
		/*float aSpikeObjectHead = spikeObject.getCurrentPositionY() + 100;
		float aBalloonObjectBottom = aBalloonObject.getCurrentPositionY() - 20;
		float aBalloonObjectHead = aBalloonObject.getCurrentPositionY() + 20;
		
		return (aSpikeObjectHead >= aBalloonObjectBottom &&
				aSpikeObjectHead < aBalloonObjectHead &&
				aBalloonObject.getCurrentPositionX() > spikeObject.getCurrentPositionX()-50 &&
						aBalloonObject.getCurrentPositionX() <= spikeObject.getCurrentPositionX() + 50);
		*/
		
		
	//}


