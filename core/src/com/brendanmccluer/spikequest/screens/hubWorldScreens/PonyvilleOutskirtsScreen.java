package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.ShyAndSeekIntroScreen;

public class PonyvilleOutskirtsScreen extends AbstractSpikeQuestStandardScreen {
	
	PonyvilleOutskirtsScreen (SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		
		super(game, 1550, 620, 1000, "backdrop/ponyvilleOutskirts.png", aScreenType, aSpikePosition);
		
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
				if (SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_SHY_AND_SEEK_COMPLETE_KEY))
					SpikeQuestScreenManager.forwardScreen(this, new FluttershyCottageScreen(game, "", "right"), game);
				else
					SpikeQuestScreenManager.forwardScreen(this, new ShyAndSeekIntroScreen(game, "", "right"), game);
				return;
			}
			
			//determine end of screen
			if (("right").equals(getEdgeTouched())) {
				SpikeQuestScreenManager.forwardScreen(this, new PonyvilleParkScreen(game, "", "left"), game);
				dispose();
				return;
			}
		}
	}

}
