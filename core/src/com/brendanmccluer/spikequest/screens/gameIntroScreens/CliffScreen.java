package com.brendanmccluer.spikequest.screens.gameIntroScreens;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.WagonObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestSoundEffect;

public class CliffScreen extends AbstractSpikeQuestScreen {
	private WagonObject aWagonObject = new WagonObject();
	private float cliffPosition = 500;
	private SpikeQuestSoundEffect screamSoundEffect = null;
	private SpikeQuestSoundEffect crashSoundEffect = null;
	
	public CliffScreen(SpikeQuestGame game) {
		super(game);
	}

	@Override
	public void initialize() {
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.CLIFF_SCREEN_BACKDROP_PATH, "Texture");
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.SCREAM_SOUND_PATH, "Sound");
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.CRASH_SOUND_PATH, "Sound");
		gameCamera = new SpikeQuestCamera(1200, 1452, 817);
	}

	@Override
	public void render(float delta) {
		
		refresh();
		useLoadingScreen(delta);
		gameCamera.attachToBatch(game.batch);
		
		if (aWagonObject.isLoaded() && game.assetManager.loadAssets()) {
			
			if (!screenStart) {
				aWagonObject.spawn(-200, cliffPosition);
				aWagonObject.resize(0.5f);
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.CLIFF_SCREEN_BACKDROP_PATH, "Texture");
				
				screamSoundEffect = new SpikeQuestSoundEffect((Sound) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.SCREAM_SOUND_PATH, "Sound"), 100);
				crashSoundEffect = new SpikeQuestSoundEffect((Sound) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.CRASH_SOUND_PATH, "Sound"), 100);
				
				screenStart = true;
			}
			else {
				
				if (aWagonObject.getCurrentPositionX() < gameCamera.getCameraWidth()-300) {
					aWagonObject.moveRight(10);
				}
				else {
					//fall!
					aWagonObject.setGroundPosition(-200);
					aWagonObject.rotate(-2);
					screamSoundEffect.playSound(false);
					//sound is done playing
					if (!screamSoundEffect.isPlaying()) {
						dispose();
						SpikeQuestScreenManager.forwardScreen(this, new CliffBottomScreen(game, crashSoundEffect, screamSoundEffect), game);
						return;
					}
					
					
					aWagonObject.standStill();
				}
				
				game.batch.begin();
				game.batch.draw(currentBackdropTexture, 0, 0);
				aWagonObject.draw(game.batch);
				game.batch.end();
				
			}
			
			
			
			
		}
		

	}
	
	@Override
	public void dispose () {
		gameCamera.discard();
		aWagonObject.discard();
		game.assetManager.disposeAsset(SpikeQuestStaticFilePaths.CLIFF_SCREEN_BACKDROP_PATH);
		aWagonObject = null;
	}

}
