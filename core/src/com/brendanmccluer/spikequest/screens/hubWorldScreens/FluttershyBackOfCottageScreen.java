package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.ponies.FluttershyObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class FluttershyBackOfCottageScreen extends AbstractSpikeQuestStandardScreen {
	private FluttershyObject fluttershyObject = null;

	public FluttershyBackOfCottageScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		super(game, 1259, 634, 1100, "backdrop/fluttershyBackOfCottage.png", aScreenType, aSpikePosition);
	}

    @Override
    public void initialize() {
        super.initialize();
        fluttershyObject = new FluttershyObject();
    }

    @Override
	public void render(float delta) {
		refresh();
		//useLoadingScreen(delta);
		if (game.assetManager.loadAssets() && loadAssets() && fluttershyObject.isLoaded()) {
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
			//fluttershyObject.draw(game.batch);
			game.batch.end();	
			controlSpike();
		}
	
		//determine end of screen
		if (("right").equals(getEdgeTouched())) {
			dispose();
			SpikeQuestScreenManager.forwardScreen(this, new FluttershyCottageScreen(game, " ", "left"), game);
		}
	}

    @Override
    public void dispose() {
        super.dispose();
        fluttershyObject.discard();
        fluttershyObject = null;
    }
}
