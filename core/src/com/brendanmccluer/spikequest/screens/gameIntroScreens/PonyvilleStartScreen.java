package com.brendanmccluer.spikequest.screens.gameIntroScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.StandardObject;
import com.brendanmccluer.spikequest.objects.SpikeObject;
import com.brendanmccluer.spikequest.objects.WagonObject;
import com.brendanmccluer.spikequest.objects.ponies.DerpyObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;

public class PonyvilleStartScreen extends AbstractSpikeQuestScreen {
	private  int mapWidth = 2098;	//set to the size of the backdrop
	private  int mapHeight = 702; //set to the size of the backdrop
	private final int CAMERA_SIZE = 1000;
	
	private SpikeObject aSpikeObject = null;
	private StandardObject aPinkieObject = null;
	private SpikeQuestController aSpikeController = null;
	private DerpyObject aDerpyObject = null;
	private WagonObject aWagonObject = null;
	private float pinkieMinTalkX = 0;
	private float pinkieMaxTalkX = 0;
	//private boolean pinkieTalk = false;
	private boolean ponyville2 = false;
	private boolean pinkieActive = false;
	private boolean movingRight = true;
	//private int bits = 0;
	
	
	public PonyvilleStartScreen (SpikeQuestGame game) {
		super(game);
	}

	@Override
	public void show() {
        aSpikeObject = new SpikeObject();
        aSpikeController = new SpikeQuestController();
        aDerpyObject = new DerpyObject();
        aWagonObject = new WagonObject();

        gameCamera = new SpikeQuestCamera(CAMERA_SIZE, mapWidth, mapHeight);

        game.assetManager.setAsset("backdrop/ponyvilleBackdrop2.png", "Texture");
        game.assetManager.setAsset("backdrop/ponyvilleBackdrop.png", "Texture");
        game.assetManager.setAsset("music/ponyvilleMusic.mp3", "Music");
	}

	public void render(float delta) {
		
		//clear the screen
		refresh();
		if (game.assetManager.loadAssets() && aSpikeObject.isLoaded() && aDerpyObject.isLoaded() && aWagonObject.isLoaded()) { 
			
			gameCamera.attachToBatch(game.batch);
			
			boolean inPinkieTalkRange = (aSpikeObject.getCurrentPositionX() >= pinkieMinTalkX) && (aSpikeObject.getCurrentPositionX() <= pinkieMaxTalkX);
			
			//game start
			if (screenStart == false) {
				
				aDerpyObject.spawn(gameCamera.getCameraPositionX(), gameCamera.getCameraHeight()-150);
				aDerpyObject.resize(0.5f);
				
				aSpikeObject.spawn(gameCamera.getCameraPositionX(), gameCamera.getCameraPositionY() - gameCamera.getCameraHeight()/2);
				//aPinkieObject.spawn(mapWidth-500, aSpikeQuestCamera.getCameraPositionY() - aSpikeQuestCamera.getCameraHeight()/2);
				aWagonObject.spawn(aSpikeObject.getCurrentPositionX()-150, aSpikeObject.getCurrentPositionY());
				
				aSpikeController.canBreatheFire = false;
				aSpikeController.canJump = false;
				
				currentBackdropTexture = (Texture) game.assetManager.loadAsset("backdrop/ponyvilleBackdrop.png", "Texture");
				
				aDerpyObject.setBanner("Use arrow keys to move Left and Right");
				
				//Spawn spike in the middle of camera x and the bottom of the camera
				screenStart = true;
				backgroundMusic = new SpikeQuestMusic((Music) game.assetManager.loadAsset("music/ponyvilleMusic.mp3", "Music"));
				backgroundMusic.playMusic(true);
				
				//set range to talk to pinkie
				//pinkieMinTalkX = aPinkieObject.getCurrentPositionX() - 100;
				//pinkieMaxTalkX = aPinkieObject.getCurrentPositionX() + 100;
			}
			else {
			
				
				game.batch.begin();
				
				game.batch.draw(currentBackdropTexture, 0, 0);
				//game.bitmapFont.draw(game.batch,Integer.toString(bits), aSpikeQuestCamera.getCameraPositionX(), aSpikeQuestCamera.getCameraPositionY());
				
				if (pinkieActive)
					aPinkieObject.draw(game.batch);
				
				aDerpyObject.draw(game.batch);
				aWagonObject.draw(game.batch);
				aSpikeObject.draw(game.batch);
				
				//Make Pinkie talk if Spike is in the her X range and button press
				/*if (pinkieActive && inPinkieTalkRange && pinkieTalk) {
					aPinkieObject.pinkieTalk(game.batch);
				}
				else {
					pinkieTalk = false;
				}*/
				
				game.batch.end();
			
				//Control Spike and wagon
				aSpikeController.controlListener(aSpikeObject, gameCamera, aSpikeObject.SPIKE_STANDARD_SPEED);
				
				
				//control spike and wagon
				if (aSpikeObject.getCenterX() <= mapWidth && Gdx.input.isKeyPressed(Keys.RIGHT)) {
					aWagonObject.moveRight(aSpikeObject.SPIKE_STANDARD_SPEED);
					aWagonObject.setCurrentPositionX(aSpikeObject.getCurrentPositionX()-150);
					
				}
				else if (aSpikeObject.getCenterX() >= 0 && Gdx.input.isKeyPressed(Keys.LEFT)) {
					aWagonObject.setCurrentPositionX(aSpikeObject.getCurrentPositionX()+50);
					aWagonObject.moveLeft(aSpikeObject.SPIKE_STANDARD_SPEED);
				}
				else {
					aWagonObject.standStill();
				}
				
				//Control Derpy
				if (movingRight && aDerpyObject.getCurrentPositionX() < mapWidth) {
					aDerpyObject.moveRight(3);
				}
				else {
					movingRight = false;
				}
				
				if (!movingRight && aDerpyObject.getCurrentPositionX() > 0) {
					aDerpyObject.moveLeft(3);
				}
				else {
					movingRight = true;
				}
				
				
				//have pinkie talk on next frame
				if (inPinkieTalkRange && Gdx.input.isKeyPressed(Keys.SPACE)) {
					//pinkieTalk = true;
				}
				
				//aPinkieObject.standStill();
				
				//change background
				if (!ponyville2 && aSpikeObject.getCenterX() >= mapWidth) {
					currentBackdropTexture = (Texture) game.assetManager.loadAsset("backdrop/ponyvilleBackdrop2.png", "Texture");
					ponyville2 = true;
					
					//stop drawing pinkie
					pinkieActive = false;
					
					gameCamera.setPositionBottomLeft();
					aSpikeObject.setCurrentPositionX(gameCamera.getCameraPositionX()-500);
					
				}
				else if (ponyville2 && aSpikeObject.getCenterX() >= mapWidth) {
					dispose();
					SpikeQuestScreenManager.forwardScreen(this, new PonyvilleSlopeScreen (game, backgroundMusic, aSpikeObject, gameCamera), game);
				}
			}
		}
		//Still loading
		else {
			game.batch.begin();
			game.bitmapFont.draw(game.batch, "Loading...", mapWidth/2, mapHeight/2);
			game.batch.end();
		}
	}
	
	@Override
	public void dispose() {
		game.assetManager.disposeAsset("backdrop/ponyvilleBackdrop2.png");
		game.assetManager.disposeAsset("backdrop/ponyvilleBackdrop.png");
		aPinkieObject = null;
		
	}
	
}
