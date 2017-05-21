package com.brendanmccluer.spikequest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.interfaces.SpikeQuestScreen;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;
import com.brendanmccluer.spikequest.objects.buttons.ButtonObject;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;

import java.util.List;

/**
 * Generic Class for all screens to implement
 * set properties used by every screen here 
 * @author Brendan
 * 
 *
 *
 */
public abstract class AbstractSpikeQuestScreen implements SpikeQuestScreen {
	protected SpikeQuestGame game;
	protected SpikeQuestCamera gameCamera = null; 
	protected boolean screenStart = false;
	protected SpikeQuestMusic backgroundMusic = null;
	protected Texture currentBackdropTexture = null;
	protected String screenType = "";
	protected SpikeQuestLoadingScreen loadingScreen = null;

	public AbstractSpikeQuestScreen (SpikeQuestGame game) {
		this.game = game;
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
				disposableObject.dispose();
		disposableObjects.clear();
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
	
	
	/*
	 * I check if objects are loaded and ready to be drawn. An object that has been
	 * discarded will be ignored
	 */
	protected boolean objectsLoaded (AbstractSpikeQuestObject[] spikeQuestObjects) {
		boolean allLoaded = true;
		for (int i=0;i<spikeQuestObjects.length;i++) {
			if (spikeQuestObjects[i] != null && !spikeQuestObjects[i].isLoaded()) {
				allLoaded = false;
			}
		}
		return allLoaded;
	}
}
