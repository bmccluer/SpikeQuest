package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.ponies.FluttershyObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class FluttershyCottageScreen extends AbstractSpikeQuestStandardScreen {
	private FluttershyObject aFluttershyObject = new FluttershyObject();
	public FluttershyCottageScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		
		super(game, 1055, 594, 1000, "backdrop/fluttershyCottage.png", aScreenType, aSpikePosition);
		
	}

	@Override
	public void render(float delta) {
		refresh();
		//useLoadingScreen(delta);
		if (game.assetManager.loadAssets() && loadAssets() && aFluttershyObject.isLoaded()) {
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
			//aFluttershyObject.draw(game.batch);
			game.batch.end();	
			controlSpike();
		}
	
		//determine end of screen
		if (("right").equals(getEdgeTouched())) {
			dispose();
			SpikeQuestScreenManager.forwardScreen(this, new PonyvilleOutskirtsScreen(game, " ", "left"), game);
			return;
		}
		if (("left").equals(getEdgeTouched())) {
			dispose();
			SpikeQuestScreenManager.forwardScreen(this, new FluttershyBackOfCottageScreen(game, " ", "right"), game);
			return;
		}

	}

}
