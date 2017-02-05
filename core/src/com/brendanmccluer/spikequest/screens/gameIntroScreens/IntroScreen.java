package com.brendanmccluer.spikequest.screens.gameIntroScreens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.SpikeObject;
import com.brendanmccluer.spikequest.objects.ponies.TwilightObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;

public class IntroScreen extends AbstractSpikeQuestScreen {
    private final int CAMERA_SIZE = 900;
    private final float OBJECT_SIZE_RATIO = 1.5f; //set objects to be twice their size
    private SpikeQuestCamera spikeQuestCamera = null;
    private TwilightObject twilightObject = null;
    private SpikeObject spikeObject = null;
    private SpikeQuestDialogController dialogController = null;

    public IntroScreen (SpikeQuestGame game) {
        super(game);
    }

    @Override
    public void initialize() {
        spikeQuestCamera = new SpikeQuestCamera(CAMERA_SIZE, game.GAME_SCREEN_WIDTH, game.GAME_SCREEN_WIDTH);;
        twilightObject = new TwilightObject();
        spikeObject = new SpikeObject();
        dialogController = new SpikeQuestDialogController(twilightObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.INTRO_SCREEN_DIALOG_PATH), "Twilight", 4,
                spikeObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.INTRO_SCREEN_DIALOG_PATH), "Spike", 3);
        game.assetManager.setAsset(SpikeQuestStaticFilePaths.TWILIGHT_LIBRARY_BACKDROP_PATH, "Texture");
        game.assetManager.setAsset("music/ponyvilleMusic.mp3", "Music");
    }

	public void render (float delta) {
			//clear the screen
			refresh();
			useLoadingScreen(delta);
			
			if (game.assetManager.loadAssets() && twilightObject.isLoaded() && spikeObject.isLoaded() && dialogController.areTextBalloonsLoaded()) {
				
				spikeQuestCamera.attachToBatch(game.batch);
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
					twilightObject.draw(game.batch);
					spikeObject.draw(game.batch);
					dialogController.drawTheDialogAndAnimateObjects(game.batch, twilightObject, spikeObject);
					game.batch.end();
					
					
					
					/*if (twilightTalking) {
						twilightTextBalloon.drawDialog(game.batch, twilightObject.getCurrentPositionX() - 200, twilightObject.getCurrentPositionY()+300);
						twilightObject.talk();
					}
					else {
						twilightObject.standStill();
					}
					if (spikeTalking && dialogIndex < 3)
						spikeTextBalloon.drawDialog(game.batch, spikeObject.getCurrentPositionX() + spikeBalloonPositionX, spikeObject.getCurrentPositionY()+300);
					
					else if (spikeTalking) 
						spikeTextBalloon.drawDialog(game.batch, spikeObject.getCurrentPositionX() + spikeBalloonPositionX + 50, spikeObject.getCurrentPositionY() + 300);*/
					
					
					controlObjects();
					
					
					
					/*//When spike stops dialog continues
					else if (!dialogScene && dialogController.firstTextIndex == 3 && !dialogController.drawFirstTextFlag){
						dialogScene = true;
						//twilightTalking = true;
						//twilightTalking = twilightTextBalloon.setNextDialog();
						twilightObject.moveRight(0);
						
						//reverse direction
						spikeObject.moveLeft(0);
						spikeObject.standStill();
						spikeBalloonPositionX = -300f;
						
					}*/
					
				
					/*//Check input
					if ((dialogScene) && Gdx.input.isKeyPressed(Keys.SPACE)) {
						
						//Start with Twilight
						twilightTalking = twilightTextBalloon.setNextDialog();
					
						if (!twilightTalking) {
							
							spikeTalking = spikeTextBalloon.setNextDialog();
							spikeObject.talk();
							
							if (!spikeTalking) {
								spikeObject.standStill();
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
			if (dialogController.secondTextIndex == 3) {
				
				
				if (spikeObject.getCurrentPositionX() < spikeQuestCamera.getCameraWidth()-200) {
					
					//moving, disable dialog
					dialogController.dialogEnabled = false;
					spikeObject.moveRight(5);
					
					//get twilight to stop talking
					twilightObject.standStill();
					
					//check again and set objects if true
					if (spikeObject.getCurrentPositionX() >= spikeQuestCamera.getCameraWidth()-200) {
						twilightObject.moveRight(0);
						spikeObject.moveLeft(0);
					}
				}
				
				else {
					
					//spikeObject.standStill();
					dialogController.setSecondTextBalloonPositionX(spikeObject.getCurrentPositionX() - 25);
					dialogController.dialogEnabled = true;
				}
				
			}
			else if (dialogController.areTextBalloonsFinished()) {
				spikeObject.moveRight(5);
				twilightObject.standStill();
				dialogController.dialogEnabled = false;
				
				if (spikeObject.getCurrentPositionX() > spikeQuestCamera.getCameraWidth()+500)
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
			
			spikeObject.spawn(0, 0);
			twilightObject.spawn(spikeQuestCamera.getCameraWidth()/2 - 100, 0);
			spikeObject.resize(OBJECT_SIZE_RATIO);
			twilightObject.resize(OBJECT_SIZE_RATIO);
			twilightObject.moveLeft(0); //change rotation
			
			dialogController.setTextBalloonDefaultPositionsOverObjects(twilightObject, spikeObject);
			dialogController.setSecondTextBalloonPositionX(dialogController.getSecondTextBalloonPositionX() + 150);
		}

		@Override
		public void dispose () {
			spikeObject.discard();
			spikeQuestCamera.discard();
			twilightObject.discard();
			dialogController.discardTextBalloons();
			
			game.assetManager.disposeAsset(SpikeQuestStaticFilePaths.TWILIGHT_LIBRARY_BACKDROP_PATH);
			
			spikeObject = null;
			spikeQuestCamera = null;
			twilightObject = null;
			
			super.dispose();
		}
}
