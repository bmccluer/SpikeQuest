package com.brendanmccluer.spikequest.screens.gameScreens;

import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.common.objects.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.ponies.FluttershyObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class ShyAndSeekIntroScreen extends AbstractSpikeQuestStandardScreen {
	private FluttershyObject aFluttershyObject = new FluttershyObject();
	private SpikeQuestTextBalloon aFluttershyTextBalloon = new SpikeQuestTextBalloon("dialog/fluttershyGameDialog.txt");
	private SpikeQuestTextBalloon aSpikeTextBalloon = new SpikeQuestTextBalloon("dialog/fluttershyGameDialog.txt");
	private SpikeQuestDialogController aDialogController = new SpikeQuestDialogController(aFluttershyObject, aFluttershyTextBalloon, "Fluttershy", 5,
			aSpikeObject,aSpikeTextBalloon, "Spike", 4);
	private boolean spikeReady = false;
	private boolean fluttershyReady = false;
	
	public ShyAndSeekIntroScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		
		super(game, 1055, 594, 1000, "backdrop/fluttershyCottage.png", aScreenType, aSpikePosition);
		
	}

	@Override
	public void render(float delta) {
		refresh();
		useLoadingScreen(delta);
		if (game.assetManager.loadAssets() && loadAssets() && aDialogController.areTextBalloonsLoaded() && aFluttershyObject.isLoaded()) {
		
			if (!screenStart) {
				initialize(false);
				
				//spawn spike slightly below bottom and to the right
				aSpikeObject.spawn(gameCamera.getCameraWidth() + 200, -20);
				aFluttershyObject.spawn(gameCamera.getCameraWidth()/2, -20);
				aFluttershyObject.moveLeft(0);
				aFluttershyObject.standStill();
				
				screenStart = true;
			}
			
			
			if (!spikeReady && fluttershyReady) {
				
				aSpikeObject.moveLeft(5);
				spikeReady = aSpikeObject.getCurrentPositionX() < gameCamera.getWorldWidth() - aSpikeObject.getCollisionRectangle().getWidth() - 150;
				
				if (spikeReady) {
					aFluttershyObject.moveRight(0);
					aFluttershyObject.standStill();
				}
			}
			else
				aDialogController.setTextBalloonDefaultPositionsOverObjects(aFluttershyObject, aSpikeObject);
			
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);
			
			game.batch.begin();
			drawBackdrop();
			
			if (spikeReady || !fluttershyReady) 
				aDialogController.drawTheDialogAndAnimateObjects(game.batch, aFluttershyObject, aSpikeObject);
				
			if (!fluttershyReady) {
				
				fluttershyReady = aDialogController.firstTextIndex == 1;
				
				//make fluttershy stop talking
				if (fluttershyReady)
					aFluttershyObject.standStill();
				
			}
				
			
			aSpikeObject.draw(game.batch);
			aFluttershyObject.draw(game.batch);
			game.batch.end();
			
			if (aDialogController.areTextBalloonsFinished()) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new ShyAndSeekInstructionScreen(game), game);
			}
				
		}
	

	}

}
