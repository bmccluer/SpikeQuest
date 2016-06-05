package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.common.objects.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.ponies.FlamObject;
import com.brendanmccluer.spikequest.objects.ponies.FlimObject;
import com.brendanmccluer.spikequest.objects.ponies.PinkieObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class SugarCubeCornerScreen extends AbstractSpikeQuestStandardScreen {
	private static final String FLIM_FLAM_DIALOG_PATH = "dialog/ponyvilleDialog/flimFlamParaspriteDialog.txt";
	private SpikeQuestDialogController aDialogController = null;
	private PinkieObject aPinkieObject = null;
	private FlimObject aFlimObject = null;
	private FlamObject aFlamObject = null;
	private Texture paraspriteCageTexture = new Texture("object/ParaspriteCage.png"); //TODO; USE ANIMATED OBJECT
			
	public SugarCubeCornerScreen(SpikeQuestGame game, String aScreenType, String aSpikeSpawnString) {
		super(game, 1097, 617, 1000, SpikeQuestStaticFilePaths.SUGAR_CUBE_CORNER_BACKDROP_PATH, aScreenType, aSpikeSpawnString);
		
		//load required assets for intro
		if (("intro").equalsIgnoreCase(screenType)) {
			
			aDialogController = new SpikeQuestDialogController(aPinkieObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SUGAR_CUBE_CORNER_INTRO_DIALOG), "Pinkie", 1, 
				aSpikeObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SUGAR_CUBE_CORNER_INTRO_DIALOG), "Spike", 0);
			aPinkieObject = new PinkieObject();
		}
		//load flim and flam if ShyAndSeek is complete
		if(SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_SHY_AND_SEEK_COMPLETE_KEY)) {
			aFlimObject = new FlimObject();
			aFlamObject = new FlamObject();
			aDialogController = new SpikeQuestDialogController(aFlimObject, new SpikeQuestTextBalloon(FLIM_FLAM_DIALOG_PATH), "Flim", 6, 
					aFlamObject, new SpikeQuestTextBalloon(FLIM_FLAM_DIALOG_PATH), "Flam", 6);
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
		
		if (loadAssets() && (aFlimObject == null || aFlimObject.isLoaded()) && (aFlamObject == null || aFlamObject.isLoaded()) && (aDialogController == null || aDialogController.areTextBalloonsLoaded())) {
			
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
			
			if(aDialogController != null) {
				if (aDialogController.areTextBalloonsFinished())
					aDialogController.reset();
			}
			
			//determine end of screen
			if (("left").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new PonyvilleParkScreen(game, " ", "right"), game);
				return;
			}
			
			if (("right").equals(getEdgeTouched()) && SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_SHY_AND_SEEK_COMPLETE_KEY)) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new PonyvilleCityHallScreen(game, " ", "left"), game);
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
		if (aFlimObject != null) {
			aFlimObject.draw(game.batch);
			aFlamObject.draw(game.batch);
			game.batch.draw(paraspriteCageTexture, aFlimObject.getCenterX() - 150, 15);
			aDialogController.drawTheDialogAndAnimateObjects(game.batch, aFlimObject, aFlamObject);
		}
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
		if (aFlimObject != null) {
			aFlimObject.spawn(gameCamera.getCameraWidth()/2 - 300, 15);
			aFlimObject.moveRight(0);
			aFlamObject.spawn(gameCamera.getCameraWidth()/2 - 150, 15);
			aFlimObject.resize(0.5f);
			aFlamObject.resize(0.5f);
			aFlamObject.moveLeft(0);
			aDialogController.setTextBalloonDefaultPositionsOverObjects(aFlimObject, aFlamObject);
			aDialogController.useTimer(); //use timer instead of space bar
		}
		screenStart = true;
	}
	
	@Override
	public void dispose() {
		if (aPinkieObject != null)
			aPinkieObject.discard();
		if (aDialogController != null)
			aDialogController.discardTextBalloons();
		if (aFlimObject != null)
			aFlimObject.discard();
		if (aFlamObject != null)
			aFlamObject.discard();
		
		aDialogController = null;
		aPinkieObject = null;
		aFlamObject = null;
		aFlimObject = null;
		super.dispose();
	}

	
}
