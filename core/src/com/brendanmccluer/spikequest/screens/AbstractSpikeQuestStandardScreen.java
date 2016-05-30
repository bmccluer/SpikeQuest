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
	private BitmapFont bitFont = new BitmapFont();
	private BitmapFont gemFont = new BitmapFont();
	private final int XSPACING = 10;
	private final int YSPACING = 40;
	protected SpikeObject aSpikeObject = new SpikeObject();
	protected SpikeQuestController aController = null;
	protected final String BACKDROP_PATH;
	private final String BIT_PATH = "object/bit/Bit.png";
	private final String GEM_PATH = "object/gems/GreenGemTexture.png";
	protected String spikePosition = "";
	protected String totalGems = Integer.toString(SpikeQuestSaveFile.getGems());
	protected String totalBits = Integer.toString(SpikeQuestSaveFile.getBits());
	protected static final String HUB_MAIN_MUSIC_PATH = "music/hubMusicMain.mp3";
	
	protected Texture bitTexture = null;
	protected Texture gemTexture = null; //only using the gem's picture, not the object

	
	public AbstractSpikeQuestStandardScreen(SpikeQuestGame game, int aBackdropWidth, int aBackdropHeight, int aCameraSize,
			String aBackdropPath, String aScreenType, String spawnSpikePosition) {
		super(game);
		
		gameCamera = new SpikeQuestCamera(aCameraSize, aBackdropWidth, aBackdropHeight);
		aController = new SpikeQuestController();
		BACKDROP_PATH = aBackdropPath;
		spikePosition = spawnSpikePosition;
		
		setScreenType(aScreenType);
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
			
			aController.controlListener(aSpikeObject, gameCamera, aSpikeObject.SPIKE_STANDARD_SPEED);
			
		}
	}
	
	/**
	 * I load the assets
	 * 
	 */
	protected boolean loadAssets() {
		boolean loaded = game.assetManager.loadAssets() && aSpikeObject.isLoaded();
		
		//load default background music if not there
		//if (loaded && backgroundMusic == null)
			//backgroundMusic = new SpikeQuestMusic((Music) game.assetManager.loadAsset(HUB_MAIN_MUSIC_PATH, "Music"));
		
		return loaded;
	}
	
	/**
	 * I initialize the screen
	 */
	protected void initialize (boolean useMusic) {
		
		currentBackdropTexture = (Texture) game.assetManager.loadAsset(BACKDROP_PATH, "Texture");
		bitTexture = (Texture) game.assetManager.loadAsset(BIT_PATH, "Texture");
		gemTexture = (Texture) game.assetManager.loadAsset(GEM_PATH, "Texture");
		
		//spawn spike
		aSpikeObject.spawn(0, 0);
		
		//relocate if coming from right
		if ("right".equalsIgnoreCase(spikePosition)) {
			aSpikeObject.setCurrentPositionX(gameCamera.getWorldWidth() - aSpikeObject.getCollisionRectangle().getWidth());
			aSpikeObject.moveLeft(0);
			aSpikeObject.standStill();
			
			//move camera to Spike
			if (aSpikeObject.getCurrentPositionX() > gameCamera.getCameraPositionX())
				gameCamera.translateCamera(aSpikeObject.getCurrentPositionX() - gameCamera.getCameraPositionX(), 0);
		}
		//play music
		if (useMusic)
			((Music) game.assetManager.loadAsset(HUB_MAIN_MUSIC_PATH,"Music")).play();
		else 
			((Music) game.assetManager.loadAsset(HUB_MAIN_MUSIC_PATH,"Music")).stop();;
		
	}
	
	/**
	 * I draw the amount of bit and gems at the top left of the camera
	 * 
	 * @param batch
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
		
		if (aSpikeObject.getCenterX() < 0) {
			edgePosition = "left";
		}
		
		else if (aSpikeObject.getCenterX() > gameCamera.getWorldWidth()) {
			edgePosition = "right";
		}
		
		else if (aSpikeObject.getCenterY() > gameCamera.getWorldHeight()) {
			edgePosition = "top";
		}
		
		else if (aSpikeObject.getCenterY() < 0)
			edgePosition = "bottom";
		
			
		return edgePosition;
	}
	
	
	/**
	 * discard spikeObject, background asset, and camera
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
		
		aSpikeObject.discard();
		aSpikeObject = null;
		gameCamera.discard();
		gameCamera = null;
	}
}
