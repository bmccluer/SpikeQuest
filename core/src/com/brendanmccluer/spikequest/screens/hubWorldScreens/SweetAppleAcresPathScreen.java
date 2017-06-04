package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class SweetAppleAcresPathScreen extends AbstractSpikeQuestStandardScreen {

	public SweetAppleAcresPathScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		super(game, 1843, 591, 1000, "backdrop/sweetAppleAcresPath.png", aScreenType, aSpikePosition);
	}
	
	@Override
	public void render(float delta) {
		refresh();
		//useLoadingScreen(delta);
		if (game.assetManager.loadAssets() && loadAssets()) {
		
			if (!screenStart) {
				startScreen(true);
				screenStart = true;
			}
			
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);
			
			game.batch.begin();
			drawBackdrop();
			drawBitsAndGems();
			spikeObject.draw(game.batch);
			game.batch.end();
			
			controlSpike();
	
			//determine end of screen
			if (("left").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(new OutsideCmcClubhouseScreen(game,null,"right"),game);
				return;
			}
			
			//determine end of screen
			if (("right").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(new PonyvilleParkScreen(game,"normal","center"),game);
				return;
			}
		}
	}

}
