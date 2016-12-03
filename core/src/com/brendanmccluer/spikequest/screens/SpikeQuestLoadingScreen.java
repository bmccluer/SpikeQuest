package com.brendanmccluer.spikequest.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.common.objects.TimerObject;
import com.brendanmccluer.spikequest.objects.SpikeObject;

public class SpikeQuestLoadingScreen extends AbstractSpikeQuestScreen {
	SpikeObject spikeObject = null;
	BitmapFont font = null;
	TimerObject timer = null;
	String loading = "";
	int ellipses = 0;
	
	public SpikeQuestLoadingScreen(SpikeQuestGame game) {
		super(game);
	}

	@Override
	public void initialize() {
		spikeObject = new SpikeObject();
		font = new BitmapFont();
		timer = new TimerObject();
        gameCamera = new SpikeQuestCamera(1000, game.GAME_SCREEN_WIDTH, game.GAME_SCREEN_HEIGHT);
	}

	@Override
	public void render(float delta) {
		refresh();
		gameCamera.attachToBatch(game.batch);
		
		if (spikeObject.isLoaded()) {
			
			if (!screenStart) {
				screenStart = true;
				spikeObject.spawn(0, 0);
				spikeObject.spawn(gameCamera.getCameraPositionX() - spikeObject.getCenterX(), 0);
				timer.startTimer(0,1);
			}
			spikeObject.moveRight(0);
			
			if (timer.isTimerFinished()) {
				if (ellipses == 3)
					ellipses = 0;
				else 
					ellipses++;
				timer.startTimer(0, 2);
			}
			game.batch.begin();
			spikeObject.draw(game.batch);
			font.draw(game.batch, "Loading" + (ellipses==1 ? "." : ellipses==2 ? ".." : ellipses==3 ? "..." : ""), gameCamera.getCameraPositionX(), gameCamera.getCameraPositionY());
			game.batch.end();
		}
	}
	
	@Override
	public void dispose() {
		gameCamera.discard();
		timer.dispose();
		spikeObject.discard();
		font.dispose();
		
		gameCamera = null;
		timer = null;
		spikeObject = null;
		font = null;
	}

}
