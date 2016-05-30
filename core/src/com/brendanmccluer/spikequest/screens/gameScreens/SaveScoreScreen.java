package com.brendanmccluer.spikequest.screens.gameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;

/**
 * I display the results for each game, calculating the amount of bits earned.
 * 
 * @author Brendan
 *
 */
public class SaveScoreScreen extends AbstractSpikeQuestScreen {
	private final float FONT_X_POSITION;
	private final int POINTS_PER_BIT = 1000;
	private final int MAX_BITS_ALLOWED = 10;
	private final String BACKGROUND_MUSIC_PATH = "music/saveScoreScreenMusic.mp3";
	private int score = 0;
	private int bits = 0;
	private int gems = 0;
	private int totalBits = 0;
	//private int totalGems = SpikeQuestSaveFile.getGems();
	private int totalGems = 0;
	private int numberOfSpaces = 0;
	private BitmapFont scoreFont = new BitmapFont();
	private BitmapFont bitsFont = new BitmapFont();
	private BitmapFont gemsFont = new BitmapFont();
	private BitmapFont totalScoreFont = new BitmapFont();
	private BitmapFont totalGemsFont = new BitmapFont();
	private AbstractSpikeQuestScreen callingScreen = null;
	
	
	/**
	 * Pass in the score, gems, and screen that is calling this screen. 
	 * 
	 * @param game
	 * @param score
	 * @param gems
	 * @param aMusicTrackPath
	 * @param aCallingScreen
	 */
	public SaveScoreScreen(SpikeQuestGame game, int score, int gems, AbstractSpikeQuestScreen aCallingScreen) {
		super(game);
		
		SpikeQuestSaveFile.setSaveFile(game.SAVE_FILE_NAME);
		this.score = score;
		//this.bits = bits;
		this.gems = gems;
		
		callingScreen = aCallingScreen;
		game.assetManager.setAsset(BACKGROUND_MUSIC_PATH, "Music");
	
		//Do not show next screen
		//aNextScreen.hide();
		
		setFont(scoreFont);
		setFont(bitsFont);
		setFont(gemsFont);
		setFont(totalScoreFont);
		setFont(totalGemsFont);
		
		//calculate bits
		bits = Math.round(score/POINTS_PER_BIT);
		
		if (bits == 0)
			bits = 1;
		
		//maximum bits allowed
		if (bits > MAX_BITS_ALLOWED) {
			bits = MAX_BITS_ALLOWED;
		}
		
		//save bits
		SpikeQuestSaveFile.addBits(bits);
		totalBits = SpikeQuestSaveFile.getBits();
		
		//save gems
		SpikeQuestSaveFile.addGems(gems);
		totalGems = SpikeQuestSaveFile.getGems();
		
		gameCamera = new SpikeQuestCamera(1800, 1554, 917); //screen properties
		FONT_X_POSITION = gameCamera.getCameraWidth()/2 + 350;
		
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.SAVE_SCORE_SCREEN_BACKDROP_PATH, "Texture");
	}
	
	/**
	 * I set the font of each BitmapFont
	 * 
	 * @param aBitmapFont
	 */
	private void setFont(BitmapFont aBitmapFont) {
		
		aBitmapFont.setColor(Color.GREEN);
		aBitmapFont.scale(3);
		
	}

	@Override
	public void render(float delta) {
		
		refresh();
		useLoadingScreen(delta);
		if (game.assetManager.loadAssets()) {
			
			gameCamera.attachToBatch(game.batch);
			
			if (!screenStart) {
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.SAVE_SCORE_SCREEN_BACKDROP_PATH, "Texture");
				backgroundMusic = new SpikeQuestMusic((Music) game.assetManager.loadAsset(BACKGROUND_MUSIC_PATH, "Music"));
				backgroundMusic.playMusic(true);
				screenStart = true;
			}
			
			if (Gdx.input.isKeyJustPressed(Keys.ANY_KEY))
					numberOfSpaces++;
			
			game.batch.begin();
			game.batch.draw(currentBackdropTexture, 0, 0);
			
			if (numberOfSpaces > 0) {
				scoreFont.draw(game.batch, Integer.toString(score), FONT_X_POSITION, gameCamera.getCameraHeight()/2 + 250);
			}
			if (numberOfSpaces > 1) {
				bitsFont.draw(game.batch, Integer.toString(bits), FONT_X_POSITION, gameCamera.getCameraHeight()/2 + 115);
			}
			if (numberOfSpaces > 2) {
				bitsFont.draw(game.batch, Integer.toString(gems), FONT_X_POSITION, gameCamera.getCameraHeight()/2 - 20);
			}
			if (numberOfSpaces > 3) {
				bitsFont.draw(game.batch, Integer.toString(totalBits), FONT_X_POSITION, gameCamera.getCameraHeight()/2 - 185);
			}
			if (numberOfSpaces > 4) {
				bitsFont.draw(game.batch, Integer.toString(totalGems), FONT_X_POSITION, gameCamera.getCameraHeight()/2 - 305);
			}
			if (numberOfSpaces > 5) {
				//call the screen manager
				SpikeQuestScreenManager.setNextScreen(callingScreen, this, game);
				dispose();
			}
			
			game.batch.end();
		}
	}
	
	@Override
	public void dispose() {
		
		scoreFont.dispose();
		gemsFont.dispose();
		bitsFont.dispose();
		totalGemsFont.dispose();
		totalScoreFont.dispose();
		gameCamera.discard();
		
		backgroundMusic.stopMusic();
		backgroundMusic.discard();
		
		gameCamera = null;
		scoreFont = null;
		gemsFont = null;
		bitsFont = null;
		totalGemsFont = null;
		totalScoreFont = null;
		backgroundMusic = null;
		
		game.assetManager.disposeAsset(SpikeQuestStaticFilePaths.SAVE_SCORE_SCREEN_BACKDROP_PATH);
		game.assetManager.disposeAsset(BACKGROUND_MUSIC_PATH);
		
		super.dispose();
	}

}
