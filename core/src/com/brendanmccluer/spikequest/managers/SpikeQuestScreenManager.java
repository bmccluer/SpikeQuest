package com.brendanmccluer.spikequest.managers;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.interfaces.SpikeQuestScreen;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.screens.MainMenuScreen;
import com.brendanmccluer.spikequest.screens.gameIntroScreens.CliffBottomScreen;
import com.brendanmccluer.spikequest.screens.gameIntroScreens.IntroScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.BalloonGameIntroScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.BalloonGameScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.ShyAndSeekOutroScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.ShyAndSeekScreen;
import com.brendanmccluer.spikequest.screens.hubWorldScreens.FluttershyBackOfCottageScreen;
import com.brendanmccluer.spikequest.screens.hubWorldScreens.PonyvilleOutsideRainbowDashScreen;
import com.brendanmccluer.spikequest.screens.hubWorldScreens.PonyvilleParkScreen;
import com.brendanmccluer.spikequest.screens.hubWorldScreens.SugarCubeCornerScreen;

/**
 * I determine which screens to set
 * 
 * @author Brendan
 *
 */
public class SpikeQuestScreenManager {
	/**
	 * I read the save file and determine which scene to set next. I also set
	 * both screens passed to null.
	 * ASSUMES SCREENS HAVE ALREADY CALLED DISCARD FUNCTION
	 * 
	 * @param aCallingScreen
	 * @param aGame
     * TODO Use Screen Stack
	 */
	public static void popNextScreen(AbstractSpikeQuestScreen aCallingScreen, SpikeQuestGame aGame) {
		if (aGame.debugMode) {
			AbstractSpikeQuestScreen debugScreen;
			SpikeQuestSaveFile.setBooleanValue(PonyvilleOutsideRainbowDashScreen.RAINBOW_RACE_TANK_INTRO_BOOLEAN, true);
			SpikeQuestSaveFile.setBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_COMPLETE_KEY, true);
			SpikeQuestSaveFile.setBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_NORMAL_KEY, true);
			SpikeQuestSaveFile.setBooleanValue(SpikeQuestSaveFile.IS_SHY_AND_SEEK_COMPLETE_KEY, true);
			SpikeQuestSaveFile.setBooleanValue(SpikeQuestSaveFile.FLUTTERSHY_TANK_INTRO_COMPLETE, false);
			SpikeQuestSaveFile.setBooleanValue(SpikeQuestSaveFile.ACCESS_SWEET_APPLE_ACRES_PATH, true);
			debugScreen = new PonyvilleParkScreen(aGame,"","left");
			forwardScreen(debugScreen, aGame);
			return;
		}

        //TODO; CREATE SOME SORT OF SAVE FILE CALCULATOR
		//MainMenuScreen is calling
		if (aCallingScreen instanceof MainMenuScreen) 
			handleMainMenuScreen((MainMenuScreen) aCallingScreen, aGame);
		
		//CliffBottom calling
		else if (aCallingScreen instanceof CliffBottomScreen) {
			aGame.screenStack.push(new SugarCubeCornerScreen(aGame,"intro","right"));
			forwardScreen(aCallingScreen, new BalloonGameIntroScreen(aGame), aGame);

		}

		//BalloonGameIntro calling
		else if (aCallingScreen instanceof BalloonGameIntroScreen)
			setBalloonGameScreen(aGame);
			
		//BalloonGameScreen calling
		else if (aCallingScreen instanceof BalloonGameScreen) 
			handleBalloonGameScreen(aGame);
			
		else if (aCallingScreen instanceof ShyAndSeekScreen)
			handleShyAndSeekScreen(aGame);
		/*//SugarCubeCorner Screen calling
		else if (aCallingScreen instanceof SugarCubeCornerScreen)
			handleSugarCubeCornerScreen(aCallingScreen, aGame);*/
			
		aCallingScreen = null;
	}
	

	private static void handleMainMenuScreen(MainMenuScreen aMainMenuScreen, SpikeQuestGame aGame) {
		
		//continue game
		if (aMainMenuScreen.continueButtonPressed) { 

			//done talking to Fluttershy about Tank but not CMCs
			if (!SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.CMC_TANK_INTRO_COMPLETE) && SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.FLUTTERSHY_TANK_INTRO_COMPLETE))
				forwardScreen(new FluttershyBackOfCottageScreen(aGame, "normal", "left"), aGame);
			//done with Fluttershy Game for first time
			else if (SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_COMPLETE_KEY) && SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_SHY_AND_SEEK_COMPLETE_KEY)
					&& !SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.RAINBOW_RACE_INTRO_COMPLETE))
				forwardScreen(aMainMenuScreen, new FluttershyBackOfCottageScreen(aGame, "normal", "left"), aGame);
			//Not done with Balloon game for first time
			else if (!SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_COMPLETE_KEY)) {
				aGame.screenStack.push(new SugarCubeCornerScreen(aGame, "intro", "right"));
				forwardScreen(aMainMenuScreen, new BalloonGameIntroScreen(aGame), aGame);
			}
			//default
			else
				setSugarCubeCornerScreen(aGame);

		}
		//new game
		else 
			forwardScreen(aMainMenuScreen, new IntroScreen(aGame), aGame);
	}



	private static void handleBalloonGameScreen(SpikeQuestGame aGame) {
		
		if (SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_COMPLETE_KEY)) 
			setSugarCubeCornerScreen(aGame);
		
		else
			setBalloonGameScreen(aGame);
	}



	private static void handleShyAndSeekScreen(SpikeQuestGame aGame) {
		SpikeQuestSaveFile.setBooleanValue(SpikeQuestSaveFile.IS_SHY_AND_SEEK_COMPLETE_KEY, true);
        forwardScreen(null, new ShyAndSeekOutroScreen(aGame, "normal", "right"), aGame);
	}


	private static void setBalloonGameScreen(SpikeQuestGame aGame) {
		BalloonGameScreen aBalloonGameScreen = null;
		
		if (SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_NORMAL_KEY))
			aBalloonGameScreen = new BalloonGameScreen(aGame, "normal");
		
		else
			aBalloonGameScreen = new BalloonGameScreen(aGame, "firstPlay");
		
		forwardScreen(null, aBalloonGameScreen, aGame);
	}



	/**
	 * I read the save file and determine which scene to set next. I also set
	 * both screens passed to null. DOES NOT dispose calling screen
	 * 
	 * @param aCallingScreen
	 * @param aScreenToNull
	 * @param aGame
     * @deprecated
	 */
	public static void popNextScreen(AbstractSpikeQuestScreen aCallingScreen, AbstractSpikeQuestScreen aScreenToNull, SpikeQuestGame aGame) {
		//null second screen
		aScreenToNull = null;
		popNextScreen(aCallingScreen, aGame);
	
	}
	
	/**
	 * I forward the screen passed and null the callingScreen. I DO NOT dispose the calling screen.
	 * 
	 * @param aCallingScreen
	 * @param aGame
     * @deprecated
	 */
	public static void forwardScreen (AbstractSpikeQuestScreen aCallingScreen, AbstractSpikeQuestScreen aScreenToForward,  SpikeQuestGame aGame) {

        aScreenToForward.initialize();
		aGame.setScreen(aScreenToForward);
	
	}

	public static void forwardScreen (AbstractSpikeQuestScreen aScreen, SpikeQuestGame aGame) {
		aScreen.initialize();
		aGame.setScreen(aScreen);
	}

    /**
     * I pop the next screen from the stack and set to the game
     * @param aGame
     */
	public static void popNextScreen(SpikeQuestGame aGame) {
        if (!aGame.screenStack.isEmpty()) {
            SpikeQuestScreen nextScreen = aGame.screenStack.pop();
            try {
                nextScreen.initialize();
                aGame.setScreen(nextScreen);
            }
            catch (Exception e) {
                //TODO; CREATE ERROR SCREEN??
                e.printStackTrace();
            }
            return;
        }
        //Go back to Main Menu if no screens left or error
		MainMenuScreen mainMenuScreen = new MainMenuScreen(aGame);
		mainMenuScreen.initialize();
        aGame.setScreen(mainMenuScreen);
    }
	
	
	/**
	 * I set the screen from the Balloon Game
	 * 
	 * @param aGame
	 */
	private static void setSugarCubeCornerScreen(SpikeQuestGame aGame) {
		SugarCubeCornerScreen aSugarCubeCornerScreen = null;
		
		if (SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_COMPLETE_KEY))
			aSugarCubeCornerScreen = new SugarCubeCornerScreen(aGame, "normal", "right");
		else 
			aSugarCubeCornerScreen = new SugarCubeCornerScreen(aGame, "intro", "right");

		forwardScreen(null, aSugarCubeCornerScreen, aGame);
	}
}
