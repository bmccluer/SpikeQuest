package com.brendanmccluer.spikequest.screens.gameIntroScreens;

import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.SpikeObject;
import com.brendanmccluer.spikequest.objects.WagonObject;
import com.brendanmccluer.spikequest.objects.ponies.PinkieObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;

public class CliffBottomScreen extends AbstractSpikeQuestScreen {
	private SpikeObject spikeObject = null;
	private PinkieObject pinkieObject = null;
	private WagonObject wagonObject = null;
	private float wagonLandingPosition = -200;
	private float cameraShake = 50;
	private SpikeQuestSoundEffect crashSoundEffect, screamSoundEffect = null;
	private SpikeQuestTextBalloon spikeQuestTextBalloon, pinkieTextBalloon = null;
	private SpikeQuestDialogController dialogController = null;
	
	public CliffBottomScreen(SpikeQuestGame game, SpikeQuestSoundEffect aCrashSoundEffect, SpikeQuestSoundEffect aScreamSoundEffect) {
		super(game);

		game.assetManager.setAsset(SpikeQuestStaticFilePaths.CLIFF_BOTTOM_SCREEN_BACKDROP_PATH, "Texture");
		gameCamera = new SpikeQuestCamera(1300, 1355, 762);
		this.crashSoundEffect = aCrashSoundEffect;
		this.screamSoundEffect = aScreamSoundEffect;
	}

	@Override
	public void initialize() {
		spikeObject = new SpikeObject();
		pinkieObject = new PinkieObject();
		wagonObject = new WagonObject();
		spikeQuestTextBalloon = new SpikeQuestTextBalloon("dialog/cliffBottom.txt");
		pinkieTextBalloon = new SpikeQuestTextBalloon("dialog/cliffBottom.txt");
		dialogController = new SpikeQuestDialogController (pinkieObject, pinkieTextBalloon, "Pinkie", 4, spikeObject, spikeQuestTextBalloon, "Spike", 4);
	}

	@Override
	public void render(float delta) {
		
		refresh();
		if (game.assetManager.loadAssets() && pinkieTextBalloon.isLoaded() && spikeQuestTextBalloon.isLoaded() && wagonObject.isLoaded() && spikeObject.isLoaded() && pinkieObject.isLoaded()) {
			
			gameCamera.attachToBatch(game.batch);
			if (!screenStart) {
				spawnObjects();
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.CLIFF_BOTTOM_SCREEN_BACKDROP_PATH, "Texture");
				screenStart = true;
				dialogController.dialogEnabled = false;
			}
			
			//each of these events happen left to right
			if (makeWagonFall() && shakeCamera() && moveSpikeNextToWagon() && movePinkieNextToSpike()) {
				dialogController.dialogEnabled = true;
				dialogController.setTextBalloonDefaultPositionsOverObjects(pinkieObject, spikeObject);
				
				//screen end
				if (dialogController.secondTextIndex == 5) {
					dispose();
					SpikeQuestScreenManager.popNextScreen(this, game);
					return;
				}
				
				//flip Spike before he talks
				if (spikeObject.getIsFacingRight())
					spikeObject.moveLeft(0);
				
			}
			
			game.batch.begin();
				game.batch.draw(currentBackdropTexture, 0, 0);
				dialogController.drawTheDialogAndAnimateObjects(game.batch, pinkieObject, spikeObject);
				wagonObject.draw(game.batch);
				pinkieObject.draw(game.batch);
				spikeObject.draw(game.batch);
				
				/*if (dialogController.drawSecondTextFlag)
					spikeQuestTextBalloon.drawDialog(game.batch, spikeObject.getCurrentPositionX()-150, spikeObject.getCurrentPositionY() + 200);
				*/
				/*if (dialogController.drawFirstTextFlag)
					pinkieTextBalloon.drawDialog(game.batch, pinkieObject.getCurrentPositionX()-80, pinkieObject.getCurrentPositionY() + 300);
				 */
				
			game.batch.end();
		}
	}

	private boolean movePinkieNextToSpike() {
		if (pinkieObject.getCurrentPositionX() < spikeObject.getCurrentPositionX() - 200) {
			pinkieObject.moveRight(5);
			spikeObject.standStill();
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
		if (spikeObject.getCurrentPositionX() < wagonObject.getCurrentPositionX() - 200) {
			spikeObject.moveRight(10);
			return false;
		}
		return true;
		
	}
	
	/**
	 * I set the objects
	 */
	private void spawnObjects() {
		spikeObject.spawn(-300, 0);
		pinkieObject.spawn(-300,0);
		wagonObject.spawn(gameCamera.getCameraWidth()/2, 800);
		
		//flip wagon and fall
		wagonObject.rotate(180);
		wagonObject.setGroundPosition(wagonLandingPosition);
		wagonObject.setGravity(3);
		crashSoundEffect.playSound(false);
	}
	
	/**
	 * I make the wagon fall, shake the camera, and return 
	 * if done falling
	 * @return
	 */
	private boolean makeWagonFall() {
		if (wagonObject.getCurrentPositionY() == wagonLandingPosition) {
			return true;
		}
		else {
			
			wagonObject.standStill();
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
		crashSoundEffect.discard();
		crashSoundEffect = null;
		screamSoundEffect.discard();
		screamSoundEffect = null;
		dialogController = null;
		pinkieObject.discard();
		pinkieObject = null;
		spikeObject.discard();
		spikeObject = null;
		pinkieTextBalloon.discard();
		pinkieTextBalloon = null;
		spikeQuestTextBalloon.discard();
		spikeQuestTextBalloon = null;
		wagonObject.discard();
		wagonObject = null;
		game.assetManager.disposeAllAssets();
	}
}
