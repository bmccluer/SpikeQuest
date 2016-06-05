package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.ponies.PinkieObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class PonyvilleStatueScreen extends AbstractSpikeQuestStandardScreen {
	private PinkieObject aPinkieObject;
	
	PonyvilleStatueScreen (SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		
		super(game, 1024, 576, 1000, "backdrop/ponyvilleStatue.png", aScreenType, aSpikePosition);
		aPinkieObject = new PinkieObject();
	}
	
	@Override
	public void render(float delta) {
		refresh();
		//useLoadingScreen(delta);
		if (game.assetManager.loadAssets() && loadAssets() && aPinkieObject.isLoaded()) {
		
			if (!screenStart) {
				initialize(true);
				aPinkieObject.spawn(427.41937f, 79.91205f);
				aPinkieObject.resize(0.25f);
				screenStart = true;
			}
			
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);
			aPinkieObject.moveRight(0);
			game.batch.begin();
			drawBackdrop();
			drawBitsAndGems();
			aPinkieObject.draw(game.batch);
			aSpikeObject.draw(game.batch);
			game.batch.end();
			
			controlSpike();
	
			//determine end of screen
			if (("left").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new PonyvilleCityHallScreen(game, "", "right"), game);
				return;
			}
			
			//determine end of screen
			if (("right").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new PonyvilleParkScreen(game, "", "left"), game);
				return;
			}
		}
	}

}
