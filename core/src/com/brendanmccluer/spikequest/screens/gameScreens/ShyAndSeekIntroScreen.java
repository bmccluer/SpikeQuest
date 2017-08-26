package com.brendanmccluer.spikequest.screens.gameScreens;

import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.ponies.FluttershyObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class ShyAndSeekIntroScreen extends AbstractSpikeQuestStandardScreen {
	private FluttershyObject aFluttershyObject = null;
	private SpikeQuestTextBalloon aFluttershyTextBalloon = null;
	private SpikeQuestTextBalloon aSpikeTextBalloon = null;
	private SpikeQuestDialogController aDialogController = null;
	private boolean spikeReady = false;
	private boolean fluttershyReady = false;
	
	public ShyAndSeekIntroScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		super(game, 1055, 594, 1000, "backdrop/fluttershyCottage.png", aScreenType, aSpikePosition);
	}

	@Override
	public void show() {
		super.show();
		aFluttershyObject = new FluttershyObject();
		aFluttershyTextBalloon = new SpikeQuestTextBalloon("dialog/fluttershyGameDialog.txt");
		aSpikeTextBalloon = new SpikeQuestTextBalloon("dialog/fluttershyGameDialog.txt");
		aDialogController = new SpikeQuestDialogController(aFluttershyObject, aFluttershyTextBalloon, "Fluttershy", 5,
                spikeObject,aSpikeTextBalloon, "Spike", 4);
	}

	@Override
	public void render(float delta) {
		refresh();
		useLoadingScreen(delta);
		if (game.assetManager.loadAssets() && loadAssets() && aDialogController.areTextBalloonsLoaded() && aFluttershyObject.isLoaded()) {
		
			if (!screenStart) {
				startScreen(false);
				
				//spawn spike slightly below bottom and to the right
				spikeObject.spawn(gameCamera.getCameraWidth() + 200, -20);
				aFluttershyObject.spawn(gameCamera.getCameraWidth()/2, -20);
				aFluttershyObject.moveLeft(0);
				aFluttershyObject.standStill();
			}
			
			
			if (!spikeReady && fluttershyReady) {
				
				spikeObject.moveLeft(5);
				spikeReady = spikeObject.getCurrentPositionX() < gameCamera.getWorldWidth() - spikeObject.getCollisionRectangle().getWidth() - 150;
				
				if (spikeReady) {
					aFluttershyObject.moveRight(0);
					aFluttershyObject.standStill();
				}
			}
			else
				aDialogController.setTextBalloonDefaultPositionsOverObjects(aFluttershyObject, spikeObject);
			
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);
			
			game.batch.begin();
			drawBackdrop();
			
			if (spikeReady || !fluttershyReady) 
				aDialogController.drawTheDialogAndAnimateObjects(game.batch, aFluttershyObject, spikeObject);
				
			if (!fluttershyReady) {
				
				fluttershyReady = aDialogController.firstTextIndex == 1;
				
				//make fluttershy stop talking
				if (fluttershyReady)
					aFluttershyObject.standStill();
				
			}

			spikeObject.draw(game.batch);
			aFluttershyObject.draw(game.batch);
			game.batch.end();
			
			if (aDialogController.areTextBalloonsFinished()) {
				dispose();
				//SpikeQuestScreenManager.forwardScreen(this, new ShyAndSeekInstructionScreen(game), game);
				game.screenStack.push(new ShyAndSeekOutroScreen(game, "normal", "right"));
				game.screenStack.push(new ShyAndSeekInstructionScreen(game));
				SpikeQuestScreenManager.popNextScreen(game);
			}
				
		}

	}

	@Override
	public void dispose() {
		super.dispose();
		aDialogController.dispose();
		aFluttershyObject.dispose();
		aFluttershyObject = null;
		aDialogController = null;
	}
}
