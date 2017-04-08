package com.brendanmccluer.spikequest;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;
import com.brendanmccluer.spikequest.interfaces.SpikeQuestScreen;
import com.brendanmccluer.spikequest.screens.MainMenuScreen;

import java.util.Stack;

public class SpikeQuestGame extends Game{
	
	//These are to be shared by every screen
	public SpriteBatch batch = null;
	public BitmapFont bitmapFont = null;
	public SpikeQuestAssetManager assetManager = null;
	public final int GAME_SCREEN_WIDTH = 1920;
	public final int GAME_SCREEN_HEIGHT = 1820;
	public final String SAVE_FILE_NAME = "SaveFile1";
    public Stack<SpikeQuestScreen> screenStack;
	public boolean debugMode = false;
	
	public void create () {
		SpikeQuestScreen mainMenuScreen = new MainMenuScreen(this);
		assetManager = new SpikeQuestAssetManager();
		batch = new SpriteBatch(); 
		bitmapFont = new BitmapFont();
        screenStack = new Stack<>();

        mainMenuScreen.initialize();
		this.setScreen(mainMenuScreen);
	}
	
	//required for initial render
	public void render () {
		super.render();
	}
	
	//game is closed
	public void dispose () {
		
		//dispose current screen
		this.getScreen().dispose();
		
		//in case assetManager was not disposed 
		assetManager.disposeAllAssets();
		
		batch.dispose();
		bitmapFont.dispose();
		
		assetManager = null;
		batch = null;
		bitmapFont = null;
	}
	
	
}
