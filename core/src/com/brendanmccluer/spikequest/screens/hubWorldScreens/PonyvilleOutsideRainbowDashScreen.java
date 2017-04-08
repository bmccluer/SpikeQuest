package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.RainbowDashObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class PonyvilleOutsideRainbowDashScreen extends AbstractSpikeQuestStandardScreen {
	private static final String RAINBOW_RACE_INTRO_DIALOG = "dialog/rainbowRaceStory/rainbowIntroDialog.txt";
    private static final String RAINBOW_RACE_TANK_INTRO_DIALOG = "dialog/rainbowRaceStory/rainbowTankIntroDialog.txt";
    public static final String RAINBOW_RACE_TANK_INTRO_BOOLEAN = "rainbowRaceIntroComplete";
    private RainbowDashObject rainbowDashObject;
    private TankObject tankObject;
    private SpikeQuestDialogController dialogController, rainbowTankDialogController;
    private boolean startFlag = false;


	public PonyvilleOutsideRainbowDashScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		super(game, 1191, 670, 1000, "backdrop/ponyvilleRoadOutsideRainbowDash.png", aScreenType, aSpikePosition);
	}

	@Override
	public void initialize() {
		super.initialize();
		rainbowDashObject = new RainbowDashObject();
        tankObject = new TankObject();
        if (SpikeQuestSaveFile.getBooleanValue(RAINBOW_RACE_TANK_INTRO_BOOLEAN))
            setScreenType("normal");
        else {
            setScreenType("rainbowRaceIntro");
            SpikeQuestTextBalloon spikeTextBalloon = new SpikeQuestTextBalloon(RAINBOW_RACE_INTRO_DIALOG);
            SpikeQuestTextBalloon rainbowTextBalloon = new SpikeQuestTextBalloon(RAINBOW_RACE_INTRO_DIALOG);
            dialogController = new SpikeQuestDialogController(spikeObject, spikeTextBalloon, "Spike",7, rainbowDashObject, rainbowTextBalloon, "Rainbowdash", 6);
            //load second dialog controller
            SpikeQuestTextBalloon rainbowTextBalloon2 = new SpikeQuestTextBalloon(RAINBOW_RACE_TANK_INTRO_DIALOG);
            SpikeQuestTextBalloon tankTextBalloon = new SpikeQuestTextBalloon(RAINBOW_RACE_TANK_INTRO_DIALOG);
            rainbowTankDialogController = new SpikeQuestDialogController(rainbowDashObject, rainbowTextBalloon2, "Rainbowdash", 1, tankObject, tankTextBalloon, "Tank", 0);
            rainbowTankDialogController.useTimer();
        }

	}

	@Override
	public void render(float delta) {
        gameCamera.attachToBatch(game.batch);
        refresh();
		useLoadingScreen(delta);

        if (("rainbowRaceIntro").equals(screenType))
            renderRainbowRaceIntro(delta);
		else
            renderNormal(delta);
	}

    private void renderNormal(float delta) {
        if (game.assetManager.loadAssets() && loadAssets() && rainbowDashObject.isLoaded()) {

            if (!screenStart) {
                startScreen(true);
                rainbowDashObject.spawn(0,0);
                rainbowDashObject.setSize(0.45f);
                rainbowDashObject.setCurrentPositionX(gameCamera.getCameraWidth() - rainbowDashObject.getCollisionRectangle().getWidth() - 250);
                rainbowDashObject.moveLeft(0);
                rainbowDashObject.standStill();
            }

            if (determineEndOfScreen())
                return;
            controlSpike();

            //Update after moving Spike
            gameCamera.attachToBatch(game.batch);

            game.batch.begin();
            drawBackdrop();
            drawBitsAndGems();
            rainbowDashObject.draw(game.batch);
            spikeObject.draw(game.batch);
            game.batch.end();
        }
    }

    private void renderRainbowRaceIntro(float delta) {
        if (game.assetManager.loadAssets() && loadAssets() && rainbowDashObject.isLoaded() && tankObject.isLoaded() && dialogController.areTextBalloonsLoaded() && rainbowTankDialogController.areTextBalloonsLoaded()) {

            if (!screenStart) {
                startScreen(false);
                spikeObject.setCurrentPositionX(-100);
                rainbowDashObject.spawn(0,0);
                rainbowDashObject.setSize(0.45f);
                rainbowDashObject.setCurrentPositionX(gameCamera.getCameraWidth() - rainbowDashObject.getCollisionRectangle().getWidth() - 250);
                tankObject.spawn(rainbowDashObject.getCenterX() + 200, 100);
                tankObject.setSize(0.25f);
                tankObject.setWeight(0);
                rainbowTankDialogController.setTextBalloonDefaultPositionsOverObjects(rainbowDashObject, tankObject);
                rainbowTankDialogController.setSecondTextBalloonPositionY(rainbowTankDialogController.getSecondTextBalloonPositionY() - 100);
                screenStart = true;
            }

            if (!startFlag && rainbowTankDialogController.areTextBalloonsFinished()) {
                if (spikeObject.getCenterX() <= 300) {
                    spikeObject.moveRight(200 * delta);
                    rainbowDashObject.standStill();
                }
                else {
                    rainbowDashObject.moveLeft(0);
                    rainbowDashObject.standStill();
                    dialogController.setTextBalloonDefaultPositionsOverObjects(spikeObject, rainbowDashObject);
                    startFlag = true;
                }
            }

            if (dialogController.areTextBalloonsFinished()) {
                spikeObject.moveLeft(200 * delta);
                tankObject.moveTowardsPoint(-25, spikeObject.getCenterY(), 300 * delta);
            }
            else
                tankObject.hover(100, 150, 25 * delta);

            if (dialogController.areTextBalloonsFinished() && determineEndOfScreen()) {
                SpikeQuestSaveFile.setBooleanValue(RAINBOW_RACE_TANK_INTRO_BOOLEAN, true);
                return;
            }

            //attach to batch after initialization
            gameCamera.attachToBatch(game.batch);
            game.batch.begin();
            drawBackdrop();
            drawBitsAndGems();
            rainbowDashObject.draw(game.batch);
            spikeObject.draw(game.batch);
            tankObject.draw(game.batch);
            rainbowTankDialogController.drawTheDialogAndAnimateObjects(game.batch, rainbowDashObject, tankObject);
            if (startFlag && !dialogController.areTextBalloonsFinished())
                dialogController.drawTheDialogAndAnimateObjects(game.batch, spikeObject, rainbowDashObject);
            game.batch.end();
        }
    }

    public boolean determineEndOfScreen() {
        //determine end of screen
        if (("left").equals(getEdgeTouched())) {
            dispose();
            SpikeQuestScreenManager.forwardScreen(new PonyvilleStatueScreen(game, "", "right"), game);
            return true;
        }

		/*	//determine end of screen
			if (("right").equals(getEdgeTouched())) {
				//do nothing
			}*/
        return false;
    }

    @Override
	public void dispose() {
		super.dispose();
		rainbowDashObject.dispose();

        if (tankObject != null)
            tankObject.dispose();
        if (dialogController != null)
            dialogController.dispose();
        if (rainbowTankDialogController != null)
            rainbowTankDialogController.dispose();
	}
}
