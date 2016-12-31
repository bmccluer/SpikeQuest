package com.brendanmccluer.spikequest.screens.gameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;

public class ShyAndSeekInstructionScreen extends AbstractSpikeQuestScreen {
	private static final String BACKDROP_TEXTURE_PATH = "backdrop/shyAndSeekInstructions.png";
	
	public ShyAndSeekInstructionScreen(SpikeQuestGame game) {
		super(game);
	}

	@Override
	public void initialize() {
		gameCamera = new SpikeQuestCamera(1675, 1564, 931); //screen properties
		//move the camera slightly to center the picture
		game.assetManager.setAsset(BACKDROP_TEXTURE_PATH, "Texture");
	}

	@Override
	public void render(float delta) {
		gameCamera.attachToBatch(game.batch);
		useLoadingScreen(delta);
		refresh();
		
		if (game.assetManager.loadAssets()) {
			
			if (!screenStart) {
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(BACKDROP_TEXTURE_PATH, "Texture");
				screenStart = true;
			}
			
			game.batch.begin();
			game.batch.draw(currentBackdropTexture, 0, 0);
			game.batch.end();
			
			if (Gdx.input.isKeyJustPressed(Keys.ANY_KEY) || Gdx.input.justTouched()) {
				dispose();

				//if Game has not been set as next,
				if (game.screenStack.isEmpty() || !(game.screenStack.peek() instanceof ShyAndSeekScreen))
					game.screenStack.push(new ShyAndSeekScreen(game));

				SpikeQuestScreenManager.popNextScreen(game);
				//SpikeQuestScreenManager.forwardScreen(this, new ShyAndSeekScreen(game), game);
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
