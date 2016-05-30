package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.common.objects.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.ponies.PinkieObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class SugarCubeCornerScreen extends AbstractSpikeQuestStandardScreen {
	private SpikeQuestDialogController aDialogController = null;
	private PinkieObject aPinkieObject = null;
			
	public SugarCubeCornerScreen(SpikeQuestGame game, String aScreenType, String aSpikeSpawnString) {
		super(game, 1097, 617, 1000, SpikeQuestStaticFilePaths.SUGAR_CUBE_CORNER_BACKDROP_PATH, aScreenType, aSpikeSpawnString);
		
		//load required assets for intro
		if (("intro").equalsIgnoreCase(screenType)) {
			
			aDialogController = new SpikeQuestDialogController(aPinkieObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SUGAR_CUBE_CORNER_INTRO_DIALOG), "Pinkie", 1, 
				aSpikeObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SUGAR_CUBE_CORNER_INTRO_DIALOG), "Spike", 0);
			aPinkieObject = new PinkieObject();
			
		}
		
	}

	@Override
	public void render(float delta) {
		
		//useLoadingScreen(delta);
		if (("intro").equalsIgnoreCase(screenType))
			renderIntro(delta);
		
		//render as normal
		else 
			renderNormal(delta);
			
	}
	
	
	private void renderNormal(float delta) {
		
		refresh();
		
		if (loadAssets()) {
			
			if (!screenStart)
				initialize();
			
			//ALWAYS ATTACH CAMERA TO BATCH AFTER INITIALIZATION
			gameCamera.attachToBatch(game.batch);
			
			game.batch.begin();
			draw();
			drawBitsAndGems();
			game.batch.end();
			
			//control
			controlSpike();
			
			//determine end of screen
			if (("left").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new PonyvilleParkScreen(game, " ", "right"), game);
				return;
			}
				
		}
		
	}

	/**
	 * I display SugarCubeCorner after first time
	 * playing the BalloonGame
	 * 
	 */
	private void renderIntro(float delta) {
		
		refresh();
		
		if (loadAssets() && aPinkieObject.isLoaded() && aDialogController.areTextBalloonsLoaded()) {
			
			if (!screenStart) {
				//initialize
				initializeIntro();
			}
			
			//ALWAYS ATTACH CAMERA RIGHT BEFORE DRAW AFTER INITIALIZATION
			gameCamera.attachToBatch(game.batch);
			
			//draw
			game.batch.begin();
			drawIntro();
			game.batch.end();
			
			//determine end of screen
			if (aDialogController.areTextBalloonsFinished()) {
				screenType = "normal";
				screenStart = false;
			}
			
		}
		
	}
	
	protected void drawIntro() {
		draw();
		aPinkieObject.draw(game.batch);
		aDialogController.drawTheDialogAndAnimateObjects(game.batch, aPinkieObject, aSpikeObject);
		
	}
	
	protected void draw() {
		drawBackdrop();
		aSpikeObject.draw(game.batch);
	}

	
	protected void initializeIntro() {
		initialize();
		aSpikeObject.spawn(gameCamera.getCameraWidth()/2 + 200, 0);
		aSpikeObject.moveLeft(0);
		aPinkieObject.spawn(gameCamera.getCameraWidth()/2 - 200, 0);
		aDialogController.setTextBalloonDefaultPositionsOverObjects(aPinkieObject, aSpikeObject);
	
	}
	
	protected void initialize() {
		
		super.initialize(true);
		screenStart = true;
	}
	
	@Override
	public void dispose() {
		
		if (aPinkieObject != null)
			aPinkieObject.discard();
		
		if (aDialogController != null)
			aDialogController.discardTextBalloons();
		
		aDialogController = null;
		aPinkieObject = null;
		
		super.dispose();
	}

	
}
