package com.brendanmccluer.spikequest.screens.gameScreens;

import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.ponies.FluttershyObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;
import com.brendanmccluer.spikequest.screens.hubWorldScreens.FluttershyBackOfCottageScreen;

public class ShyAndSeekOutroScreen extends AbstractSpikeQuestStandardScreen {
	private FluttershyObject aFluttershyObject = new FluttershyObject();
	private SpikeQuestTextBalloon aFluttershyTextBalloon = new SpikeQuestTextBalloon("dialog/endFluttershyGameDialog.txt");
	private SpikeQuestTextBalloon aSpikeTextBalloon = new SpikeQuestTextBalloon("dialog/endFluttershyGameDialog.txt");
	private SpikeQuestDialogController aDialogController = new SpikeQuestDialogController(aFluttershyObject, aFluttershyTextBalloon, "Fluttershy", 6,
			spikeObject,aSpikeTextBalloon, "Spike", 5);
	private boolean spikeTurnLeft = false;
	
	public ShyAndSeekOutroScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		
		super(game, 1259, 634, 1100, "backdrop/fluttershyBackOfCottage.png", aScreenType, aSpikePosition);
	}

	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public void render(float delta) {
		refresh();
		useLoadingScreen(delta);
		if (game.assetManager.loadAssets() && loadAssets() && aDialogController.areTextBalloonsLoaded() && aFluttershyObject.isLoaded()) {
		
			if (!screenStart) {
				startScreen(false);
				//spawn spike slightly below bottom and to the right
				spikeObject.spawn(gameCamera.getWorldWidth() - spikeObject.getCollisionRectangle().getWidth() - 150, -10);
				aFluttershyObject.spawn(gameCamera.getCameraWidth()/2, -10);
				spikeObject.moveLeft(0);
				aFluttershyObject.standStill();
				
				screenStart = true;
			}
			aDialogController.setTextBalloonDefaultPositionsOverObjects(aFluttershyObject, spikeObject);
			
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);
			
			game.batch.begin();
			drawBackdrop();
			
			/*if (spikeReady || !fluttershyReady) */
			if ((aDialogController.firstTextIndex == 5 && aDialogController.secondTextIndex == 5) 
					&& spikeObject.getCurrentPositionX() <= gameCamera.getWorldWidth() - 100) {
				spikeObject.moveRight(5);
				spikeTurnLeft = true;
			}
			else {
				aDialogController.drawTheDialogAndAnimateObjects(game.batch, aFluttershyObject, spikeObject);
				if (spikeTurnLeft) {
					spikeObject.moveLeft(0);
					spikeTurnLeft = false;
				}
			}
			spikeObject.draw(game.batch);
			aFluttershyObject.draw(game.batch);
			game.batch.end();
			
			if (aDialogController.areTextBalloonsFinished()) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new FluttershyBackOfCottageScreen(game,"","right"), game);
			}
				
		}
	

	}

}
