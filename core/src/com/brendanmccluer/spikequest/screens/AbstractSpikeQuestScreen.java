package com.brendanmccluer.spikequest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;
import com.brendanmccluer.spikequest.SpikeQuestFadingEffect;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.interfaces.LoadableObject;
import com.brendanmccluer.spikequest.interfaces.SpikeQuestScreen;
import com.brendanmccluer.spikequest.objects.buttons.ButtonObject;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic Class for all screens to implement
 * set properties used by every screen here 
 * @author Brendan
 * 
 *
 *
 */
public abstract class AbstractSpikeQuestScreen implements SpikeQuestScreen, LoadableObject {
	protected SpikeQuestGame game;
	protected SpikeQuestCamera gameCamera = null; 
	protected boolean screenStart = false;
	protected SpikeQuestMusic backgroundMusic = null;
	protected Texture currentBackdropTexture = null;
	protected String screenType = "";
	protected SpikeQuestLoadingScreen loadingScreen = null;
	protected SpikeQuestFadingEffect fadingEffect = null;

	private List<LoadableObject> loadList = null;
	private boolean isAssetManagerLoaded, isObjectsLoaded;


	public AbstractSpikeQuestScreen (SpikeQuestGame game) {
		this.game = game;
		loadList = new ArrayList<>();
	}

	/**
	 * I display the loading screen if screenStart is not true. I also dispose loading screen 
	 * when loading screen done
	 * @param delta
	 */
	protected void useLoadingScreen(float delta) {
		if (screenStart && loadingScreen != null) {
			disposeLoadingScreen();
			loadingScreen = null;
		}
		else if (!screenStart)
			renderLoadingScreen(delta);
	}
	
	private void renderLoadingScreen(float delta) {
		if (loadingScreen == null) {
			loadingScreen = new SpikeQuestLoadingScreen(game);
			loadingScreen.initialize();
		}

		loadingScreen.render(delta);
	}
	
	private void disposeLoadingScreen() {
		if (loadingScreen != null)
			loadingScreen.dispose();
	}
	

	/**
	 * I check if a button is pressed (if the camera is set)
	 * @param aButtonObjectInterface
	 * @return
	 */
	protected boolean isButtonPressed(ButtonObject aButtonObjectInterface) {
		/*if (Gdx.app.getType().equals(ApplicationType.Android)) {
			if (gameCamera != null) {
				return aButtonObject.isTouching(Gdx.input.is, yMousePos)
			}
		}*/
			
		if (gameCamera != null) {
			return aButtonObjectInterface.checkMouseOver(gameCamera.getMousePositionX(), gameCamera.getMousePositionY()) &&
					Gdx.input.isButtonPressed(Buttons.LEFT);
		}
		
		return false;
	}
	
	/**
	 * I clear the game screen
	 */
	public void refresh() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	public void fade() {
		fade(5);
	}

	public void fade(int seconds) {
		if(fadingEffect == null) {
			fadingEffect = new SpikeQuestFadingEffect(gameCamera);
		}
		fadingEffect.setFade(seconds);
	}

	/**
	 * Call batch.begin before this
	 * @param delta
     */
	public void drawEffects(float delta) {
		boolean isDrawing = game.batch.isDrawing();
		if(!isDrawing)
			game.batch.begin();
		if(fadingEffect != null) {
			fadingEffect.draw(game.batch);
		}
		if(!isDrawing)
			game.batch.end();
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void show () {
	}

	@Override
	public void hide () {
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		//reset font for new screens
		game.bitmapFont.dispose(); 
		game.bitmapFont = new BitmapFont();
		if (currentBackdropTexture != null) {
			currentBackdropTexture.dispose();
			currentBackdropTexture = null;
		}
		safeDispose(fadingEffect);
		disposeLoader();
	}

	protected void disposeLoader() {
		safeDispose(loadList);
	}
	
	/**
	 * I draw the backdrop
	 */
	protected void drawBackdrop () {
		if (currentBackdropTexture != null)
			game.batch.draw(currentBackdropTexture, 0, 0);
	}

	/**
	 * Checks for null before disposing
	 * @param disposableObject
     */
	protected void safeDispose(Disposable disposableObject) {
		if (disposableObject != null)
			disposableObject.dispose();
	}

	protected void safeDispose(List<? extends Disposable> disposableObjects) {
		if (disposableObjects != null)
			for (Disposable disposableObject : disposableObjects)
				safeDispose(disposableObject);
		disposableObjects.clear();
	}

	protected void safeDispose(Disposable... disposables) {
		for(Disposable disposableObject : disposables)
			safeDispose(disposableObject);
	}
	
	/**
	 * I set the screen type. If the type
	 * is recognized by the screen, it will render that type
	 * 
	 * @param aScreenType
	 */
	public void setScreenType (String aScreenType) {
		screenType = aScreenType;
	}

	/**
	 * I return the screen type
	 * 
	 * @return
	 */
	public String getScreenType () {
		return screenType;
	}

	protected void addToLoader(LoadableObject... objects) {
		for(LoadableObject object : objects)
			loadList.add(object);
	}

	public boolean isLoaded() {
		if(!isAssetManagerLoaded)
			isAssetManagerLoaded = game.assetManager.loadAssets();
		if(!isObjectsLoaded)
			isObjectsLoaded = loadObjects();
		return isAssetManagerLoaded && isObjectsLoaded;
	}

	private boolean loadObjects() {
		boolean allLoaded = true;
		for (LoadableObject object : loadList) {
			if(object == null)
				System.out.println("Warning. Object in loadList is null");
			else if(!object.isLoaded())
				allLoaded = false;
		}
		return allLoaded;
	}
}
