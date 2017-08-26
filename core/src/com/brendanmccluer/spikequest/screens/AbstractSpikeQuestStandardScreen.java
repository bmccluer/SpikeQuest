package com.brendanmccluer.spikequest.screens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.brendanmccluer.spikequest.SpikeQuestController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.objects.SpikeObject;

/**
 * I extend the Abstract screen by creating a spike object with a controller 
 * and camera
 * 
 * @author Brendan
 *
 */
public abstract class AbstractSpikeQuestStandardScreen extends AbstractSpikeQuestScreen {
	private BitmapFont bitFont, gemFont = null;
	private final int XSPACING = 10;
	private final int YSPACING = 40;
	private int backdropWidth, backdropHeight, cameraSize = 0;
	protected SpikeObject spikeObject = null;
	protected SpikeQuestController controller = null;
	protected final String BACKDROP_PATH;
	private final String BIT_PATH = "object/bit/Bit.png";
	private final String GEM_PATH = "object/gems/GreenGemTexture.png";
	protected String spikePosition = "";
	protected String totalGems, totalBits = null;
	protected static final String HUB_MAIN_MUSIC_PATH = "music/hubMusicMain.mp3";
	
	protected Texture bitTexture = null;
	protected Texture gemTexture = null; //only using the gem's picture, not the object

	
	public AbstractSpikeQuestStandardScreen(SpikeQuestGame game, int aBackdropWidth, int aBackdropHeight, int aCameraSize,
			String aBackdropPath, String aScreenType, String spawnSpikePosition) {
		super(game);

		BACKDROP_PATH = aBackdropPath;
		spikePosition = spawnSpikePosition;
		backdropWidth = aBackdropWidth;
		backdropHeight = aBackdropHeight;
		cameraSize = aCameraSize;
		
		setScreenType(aScreenType);
	}

	@Override
	public void show() {
		bitFont = new BitmapFont();
		gemFont = new BitmapFont();
		spikeObject = new SpikeObject();
		addToLoader(spikeObject);
		gameCamera = new SpikeQuestCamera(cameraSize, backdropWidth, backdropHeight);
		controller = new SpikeQuestController();
        totalGems = Integer.toString(SpikeQuestSaveFile.getGems());
        totalBits = Integer.toString(SpikeQuestSaveFile.getBits());

		game.assetManager.setAsset(BACKDROP_PATH, "Texture");
		game.assetManager.setAsset(BIT_PATH, "Texture");
		game.assetManager.setAsset(GEM_PATH, "Texture");

		//set music if not there
		if (game.assetManager.loadAsset(HUB_MAIN_MUSIC_PATH, "Music") == null)
			game.assetManager.setAsset(HUB_MAIN_MUSIC_PATH, "Music");

	}

	/**
	 * I control spike
	 * 
	 */
	protected void controlSpike() {
		
		if (loadAssets()) {
			
			controller.controlListener(spikeObject, gameCamera, spikeObject.SPIKE_STANDARD_SPEED);
			
		}
	}
	
	/**
	 * I load the assets
	 * Deprecated: Use isLoaded instead
	 */
	@Deprecated
	protected boolean loadAssets() {
		boolean loaded = game.assetManager.loadAssets() && spikeObject.isLoaded();
		return loaded;
	}
	
	/**
	 * I start the screen
	 */
	protected void startScreen(boolean useMusic) {
		currentBackdropTexture = (Texture) game.assetManager.loadAsset(BACKDROP_PATH, "Texture");
		bitTexture = (Texture) game.assetManager.loadAsset(BIT_PATH, "Texture");
		gemTexture = (Texture) game.assetManager.loadAsset(GEM_PATH, "Texture");
		
		//spawn spike
		spikeObject.spawn(0, 0);
		
		//relocate if coming from right
		if ("right".equalsIgnoreCase(spikePosition)) {
			spikeObject.setCurrentPositionX(gameCamera.getWorldWidth() - spikeObject.getCollisionRectangle().getWidth());
			spikeObject.moveLeft(0);
			spikeObject.standStill();
			
			//move camera to Spike
			if (spikeObject.getCurrentPositionX() > gameCamera.getCameraPositionX())
				gameCamera.translateCamera(spikeObject.getCurrentPositionX() - gameCamera.getCameraPositionX(), 0);
		}
		if ("center".equalsIgnoreCase(spikePosition)) {
			spikeObject.setCurrentPositionX(gameCamera.getWorldWidth()/2 - spikeObject.getCollisionRectangle().getWidth());

			//move camera to Spike
			if (spikeObject.getCurrentPositionX() > gameCamera.getCameraPositionX())
				gameCamera.translateCamera(spikeObject.getCurrentPositionX() - gameCamera.getCameraPositionX(), 0);
		}
		//play music
		if (useMusic)
			((Music) game.assetManager.loadAsset(HUB_MAIN_MUSIC_PATH,"Music")).play();
		else 
			((Music) game.assetManager.loadAsset(HUB_MAIN_MUSIC_PATH,"Music")).stop();;

		screenStart = true;
	}
	
	/**
	 * I draw the amount of bit and gems at the top left of the camera
	 *
	 */
	protected void drawBitsAndGems () {
		
		float leftCameraEdge = gameCamera.getCameraPositionX() - gameCamera.getCameraWidth()/2;
		float topCameraEdge = gameCamera.getCameraPositionY() + gameCamera.getCameraHeight()/2;
		float yLocation = topCameraEdge - YSPACING;
		
		TextBounds gemFontBounds = gemFont.getBounds(totalGems);
		TextBounds bitFontBounds = bitFont.getBounds(totalGems);
		
		float gemFontXLocation = leftCameraEdge + gemTexture.getWidth() + gemFontBounds.width; //draws from right to left
		float bitXLocation = gemFontXLocation + gemFontBounds.width + XSPACING; 
		float bitFontXLocation = bitXLocation + bitTexture.getWidth() + bitFontBounds.width; //draws from right to left
		
		game.batch.draw(gemTexture, leftCameraEdge + XSPACING, yLocation);
		gemFont.draw(game.batch, totalGems, gemFontXLocation, yLocation + gemFontBounds.height); //draws from top to bottom
		game.batch.draw(bitTexture, bitXLocation, yLocation);
		bitFont.draw(game.batch, totalBits, bitFontXLocation, yLocation + bitFontBounds.height); //draws from top to bottom
	}
	
	/**
	 * I return a string representation of an edge Spike is touching 
	 * 
	 * @return
	 */
	protected String getEdgeTouched () {
		String edgePosition = "";
		
		if (spikeObject.getCenterX() < 0) {
			edgePosition = "left";
		}
		
		else if (spikeObject.getCenterX() > gameCamera.getWorldWidth()) {
			edgePosition = "right";
		}
		
		else if (spikeObject.getCenterY() > gameCamera.getWorldHeight()) {
			edgePosition = "top";
		}
		
		else if (spikeObject.getCenterY() < 0)
			edgePosition = "bottom";
		
			
		return edgePosition;
	}
	
	
	/**
	 * dispose spikeObject, background asset, and camera
	 */
	@Override
	public void dispose() {
		game.assetManager.disposeAsset(BACKDROP_PATH);
		game.assetManager.disposeAsset(GEM_PATH);
		game.assetManager.disposeAsset(BIT_PATH);
		
		gemTexture = null;
		bitTexture = null;
		
		gemFont.dispose();
		bitFont.dispose();
		
		gemFont = null;
		bitFont = null;

		gameCamera.discard();
		gameCamera = null;
		disposeLoader();
	}
}
