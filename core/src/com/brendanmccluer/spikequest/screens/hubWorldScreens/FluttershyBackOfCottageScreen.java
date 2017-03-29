package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.dialog.SpikeQuestMultipleDialogController;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextObject;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.FluttershyObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class FluttershyBackOfCottageScreen extends AbstractSpikeQuestStandardScreen {
	public final String RAINBOW_RACE_TANK_INTRO_BOOLEAN = "rainbowRaceIntroComplete";
	public final String FLUTTERSHY_TANK_INTRO_BOOLEAN = "fluttershyTankIntroComplete";
	private FluttershyObject fluttershyObject = null;
    private TankObject tankObject = null;
	private SpikeQuestMultipleDialogController dialogController = null;
	private boolean spikeReady;


	public FluttershyBackOfCottageScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		super(game, 1259, 634, 1100, "backdrop/fluttershyBackOfCottage.png", aScreenType, aSpikePosition);
	}

    @Override
    public void initialize() {
        super.initialize();
		if (SpikeQuestSaveFile.getBooleanValue(RAINBOW_RACE_TANK_INTRO_BOOLEAN) && !SpikeQuestSaveFile.getBooleanValue(FLUTTERSHY_TANK_INTRO_BOOLEAN)) {
			setScreenType("tankIntro");
			fluttershyObject = new FluttershyObject();
            tankObject = new TankObject();
			dialogController = new SpikeQuestMultipleDialogController("dialog/rainbowRaceStory/fluttershyTankDialog.txt",
					new SpikeQuestTextObject(spikeObject, "Spike"), new SpikeQuestTextObject(fluttershyObject, "Fluttershy"),
					new SpikeQuestTextObject(tankObject, "Tank"));
		}
		else
			setScreenType("normal");

    }

    @Override
	public void render(float delta) {
		refresh();
		if (("tankIntro").equals(screenType))
			renderTankIntro(delta);
		else
			renderNormal(delta);

	}

	private void renderNormal(float delta) {
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

	private void renderTankIntro(float delta) {
		if (game.assetManager.loadAssets() && loadAssets() && fluttershyObject.isLoaded() && dialogController.isLoaded() && tankObject.isLoaded()) {
			if (!screenStart) {
				startScreen(false);
				fluttershyObject.spawn(gameCamera.getCameraWidth()/2 - 250, -10);
                fluttershyObject.moveLeft(0);
                tankObject.spawn(spikeObject.getCenterX() + 100, 100);
                tankObject.setSize(0.25f);
                tankObject.setGravity(0);
			}
            //attach to batch after initialization
            gameCamera.attachToBatch(game.batch);
			if (!spikeReady) {
				spikeObject.moveLeft(delta * 250);
                tankObject.moveLeft(delta * 250);
				if (spikeObject.getCenterX() < gameCamera.getCameraWidth()/2 + 100) {
                    spikeReady = true;
                }
			}
            if (spikeReady)
				dialogController.updateTextAndObjects(delta);
            tankObject.hover(100, 150, 25 * delta);
            game.batch.begin();
            drawBackdrop();
            fluttershyObject.draw(game.batch);
            spikeObject.draw(game.batch);
            tankObject.draw(game.batch);
            if (spikeReady)
                dialogController.drawText(game.batch);
            game.batch.end();
		}
	}

	@Override
    public void dispose() {
        super.dispose();
        if (fluttershyObject != null)
			fluttershyObject.discard();
        if (tankObject != null)
            tankObject.discard();
		if (dialogController != null)
			dialogController.discard();
    }
}
