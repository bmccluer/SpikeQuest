package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class PonyvilleCityHallScreen extends AbstractSpikeQuestStandardScreen {
	
	PonyvilleCityHallScreen (SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		
		super(game, 1080, 608, 1050, "backdrop/outsideCityHall.png", aScreenType, aSpikePosition);
		
	}
	
	@Override
	public void render(float delta) {
		refresh();
		//useLoadingScreen(delta);
		if (game.assetManager.loadAssets() && loadAssets()) {
		
			if (!screenStart) {
				initialize(true);
				screenStart = true;
			}
			
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);
			
			game.batch.begin();
			drawBackdrop();
			drawBitsAndGems();
			aSpikeObject.draw(game.batch);
			game.batch.end();
			
			controlSpike();
	
			//determine end of screen
			if (("left").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new SugarCubeCornerScreen(game, "normal", "right"), game);
				return;
			}
			
			//determine end of screen
			if (("right").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new PonyvilleStatueScreen(game, "", "left"), game);
				return;
			}
		}
	}

}
