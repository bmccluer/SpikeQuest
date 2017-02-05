package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.ponies.FlamObject;
import com.brendanmccluer.spikequest.objects.ponies.FlimObject;
import com.brendanmccluer.spikequest.objects.ponies.PinkieObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class SugarCubeCornerScreen extends AbstractSpikeQuestStandardScreen {
	private static final String FLIM_FLAM_DIALOG_PATH = "dialog/ponyvilleDialog/flimFlamParaspriteDialog.txt";
	private SpikeQuestDialogController dialogController = null;
	private PinkieObject pinkieObject = null;
	private FlimObject flimObject = null;
	private FlamObject flamObject = null;
	private Texture paraspriteCageTexture = null;
			
	public SugarCubeCornerScreen(SpikeQuestGame game, String aScreenType, String aSpikeSpawnString) {
		super(game, 1097, 617, 1000, SpikeQuestStaticFilePaths.SUGAR_CUBE_CORNER_BACKDROP_PATH, aScreenType, aSpikeSpawnString);
	}

	@Override
	public void initialize() {
		super.initialize();
		paraspriteCageTexture = new Texture("object/ParaspriteCage.png"); //TODO; USE ANIMATED OBJECT
		//load required assets for intro
		if (("intro").equalsIgnoreCase(screenType)) {

			dialogController = new SpikeQuestDialogController(pinkieObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SUGAR_CUBE_CORNER_INTRO_DIALOG), "Pinkie", 1,
					spikeObject, new SpikeQuestTextBalloon(SpikeQuestStaticFilePaths.SUGAR_CUBE_CORNER_INTRO_DIALOG), "Spike", 0);
			pinkieObject = new PinkieObject();
		}
		//load flim and flam if ShyAndSeek is complete
		if(SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_SHY_AND_SEEK_COMPLETE_KEY)) {
			flimObject = new FlimObject();
			flamObject = new FlamObject();
			dialogController = new SpikeQuestDialogController(flimObject, new SpikeQuestTextBalloon(FLIM_FLAM_DIALOG_PATH), "Flim", 6,
					flamObject, new SpikeQuestTextBalloon(FLIM_FLAM_DIALOG_PATH), "Flam", 6);
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
		
		if (loadAssets() && (flimObject == null || flimObject.isLoaded()) && (flamObject == null || flamObject.isLoaded()) && (dialogController == null || dialogController.areTextBalloonsLoaded())) {
			
			if (!screenStart)
				startScreenIntro();
			
			//ALWAYS ATTACH CAMERA TO BATCH AFTER INITIALIZATION
			gameCamera.attachToBatch(game.batch);
			
			game.batch.begin();
			draw();
			drawBitsAndGems();
			game.batch.end();
			
			//control
			controlSpike();
			
			if(dialogController != null) {
				if (dialogController.areTextBalloonsFinished())
					dialogController.reset();
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
		
		if (loadAssets() && pinkieObject.isLoaded() && dialogController.areTextBalloonsLoaded()) {
			
			if (!screenStart) {
				//startScreen
				initializeIntro();
			}
			
			//ALWAYS ATTACH CAMERA RIGHT BEFORE DRAW AFTER INITIALIZATION
			gameCamera.attachToBatch(game.batch);
			
			//draw
			game.batch.begin();
			drawIntro();
			game.batch.end();
			
			//determine end of screen
			if (dialogController.areTextBalloonsFinished()) {
				screenType = "normal";
				screenStart = false;
			}
			
		}
		
	}
	
	protected void drawIntro() {
		draw();
		pinkieObject.draw(game.batch);
		dialogController.drawTheDialogAndAnimateObjects(game.batch, pinkieObject, spikeObject);
		
	}
	
	protected void draw() {
		drawBackdrop();
		if (flimObject != null) {
			flimObject.draw(game.batch);
			flamObject.draw(game.batch);
			game.batch.draw(paraspriteCageTexture, flimObject.getCenterX() - 150, 15);
			dialogController.drawTheDialogAndAnimateObjects(game.batch, flimObject, flamObject);
		}
		spikeObject.draw(game.batch);
	}

	
	protected void initializeIntro() {
		startScreenIntro();
		spikeObject.spawn(gameCamera.getCameraWidth()/2 + 200, 0);
		spikeObject.moveLeft(0);
		pinkieObject.spawn(gameCamera.getCameraWidth()/2 - 200, 0);
		dialogController.setTextBalloonDefaultPositionsOverObjects(pinkieObject, spikeObject);
	
	}
	
	protected void startScreenIntro() {
		
		super.startScreen(true);
		if (flimObject != null) {
			flimObject.spawn(gameCamera.getCameraWidth()/2 - 300, 15);
			flimObject.moveRight(0);
			flamObject.spawn(gameCamera.getCameraWidth()/2 - 150, 15);
			flimObject.resize(0.5f);
			flamObject.resize(0.5f);
			flamObject.moveLeft(0);
			dialogController.setTextBalloonDefaultPositionsOverObjects(flimObject, flamObject);
			dialogController.useTimer(); //use timer instead of space bar
		}
		screenStart = true;
	}
	
	@Override
	public void dispose() {
		if (pinkieObject != null)
			pinkieObject.discard();
		if (dialogController != null)
			dialogController.discardTextBalloons();
		if (flimObject != null)
			flimObject.discard();
		if (flamObject != null)
			flamObject.discard();
		
		dialogController = null;
		pinkieObject = null;
		flamObject = null;
		flimObject = null;
		super.dispose();
	}

	
}
