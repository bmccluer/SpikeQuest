package com.brendanmccluer.spikequest.managers;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.screens.MainMenuScreen;
import com.brendanmccluer.spikequest.screens.gameIntroScreens.CliffBottomScreen;
import com.brendanmccluer.spikequest.screens.gameIntroScreens.IntroScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.BalloonGameIntroScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.BalloonGameScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.ShyAndSeekOutroScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.ShyAndSeekScreen;
import com.brendanmccluer.spikequest.screens.hubWorldScreens.FluttershyBackOfCottageScreen;
import com.brendanmccluer.spikequest.screens.hubWorldScreens.SugarCubeCornerScreen;

/**
 * I determine which screens to set
 * 
 * @author Brendan
 *
 */
public class SpikeQuestScreenManager {
	//Save File Keys
	public static boolean debugging = false;
	
	public static boolean isDebugging() {
		return debugging;
	}

	public static void setDebugging(boolean debugging) {
		SpikeQuestScreenManager.debugging = debugging;
	}

	/**
	 * I read the save file and determine which scene to set next. I also set
	 * both screens passed to null.
	 * ASSUMES SCREENS HAVE ALREADY CALLED DISCARD FUNCTION
	 * 
	 * @param aCallingScreen
	 * @param aGame
     * TODO Use Screen Stack
	 */
	public static void setNextScreen (AbstractSpikeQuestScreen aCallingScreen, SpikeQuestGame aGame) {
		//TODO DELETE THIS
		if (debugging) {
			//nothing
		}

		//MainMenuScreen is calling
		if (aCallingScreen instanceof MainMenuScreen) 
			handleMainMenuScreen((MainMenuScreen) aCallingScreen, aGame);
		
		//CliffBottom calling
		else if (aCallingScreen instanceof CliffBottomScreen)
			forwardScreen(aCallingScreen, new BalloonGameIntroScreen(aGame), aGame);
		
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
		
			if (SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_COMPLETE_KEY) && SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_SHY_AND_SEEK_COMPLETE_KEY))
				forwardScreen(aMainMenuScreen, new FluttershyBackOfCottageScreen(aGame, "normal", "left"), aGame);
			else if (SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_COMPLETE_KEY))
				setSugarCubeCornerScreen(aGame);
			else 
				//instructions screen
				forwardScreen(aMainMenuScreen, new BalloonGameIntroScreen(aGame), aGame);
		
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
	 */
	public static void setNextScreen (AbstractSpikeQuestScreen aCallingScreen, AbstractSpikeQuestScreen aScreenToNull, SpikeQuestGame aGame) {
		//null second screen
		aScreenToNull = null;
		setNextScreen(aCallingScreen, aGame);
	
	}
	
	/**
	 * I forward the screen passed and null the callingScreen. I DO NOT dispose the calling screen.
	 * 
	 * @param aCallingScreen
	 * @param aScreenToNull
	 * @param aGame
	 */
	public static void forwardScreen (AbstractSpikeQuestScreen aCallingScreen, AbstractSpikeQuestScreen aScreenToForward,  SpikeQuestGame aGame) {
	
		//TODO Handle dispose at this level and have screens not dispose themselves
		//aCallingScreen.dispose();
        aScreenToForward.initialize();
		aGame.setScreen(aScreenToForward);
	
	}
	
	
	/**
	 * I set the screen from the Balloon Game
	 * 
	 * @param aGame
	 */
	private static void setSugarCubeCornerScreen(SpikeQuestGame aGame) {
		SugarCubeCornerScreen aSugarCubeCornerScreen = null;
		
		if (SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.IS_BALLOON_GAME_NORMAL_KEY)) 
			aSugarCubeCornerScreen = new SugarCubeCornerScreen(aGame, "normal", "right");
		else 
			aSugarCubeCornerScreen = new SugarCubeCornerScreen(aGame, "intro", "right");

		forwardScreen(null, aSugarCubeCornerScreen, aGame);
	}
}
