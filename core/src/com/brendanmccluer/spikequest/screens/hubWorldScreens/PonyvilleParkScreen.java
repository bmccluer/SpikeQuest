package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;


public class PonyvilleParkScreen extends AbstractSpikeQuestStandardScreen {
	private Texture treeTexture = null;
	private final static String TREE_PATH = "backdrop/treeCutOut.png";
	private boolean spikeReady = false;
	
	public PonyvilleParkScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		super(game, 1089, 613, 1000, "backdrop/ponyvillePark.png", aScreenType, aSpikePosition);
		
		game.assetManager.setAsset(TREE_PATH, "Texture");
		
	}

	@Override
	public void render(float delta) {
		refresh();
		//useLoadingScreen(delta);
		if (loadAssets()) {
			
			if (!screenStart)
				initialize();
			
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);
			
			game.batch.begin();
			drawBackdrop();
			drawBitsAndGems();
			aSpikeObject.draw(game.batch);
			game.batch.draw(treeTexture, gameCamera.getWorldWidth() - treeTexture.getWidth(), 0);
			game.batch.end();
			
			//move spike slighty further left
			if (!spikeReady && "right".equals(spikePosition)) {
				aSpikeObject.moveLeft(aSpikeObject.SPIKE_STANDARD_SPEED);
				
				if (aSpikeObject.getCurrentPositionX() <= gameCamera.getWorldWidth() - treeTexture.getWidth())
					spikeReady = true;
			}
			
			else
				controlSpike();
			
			//determine end of screen
			if (("right").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new SugarCubeCornerScreen(game, "", "left"), game);
				return;
			}
			
			if (("left").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new PonyvilleOutskirtsScreen(game, "", "right"), game);
				return;
			}
			
		
		}
		
	}
		
	protected void initialize () {
		screenStart = true;
		treeTexture = (Texture) game.assetManager.loadAsset(TREE_PATH, "Texture");
		
		super.initialize(false);
		
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		game.assetManager.disposeAsset(TREE_PATH);
	}

}
