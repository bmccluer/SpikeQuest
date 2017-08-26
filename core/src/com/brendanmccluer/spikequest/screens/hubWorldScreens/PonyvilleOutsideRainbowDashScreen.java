package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.brendanmccluer.spikequest.SpikeQuestDialogController;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.dialog.SpikeQuestMultipleDialogController;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextObject;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.RainbowDashObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.RainbowRaceInstructionsScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.RainbowRaceScreen;

public class PonyvilleOutsideRainbowDashScreen extends AbstractSpikeQuestStandardScreen {
	private static final String RAINBOW_RACE_INTRO_DIALOG = "dialog/rainbowRaceStory/rainbowIntroDialog.txt";
    private static final String RAINBOW_RACE_TANK_INTRO_DIALOG = "dialog/rainbowRaceStory/rainbowTankIntroDialog.txt";
    public static final String RAINBOW_RACE_TANK_INTRO_BOOLEAN = "rainbowRaceIntroComplete";
    public static final String SCREEN_TYPE_TANK_INTRO = "rainbowRaceIntro";
    public static final String SCREEN_TYPE_RACE_INTRO = "rainbowRaceStart";
    private RainbowDashObject rainbowDashObject;
    private TankObject tankObject;
    private SpikeQuestMultipleDialogController multiDialogController = null;
    private SpikeQuestDialogController dialogController, rainbowTankDialogController;
    private boolean startFlag = false;


	public PonyvilleOutsideRainbowDashScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		super(game, 1191, 670, 1000, "backdrop/ponyvilleRoadOutsideRainbowDash.png", aScreenType, aSpikePosition);
        if(screenType != null && screenType.isEmpty()) {
            if(!SpikeQuestSaveFile.getBooleanValue(RAINBOW_RACE_TANK_INTRO_BOOLEAN))
                setScreenType(SCREEN_TYPE_TANK_INTRO);
            else if(!SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.RAINBOW_RACE_COMPLETE))
                setScreenType(SCREEN_TYPE_RACE_INTRO);
            else
                setScreenType("normal");
        }
	}

	@Override
	public void show() {
		super.show();
		rainbowDashObject = new RainbowDashObject();
        tankObject = new TankObject();
        if(SCREEN_TYPE_TANK_INTRO.equals(screenType)) {
            SpikeQuestTextBalloon spikeTextBalloon = new SpikeQuestTextBalloon(RAINBOW_RACE_INTRO_DIALOG);
            SpikeQuestTextBalloon rainbowTextBalloon = new SpikeQuestTextBalloon(RAINBOW_RACE_INTRO_DIALOG);
            dialogController = new SpikeQuestDialogController(spikeObject, spikeTextBalloon, "Spike",7, rainbowDashObject, rainbowTextBalloon, "Rainbowdash", 6);
            //load second dialog controller
            SpikeQuestTextBalloon rainbowTextBalloon2 = new SpikeQuestTextBalloon(RAINBOW_RACE_TANK_INTRO_DIALOG);
            SpikeQuestTextBalloon tankTextBalloon = new SpikeQuestTextBalloon(RAINBOW_RACE_TANK_INTRO_DIALOG);
            rainbowTankDialogController = new SpikeQuestDialogController(rainbowDashObject, rainbowTextBalloon2, "Rainbowdash", 1, tankObject, tankTextBalloon, "Tank", 0);
            rainbowTankDialogController.useTimer();
        }
        else if(SCREEN_TYPE_RACE_INTRO.equals(screenType)) {
            multiDialogController = new SpikeQuestMultipleDialogController("dialog/rainbowRaceStory/rainbowStartGameDialog.txt",
                    new SpikeQuestTextObject(spikeObject, "Spike"),
                    new SpikeQuestTextObject(rainbowDashObject, "RainbowDash"),
                    new SpikeQuestTextObject(tankObject, "Tank"));
        }
	}

	@Override
	public void render(float delta) {
        gameCamera.attachToBatch(game.batch);
        refresh();
		useLoadingScreen(delta);
        if (SCREEN_TYPE_TANK_INTRO.equals(screenType))
            renderRainbowRaceIntro(delta);
		else if(SCREEN_TYPE_RACE_INTRO.equals(screenType))
            renderGameStart(delta);
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
            if (!rainbowTankDialogController.areTextBalloonsFinished())
                rainbowTankDialogController.drawTheDialogAndAnimateObjects(game.batch, rainbowDashObject, tankObject);
            if (startFlag && !dialogController.areTextBalloonsFinished())
                dialogController.drawTheDialogAndAnimateObjects(game.batch, spikeObject, rainbowDashObject);
            game.batch.end();
        }
    }

    private void renderGameStart(float delta) {
        if(game.assetManager.loadAssets() && multiDialogController.isLoaded() && spikeObject.isLoaded() && rainbowDashObject.isLoaded() && tankObject.isLoaded()) {
            if(!screenStart) {
                spikeObject.spawn(-300,0);
                tankObject.spawn(-300,0);
                tankObject.setWeight(0);
                tankObject.setSize(0.25f);
                tankObject.controlsDisabled = true;
                rainbowDashObject.spawn(0,0);
                rainbowDashObject.setSize(0.45f);
                rainbowDashObject.setCurrentPositionX(gameCamera.getCameraWidth() - rainbowDashObject.getCollisionRectangle().getWidth() - 250);
                rainbowDashObject.moveLeft(0);
                startScreen(false);
            }
            multiDialogController.updateTextAndObjects(delta);
            if(multiDialogController.areTextBalloonsFinished()) {
                dispose();
                RainbowRaceScreen raceScreen = new RainbowRaceScreen(game);
                raceScreen.setScreenType(RainbowRaceScreen.SCREEN_TYPE_FIRST_TIME_PLAY);
                game.screenStack.push(raceScreen);
                game.screenStack.push(new RainbowRaceInstructionsScreen(game));
                SpikeQuestScreenManager.popNextScreen(game);
                return;
            }
            tankObject.hover(100, 150, 25 * delta);
            game.batch.begin();
            drawBackdrop();
            rainbowDashObject.draw(game.batch);
            spikeObject.draw(game.batch);
            tankObject.draw(game.batch);
            multiDialogController.drawText(game.batch);
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
		safeDispose(rainbowDashObject, tankObject, dialogController, rainbowTankDialogController,
                multiDialogController);
	}
}
