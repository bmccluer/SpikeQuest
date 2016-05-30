package com.brendanmccluer.spikequest.screens.gameIntroScreens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.common.objects.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.SpikeObject;
import com.brendanmccluer.spikequest.objects.ponies.TwilightObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;

public class IntroScreen extends AbstractSpikeQuestScreen {
		private final int CAMERA_SIZE = 900;
		private final float OBJECT_SIZE_RATIO = 1.5f; //set objects to be twice their size
		
		private SpikeQuestCamera aSpikeQuestCamera = new SpikeQuestCamera(CAMERA_SIZE, game.GAME_SCREEN_WIDTH, game.GAME_SCREEN_WIDTH);;
		private TwilightObject aTwilightObject = new TwilightObject(); 
		private SpikeObject aSpikeObject = new SpikeObject();
		private SpikeQuestDialogController aDialogController = new SpikeQuestDialogController(aTwilightObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.INTRO_SCREEN_DIALOG_PATH), "Twilight", 4,
				aSpikeObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.INTRO_SCREEN_DIALOG_PATH), "Spike", 3);
		
		public IntroScreen (SpikeQuestGame game) {
			super(game);

			game.assetManager.setAsset(SpikeQuestStaticFilePaths.TWILIGHT_LIBRARY_BACKDROP_PATH, "Texture");
			game.assetManager.setAsset("music/ponyvilleMusic.mp3", "Music");
		}
		
		public void render (float delta) {
			//clear the screen
			refresh();
			useLoadingScreen(delta);
			
			if (game.assetManager.loadAssets() && aTwilightObject.isLoaded() && aSpikeObject.isLoaded() && aDialogController.areTextBalloonsLoaded()) { 
				
				aSpikeQuestCamera.attachToBatch(game.batch);
				//game start
				if (!screenStart) {
					
					screenStart = true;
					currentBackdropTexture = (Texture) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.TWILIGHT_LIBRARY_BACKDROP_PATH, "Texture");
					setObjects();
					//start music
					backgroundMusic = new SpikeQuestMusic((Music) game.assetManager.loadAsset("music/ponyvilleMusic.mp3", "Music"));
					backgroundMusic.playMusic(true);
				}
				else {
					//clear the screen
					refresh();
					
					
					game.batch.begin();
					game.batch.draw(currentBackdropTexture,0,0);
					aTwilightObject.draw(game.batch);
					aSpikeObject.draw(game.batch);
					aDialogController.drawTheDialogAndAnimateObjects(game.batch, aTwilightObject, aSpikeObject);
					game.batch.end();
					
					
					
					/*if (twilightTalking) {
						twilightTextBalloon.drawDialog(game.batch, aTwilightObject.getCurrentPositionX() - 200, aTwilightObject.getCurrentPositionY()+300);
						aTwilightObject.talk();
					}
					else {
						aTwilightObject.standStill();
					}
					if (spikeTalking && dialogIndex < 3)
						spikeTextBalloon.drawDialog(game.batch, aSpikeObject.getCurrentPositionX() + spikeBalloonPositionX, aSpikeObject.getCurrentPositionY()+300);
					
					else if (spikeTalking) 
						spikeTextBalloon.drawDialog(game.batch, aSpikeObject.getCurrentPositionX() + spikeBalloonPositionX + 50, aSpikeObject.getCurrentPositionY() + 300);*/
					
					
					controlObjects();
					
					
					
					/*//When spike stops dialog continues
					else if (!dialogScene && aDialogController.firstTextIndex == 3 && !aDialogController.drawFirstTextFlag){
						dialogScene = true;
						//twilightTalking = true;
						//twilightTalking = twilightTextBalloon.setNextDialog();
						aTwilightObject.moveRight(0);
						
						//reverse direction
						aSpikeObject.moveLeft(0);
						aSpikeObject.standStill();
						spikeBalloonPositionX = -300f;
						
					}*/
					
				
					/*//Check input
					if ((dialogScene) && Gdx.input.isKeyPressed(Keys.SPACE)) {
						
						//Start with Twilight
						twilightTalking = twilightTextBalloon.setNextDialog();
					
						if (!twilightTalking) {
							
							spikeTalking = spikeTextBalloon.setNextDialog();
							aSpikeObject.talk();
							
							if (!spikeTalking) {
								aSpikeObject.standStill();
								dialogIndex++;
								if (dialogIndex > 2) {
									spikeBalloonPositionX = 500;
								}
								
								twilightTextBalloon.loadDialog("Intro" + dialogIndex);
								spikeTextBalloon.loadDialog("Intro" + dialogIndex);
							}
							
						}
					}*/
					
				}
			}
		}
		
		
		private void controlObjects() {
			//start moving spike on scene three
			if (aDialogController.secondTextIndex == 3) {
				
				
				if (aSpikeObject.getCurrentPositionX() < aSpikeQuestCamera.getCameraWidth()-200) {
					
					//moving, disable dialog
					aDialogController.dialogEnabled = false;
					aSpikeObject.moveRight(5);
					
					//get twilight to stop talking
					aTwilightObject.standStill();
					
					//check again and set objects if true
					if (aSpikeObject.getCurrentPositionX() >= aSpikeQuestCamera.getCameraWidth()-200) {
						aTwilightObject.moveRight(0);
						aSpikeObject.moveLeft(0);
					}
				}
				
				else {
					
					//aSpikeObject.standStill();
					aDialogController.setSecondTextBalloonPositionX(aSpikeObject.getCurrentPositionX() - 25);
					aDialogController.dialogEnabled = true;
				}
				
			}
			else if (aDialogController.areTextBalloonsFinished()) {
				aSpikeObject.moveRight(5);
				aTwilightObject.standStill();
				aDialogController.dialogEnabled = false;
				
				if (aSpikeObject.getCurrentPositionX() > aSpikeQuestCamera.getCameraWidth()+500)
					setNextScreen();
			}
		}

		/**
		 * I move to the next screen
		 */
		private void setNextScreen() {
			dispose();
			SpikeQuestScreenManager.forwardScreen(this, new PonyvilleStartScreen(game), game);
		}

		/**
		 * I set the objects in place
		 */
		private void setObjects() {
			
			aSpikeObject.spawn(0, 0);
			aTwilightObject.spawn(aSpikeQuestCamera.getCameraWidth()/2 - 100, 0);
			aSpikeObject.resize(OBJECT_SIZE_RATIO);
			aTwilightObject.resize(OBJECT_SIZE_RATIO);
			aTwilightObject.moveLeft(0); //change rotation
			
			aDialogController.setTextBalloonDefaultPositionsOverObjects(aTwilightObject, aSpikeObject);
			aDialogController.setSecondTextBalloonPositionX(aDialogController.getSecondTextBalloonPositionX() + 150);
		}

		@Override
		public void dispose () {
			aSpikeObject.discard();
			aSpikeQuestCamera.discard();
			aTwilightObject.discard();
			aDialogController.discardTextBalloons();
			
			game.assetManager.disposeAsset(SpikeQuestStaticFilePaths.TWILIGHT_LIBRARY_BACKDROP_PATH);
			
			aSpikeObject = null;
			aSpikeQuestCamera = null;
			aTwilightObject = null;
			
			super.dispose();
		}
}
