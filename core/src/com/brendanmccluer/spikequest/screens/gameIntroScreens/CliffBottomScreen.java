package com.brendanmccluer.spikequest.screens.gameIntroScreens;

import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.common.objects.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.SpikeObject;
import com.brendanmccluer.spikequest.objects.WagonObject;
import com.brendanmccluer.spikequest.objects.ponies.PinkieObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;

public class CliffBottomScreen extends AbstractSpikeQuestScreen {
	private SpikeObject aSpikeObject = new SpikeObject();
	private PinkieObject aPinkieObject = new PinkieObject();
	private WagonObject aWagonObject = new WagonObject();
	private float wagonLandingPosition = -200;
	private float cameraShake = 50;
	private SpikeQuestSoundEffect aCrashSoundEffect = null;
	private SpikeQuestSoundEffect aScreamSoundEffect = null;
	private SpikeQuestTextBalloon aSpikeTextBalloon = new SpikeQuestTextBalloon("dialog/cliffBottom.txt");
	private SpikeQuestTextBalloon aPinkieTextBalloon = new SpikeQuestTextBalloon("dialog/cliffBottom.txt");
	private SpikeQuestDialogController aDialogController = new SpikeQuestDialogController (aPinkieObject, aPinkieTextBalloon, "Pinkie", 4, aSpikeObject, aSpikeTextBalloon, "Spike", 4);
	
	public CliffBottomScreen(SpikeQuestGame game, SpikeQuestSoundEffect aCrashSoundEffect, SpikeQuestSoundEffect aScreamSoundEffect) {
		super(game);
		
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.CLIFF_BOTTOM_SCREEN_BACKDROP_PATH, "Texture");
		gameCamera = new SpikeQuestCamera(1300, 1355, 762);
		this.aCrashSoundEffect = aCrashSoundEffect;
		this.aScreamSoundEffect = aScreamSoundEffect;
		
	}

	@Override
	public void render(float delta) {
		
		refresh();
		if (game.assetManager.loadAssets() && aPinkieTextBalloon.isLoaded() && aSpikeTextBalloon.isLoaded() && aWagonObject.isLoaded() && aSpikeObject.isLoaded() && aPinkieObject.isLoaded()) {
			
			gameCamera.attachToBatch(game.batch);
			if (!screenStart) {
				spawnObjects();
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.CLIFF_BOTTOM_SCREEN_BACKDROP_PATH, "Texture");
				screenStart = true;
				aDialogController.dialogEnabled = false;
			}
			
			//each of these events happen left to right
			if (makeWagonFall() && shakeCamera() && moveSpikeNextToWagon() && movePinkieNextToSpike()) {
				aDialogController.dialogEnabled = true;
				aDialogController.setTextBalloonDefaultPositionsOverObjects(aPinkieObject, aSpikeObject);
				
				//screen end
				if (aDialogController.secondTextIndex == 5) {
					dispose();
					SpikeQuestScreenManager.setNextScreen(this, game);
					return;
				}
				
				//flip Spike before he talks
				if (aSpikeObject.getIsFacingRight()) 
					aSpikeObject.moveLeft(0);
				
			}
			
			game.batch.begin();
				game.batch.draw(currentBackdropTexture, 0, 0);
				aDialogController.drawTheDialogAndAnimateObjects(game.batch, aPinkieObject, aSpikeObject);
				aWagonObject.draw(game.batch);
				aPinkieObject.draw(game.batch);
				aSpikeObject.draw(game.batch);
				
				/*if (aDialogController.drawSecondTextFlag) 
					aSpikeTextBalloon.drawDialog(game.batch, aSpikeObject.getCurrentPositionX()-150, aSpikeObject.getCurrentPositionY() + 200);
				*/
				/*if (aDialogController.drawFirstTextFlag) 
					aPinkieTextBalloon.drawDialog(game.batch, aPinkieObject.getCurrentPositionX()-80, aPinkieObject.getCurrentPositionY() + 300);
				 */
				
			game.batch.end();
		}
	}

	private boolean movePinkieNextToSpike() {
		if (aPinkieObject.getCurrentPositionX() < aSpikeObject.getCurrentPositionX() - 200) {
			aPinkieObject.moveRight(5);
			aSpikeObject.standStill();
			return false;
		}
		return true;
	}

	/**
	 * I control spike and return true when
	 * object is ready for next part of the scene
	 * @return
	 */
	private boolean moveSpikeNextToWagon() {
		if (aSpikeObject.getCurrentPositionX() < aWagonObject.getCurrentPositionX() - 200) {
			aSpikeObject.moveRight(10);
			return false;
		}
		return true;
		
	}
	
	/**
	 * I set the objects
	 */
	private void spawnObjects() {
		aSpikeObject.spawn(-300, 0);
		aPinkieObject.spawn(-300,0);
		aWagonObject.spawn(gameCamera.getCameraWidth()/2, 800);
		
		//flip wagon and fall
		aWagonObject.rotate(180);
		aWagonObject.setGroundPosition(wagonLandingPosition);
		aWagonObject.setGravity(3);
		aCrashSoundEffect.playSound(false);
	}
	
	/**
	 * I make the wagon fall, shake the camera, and return 
	 * if done falling
	 * @return
	 */
	private boolean makeWagonFall() {
		if (aWagonObject.getCurrentPositionY() == wagonLandingPosition) {
			return true;
		}
		else {
			
			aWagonObject.standStill();
			return false;
		}
	}
	
	/**
	 * I shake the camera
	 */
	private boolean shakeCamera() {
		//shake the camera until certain point
		if (Math.abs(cameraShake) > 0) {
			gameCamera.translateCamera(cameraShake, 0);
			cameraShake *= -1;
			
			//add or subtract depending on negative or positve
			if (cameraShake < 0) {
				cameraShake++;
			}
			else {
				cameraShake--;
			}
			return false;
			
		}
		
		return true;
	
	}
	
	@Override
	public void dispose() {
		aCrashSoundEffect.discard();
		aCrashSoundEffect = null;
		aScreamSoundEffect.discard();
		aScreamSoundEffect = null;
		aDialogController = null;
		aPinkieObject.discard();
		aPinkieObject = null;
		aSpikeObject.discard();
		aSpikeObject = null;
		aPinkieTextBalloon.discard();
		aPinkieTextBalloon = null;
		aSpikeTextBalloon.discard();
		aSpikeTextBalloon = null;
		aWagonObject.discard();
		aWagonObject = null;
		game.assetManager.disposeAllAssets();
	}
}
