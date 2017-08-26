package com.brendanmccluer.spikequest.screens.gameIntroScreens;

import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.HatObject;
import com.brendanmccluer.spikequest.objects.SpikeObject;
import com.brendanmccluer.spikequest.objects.WagonObject;
import com.brendanmccluer.spikequest.objects.ponies.PinkieObject;
import com.brendanmccluer.spikequest.objects.ponies.RarityObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;

public class PonyvilleSlopeScreen extends AbstractSpikeQuestScreen {
	private SpikeObject aSpikeObject = null;
	private PinkieObject aPinkieObject = null;
	private WagonObject aWagonObject = null;
	private RarityObject aRarityObject = null;
	private HatObject aPinkHatObject = null;
	private HatObject aBlueHatObject = null;
	private SpikeQuestDialogController aRaritySpikeDialogController, aPinkieSpikeDialogController = null;
	private float wagonSpeed = 0.3f;
	private boolean soundPlayed = false;

	/**
	 * I come after ponyville 
	 * start. Pass the game, music, spikeObject, and Camera
	 * @param game
	 * @param backgroundMusic
	 * @param aSpikeObject
	 * @param aSpikeCamera
	 */
	public PonyvilleSlopeScreen(SpikeQuestGame game, SpikeQuestMusic backgroundMusic, SpikeObject aSpikeObject, SpikeQuestCamera aSpikeCamera) {
		super(game);
        //position the camera up a little for the slope
        aSpikeCamera.setPositionBottomLeft();
        aSpikeCamera.setPosition(900, 390);
        aSpikeCamera.setCameraSize(20);
        this.backgroundMusic = backgroundMusic;
        this.aSpikeObject = aSpikeObject;
        this.gameCamera = aSpikeCamera;
	}

	@Override
	public void show() {
        aPinkieObject = new PinkieObject();
        aWagonObject = new WagonObject();
        aRarityObject = new RarityObject();
        aPinkHatObject = new HatObject();
        aBlueHatObject = new HatObject();

        aRarityObject.loadSounds("spikeyWikey");

        game.assetManager.setAsset("backdrop/slope.png", "Texture");

        aRaritySpikeDialogController = new SpikeQuestDialogController(aRarityObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SLOPE_SCREEN_RARITY_SPIKE_DIALOG_PATH), "Rarity", 2,
                aSpikeObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SLOPE_SCREEN_RARITY_SPIKE_DIALOG_PATH), "Spike", 1);
        aPinkieSpikeDialogController = new SpikeQuestDialogController(aPinkieObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SLOPE_SCREEN_PINKIE_SPIKE_DIALOG_PATH), "Pinkie", 1,
                aSpikeObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SLOPE_SCREEN_PINKIE_SPIKE_DIALOG_PATH), "Spike", 1);
	}

	@Override
	public void render(float delta) {
		//clear the screen
		refresh();
		useLoadingScreen(delta);
		
		if (game.assetManager.loadAssets() && aRarityObject.isLoaded() && aPinkieObject.isLoaded() && aWagonObject.isLoaded() && aPinkieSpikeDialogController.areTextBalloonsLoaded()
				&& aRaritySpikeDialogController.areTextBalloonsLoaded() && aBlueHatObject.isLoaded() && aPinkHatObject.isLoaded()) {
			
			if (!screenStart) {
				
				//will set objects loaded when true
				setObjects();
				screenStart = true;
			}
			else {
				gameCamera.attachToBatch(game.batch);
				
				if (aSpikeObject.getCurrentPositionX() < gameCamera.getCameraPositionX()) {
					aSpikeObject.moveRight(5);
					aSpikeObject.setGroundPosition(aSpikeObject.getGroundPosition() - 0.02f);
					//control wagon
					aWagonObject.moveRight(5);
					aWagonObject.setGroundPosition(aWagonObject.getGroundPosition() - 0.02f);
				}
				else {
					
					//Start Scene
					if (!aRaritySpikeDialogController.areTextBalloonsFinished()) {
						
						if (!soundPlayed) {
							aRarityObject.playSoundEffect("spikeyWikey");
							soundPlayed = true;
						}
						
						//Rarity runs to Spike
						if (aRarityObject.getCurrentPositionX() < aSpikeObject.getCurrentPositionX() - 200) {
							
							aRarityObject.moveRight(10);
							aPinkHatObject.moveRight(10);
							aBlueHatObject.moveRight(10);
							aSpikeObject.standStill();
						}
						
						else {
							//Spike faces Rarity
							if (aSpikeObject.getIsFacingRight())
								aSpikeObject.moveLeft(0);
						
							aRaritySpikeDialogController.dialogEnabled = true;
							aRaritySpikeDialogController.setTextBalloonDefaultPositionsOverObjects(aRarityObject, aSpikeObject);
							
						}
					
						
					//dialogListener();
					}
					
					else if (aRarityObject.getCurrentPositionX() <= -100) {
						
						//Move Pinkie
						if (aPinkieObject.getCurrentPositionX() < (aSpikeObject.getCurrentPositionX()-200)) {
							
							aPinkieObject.moveRight(5);
							aSpikeObject.standStill(); //make Spike Stop Talking
							//aRarityObject.standStill();
						}
						else if (aPinkieSpikeDialogController.dialogEnabled == false){
							//enable next dialog
							aPinkieSpikeDialogController.dialogEnabled = true;
							aPinkieSpikeDialogController.setTextBalloonDefaultPositionsOverObjects(aPinkieObject, aSpikeObject);
							
						}
					}
					
					//Move Rarity offscreen and shut off dialog
					else {
						
						aRaritySpikeDialogController.dialogEnabled = false;
						//aRarityObject.setUsingMagic(false);
						aRarityObject.moveLeft(5);
						aPinkHatObject.moveLeft(5);
						aBlueHatObject.moveLeft(5);
					}
						
					//Move Spike before screen change				
					if (aPinkieSpikeDialogController.firstTextIndex == 2 && !aSpikeObject.getIsFacingRight())
						aSpikeObject.moveRight(0);
					
					//Wagon begins to roll away
					if (aRaritySpikeDialogController.firstTextIndex > 1 && aWagonObject.getCurrentPositionX() < game.GAME_SCREEN_WIDTH + 500) {
						aWagonObject.moveRight(wagonSpeed);
						aWagonObject.setGroundPosition(aWagonObject.getGroundPosition() - 0.25f);
						wagonSpeed += 0.05;
					}
					else {
						aWagonObject.standStill();
					}
					
				
				}//end else for Spike in Position
	
				draw();
				
				//set next screen
				if (aPinkieSpikeDialogController.areTextBalloonsFinished()) {
					backgroundMusic.stopMusic();
					dispose();
					SpikeQuestScreenManager.forwardScreen(this, new CliffScreen(game), game);
				}
			
			}
		}
	}

	private void setObjects() {
		
		currentBackdropTexture = (Texture) game.assetManager.loadAsset("backdrop/slope.png", "Texture");
		aSpikeObject.spawn(0, 100);
		aPinkieObject.spawn(0, 100);
		aRarityObject.spawn(0, 100);
		aPinkHatObject.spawn(aRarityObject.getCollisionRectangle().getWidth(), aRarityObject.getCollisionRectangle().getHeight()); //pink hat right of Rarity
		aBlueHatObject.spawn(0 - aRarityObject.getCollisionRectangle().getWidth(), aRarityObject.getCollisionRectangle().getHeight()); //blue hat left of Rarity
		
		aWagonObject.spawn(aSpikeObject.getCurrentPositionX()-150, aSpikeObject.getCurrentPositionY());
		
		//turn off until ready
		aPinkieSpikeDialogController.dialogEnabled = false;
		aRaritySpikeDialogController.dialogEnabled = false;
		
		//set Blue hat to blue (default pink)
		aBlueHatObject.setHatBlue();
		
		//set Rarity to using magic
		aRarityObject.setUsingMagic(true);
		
	}

	private void draw() {
		
		game.batch.begin();
		game.batch.draw(currentBackdropTexture,0,0);
		
		aWagonObject.draw(game.batch);
		aBlueHatObject.draw(game.batch);
		aSpikeObject.draw(game.batch);
		aRarityObject.draw(game.batch);
		aPinkHatObject.draw(game.batch);
		
		aPinkieObject.draw(game.batch);
		
		aPinkieSpikeDialogController.drawTheDialogAndAnimateObjects(game.batch, aPinkieObject, aSpikeObject);
		aRaritySpikeDialogController.drawTheDialogAndAnimateObjects(game.batch, aRarityObject, aSpikeObject);
		game.batch.end();
		
	}
	
	@Override
	public void dispose () {
		
		aPinkieSpikeDialogController.dispose();
		aRaritySpikeDialogController.dispose();
		aWagonObject.dispose();
		aRarityObject.dispose();
		aPinkieObject.dispose();
		aSpikeObject.dispose();
		gameCamera.discard();
		aBlueHatObject.dispose();
		aPinkHatObject.dispose();
		backgroundMusic.dispose();
		
		aBlueHatObject = null;
		aPinkHatObject = null;
		aWagonObject = null;
		aPinkieSpikeDialogController = null;
		aRaritySpikeDialogController = null;
		aPinkieObject = null;
		aRarityObject = null;
		aSpikeObject = null;
		gameCamera = null;
		backgroundMusic = null;
	}

}
