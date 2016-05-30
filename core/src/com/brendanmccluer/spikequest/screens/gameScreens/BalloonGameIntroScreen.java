package com.brendanmccluer.spikequest.screens.gameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;

public class BalloonGameIntroScreen extends AbstractSpikeQuestScreen {
	
	public BalloonGameIntroScreen(SpikeQuestGame game) {
		super(game);
		gameCamera = new SpikeQuestCamera(1675, 1564, 931); //screen properties
		//move the camera slightly to center the picture
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.BALLOON_GAME_INTRO_BACKDROP_PATH, "Texture");
	}

	@Override
	public void render(float delta) {
		gameCamera.attachToBatch(game.batch);
		useLoadingScreen(delta);
		refresh();
		
		if (game.assetManager.loadAssets()) {
			
			if (!screenStart) {
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.BALLOON_GAME_INTRO_BACKDROP_PATH, "Texture");
				screenStart = true;
			}
			
			game.batch.begin();
			game.batch.draw(currentBackdropTexture, 0, 0);
			game.batch.end();
			
			if (Gdx.input.isButtonPressed(Keys.ANY_KEY) || Gdx.input.isKeyPressed(Keys.ANY_KEY)) {
				dispose();
				SpikeQuestScreenManager.setNextScreen(this, game);
			}
		
		}
	}
	
	@Override
	public void dispose() {
		game.assetManager.disposeAllAssets();
	
		gameCamera.discard();
		gameCamera = null;
		
		super.dispose();
		
	}

}
