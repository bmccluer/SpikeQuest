package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.interfaces.ButtonObjectAction;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.buttons.Direction;
import com.brendanmccluer.spikequest.objects.buttons.ImageButtonObject;
import com.brendanmccluer.spikequest.objects.buttons.NavigationButtonObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;


public class PonyvilleParkScreen extends AbstractSpikeQuestStandardScreen {
	private Texture treeTexture = null;
	private final static String TREE_PATH = "backdrop/treeCutOut.png";
	private boolean spikeReady = false;
	private NavigationButtonObject downButton = null;
	
	public PonyvilleParkScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		super(game, 1089, 613, 1000, "backdrop/ponyvillePark.png", aScreenType, aSpikePosition);
	}

	@Override
	public void show() {
		super.show();
		game.assetManager.setAsset(TREE_PATH, "Texture");
		if(SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.ACCESS_SWEET_APPLE_ACRES_PATH)) {
			downButton = new NavigationButtonObject(Direction.DOWN, this, "Sweet Apple Acres");
			downButton.setObjectDetection(spikeObject);
			downButton.setGdxInputInt(Input.Keys.DOWN);
			downButton.setButtonAction(new ButtonObjectAction() {
				@Override
				public void handle(AbstractSpikeQuestScreen screen) {
					screen.dispose();
					SpikeQuestScreenManager.forwardScreen(new SweetAppleAcresPathScreen(game, "normal", "right"), game);
				}
			});
		}

	}

	@Override
	public void render(float delta) {
		refresh();
		//useLoadingScreen(delta);
		if (loadAssets()) {
			if (!screenStart) {
				startScreen();
				if(downButton != null)
					downButton.setPosition(currentBackdropTexture.getWidth()/2, spikeObject.getCollisionHeadRectangle().getY() + spikeObject.getCollisionHeadRectangle().getHeight() + 50);
			}
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);
			game.batch.begin();
			drawBackdrop();
			drawBitsAndGems();
			spikeObject.draw(game.batch);
			game.batch.draw(treeTexture, gameCamera.getWorldWidth() - treeTexture.getWidth(), 0);
			if(downButton != null) {
				downButton.draw(game.batch);
			}
			game.batch.end();

			if(downButton != null) {
				downButton.update(delta,gameCamera);
				if(downButton.isClicked())
					return;
			}
			//move spike slighty further left
			if (!spikeReady && "right".equals(spikePosition)) {
				spikeObject.moveLeft(spikeObject.SPIKE_STANDARD_SPEED);
				
				if (spikeObject.getCurrentPositionX() <= gameCamera.getWorldWidth() - treeTexture.getWidth())
					spikeReady = true;
			}
			else
				controlSpike();

			//determine end of screen
			if(("right").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new SugarCubeCornerScreen(game, "", "left"), game);
				return;
			}
			if(("left").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new PonyvilleOutskirtsScreen(game, "", "right"), game);
				return;
			}

		}
		
	}
		
	protected void startScreen() {
		screenStart = true;
		treeTexture = (Texture) game.assetManager.loadAsset(TREE_PATH, "Texture");
		
		super.startScreen(false);
		
	}
	
	@Override
	public void dispose() {
		super.dispose();
		safeDispose(downButton);
		game.assetManager.disposeAsset(TREE_PATH);
	}

}
