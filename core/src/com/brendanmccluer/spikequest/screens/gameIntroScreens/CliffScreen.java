package com.brendanmccluer.spikequest.screens.gameIntroScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestAssets;
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
	private ParticleEffect dustParticles = null;

	public CliffScreen(SpikeQuestGame game) {
		super(game);
	}

	@Override
	public void show() {
		game.assetManager.setAsset(SpikeQuestAssets.CLIFF_SCREEN_BACKDROP_PATH, "Texture");
		game.assetManager.setAsset(SpikeQuestAssets.SCREAM_SOUND_PATH, "Sound");
		game.assetManager.setAsset(SpikeQuestAssets.CRASH_SOUND_PATH, "Sound");
		gameCamera = new SpikeQuestCamera(1200, 1452, 817);
		dustParticles = new ParticleEffect();
		dustParticles.load(Gdx.files.internal(SpikeQuestAssets.PARTICLE_FX_SMOKE), Gdx.files.internal(SpikeQuestAssets.PARTICLE_FX_DIRECTORY));
		dustParticles.scaleEffect(200);
		dustParticles.start();
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
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(SpikeQuestAssets.CLIFF_SCREEN_BACKDROP_PATH, "Texture");
				screamSoundEffect = new SpikeQuestSoundEffect((Sound) game.assetManager.loadAsset(SpikeQuestAssets.SCREAM_SOUND_PATH, "Sound"), 100);
				crashSoundEffect = new SpikeQuestSoundEffect((Sound) game.assetManager.loadAsset(SpikeQuestAssets.CRASH_SOUND_PATH, "Sound"), 100);
				screenStart = true;
			}
			else {
				dustParticles.setPosition(aWagonObject.getCenterX(), aWagonObject.getCenterY());
				if (aWagonObject.getCurrentPositionX() < gameCamera.getCameraWidth()-300) {
					aWagonObject.moveRight(10);
				}
				else {
					//fall!
					dustParticles.allowCompletion();
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
				dustParticles.draw(game.batch, delta);
				aWagonObject.draw(game.batch);
				game.batch.end();
				
			}
			
			
			
			
		}
		

	}
	
	@Override
	public void dispose () {
		gameCamera.discard();
		aWagonObject.dispose();
		game.assetManager.disposeAsset(SpikeQuestAssets.CLIFF_SCREEN_BACKDROP_PATH);
		aWagonObject = null;
	}

}
