package com.brendanmccluer.spikequest.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.buttons.ButtonObject;

/**
 * I render the MainMenu screen
 * @author Brendan
 *
 */
public class MainMenuScreen extends AbstractSpikeQuestScreen {
	private ButtonObject continueButton, newGameButton, creditsButton, gameSelectButton = null;
	
	//used for Screen Manager
	public boolean continueButtonPressed = false;
	public boolean newButtonPressed = false;

	public MainMenuScreen (SpikeQuestGame game) {
		super(game);
	}

    @Override
    public void initialize() {
        continueButton = new ButtonObject("Continue");
        newGameButton = new ButtonObject("New Game");
        creditsButton = new ButtonObject("Credits");
        gameSelectButton = new ButtonObject("Game Select");

        //set camera
        gameCamera = new SpikeQuestCamera(1700, 1554, 917);
        continueButton.setPosition(gameCamera.getCameraPositionX() + 400, gameCamera.getCameraPositionY());
        newGameButton.setPosition(gameCamera.getCameraPositionX() + 400, gameCamera.getCameraPositionY() - 100);
        gameSelectButton.setPosition(gameCamera.getCameraPositionX() + 400, gameCamera.getCameraPositionY() - 200);
        creditsButton.setPosition(gameCamera.getCameraPositionX() + 400, gameCamera.getCameraPositionY() - 300);

        //use asset manager to set resources
        game.assetManager.setAsset(SpikeQuestStaticFilePaths.MAIN_MENU_BACKDROP_PATH, "Texture");
    }
	
	public void render (float delta){
		refresh();
		gameCamera.attachToBatch(game.batch);
		
		
		if (game.assetManager.loadAssets() && continueButton.isLoaded() && newGameButton.isLoaded() && creditsButton.isLoaded() && gameSelectButton.isLoaded()) {
			
			if (!screenStart) {
				
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(SpikeQuestStaticFilePaths.MAIN_MENU_BACKDROP_PATH, "Texture");
				
				screenStart = true;
			}
        
			//Draw
			game.batch.begin();
			game.batch.draw(currentBackdropTexture, 0, 0);
			continueButton.draw(game.batch);
			newGameButton.draw(game.batch);
			gameSelectButton.draw(game.batch);
			creditsButton.draw(game.batch);
			
			//game.batch.draw(mainMenuImage, aSpikeQuestCamera.getMousePositionX(), aSpikeQuestCamera.getMousePositionY());
	        game.batch.end();
           
	        //Go to start screen
	        if (isButtonPressed(newGameButton)) {
	        	SpikeQuestSaveFile.deleteSaveFile(SpikeQuestStaticFilePaths.SAVE_FILE_NAME);
	        	SpikeQuestSaveFile.createNewSaveFile(SpikeQuestStaticFilePaths.SAVE_FILE_NAME);
	        	SpikeQuestSaveFile.setSaveFile(SpikeQuestStaticFilePaths.SAVE_FILE_NAME);
	        	
	        	newButtonPressed = true;
	        	setNextScreen();
	        	
			}
	        else if (isButtonPressed(continueButton)) {
	        	//debugging mode
	        	if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT))
	        		game.debugMode = true;
	        	SpikeQuestSaveFile.setSaveFile(SpikeQuestStaticFilePaths.SAVE_FILE_NAME);
	        	continueButtonPressed = true;
	        	setNextScreen();
	        	
	        }
			else if (isButtonPressed(gameSelectButton)) {
				//debugging mode
				if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT))
					game.debugMode = true;
				SpikeQuestSaveFile.setSaveFile(SpikeQuestStaticFilePaths.SAVE_FILE_NAME);
				dispose();
				game.screenStack.push(new GameSelectScreen(game));
				SpikeQuestScreenManager.popNextScreen(game);
				return;
			}
	        else if (isButtonPressed(creditsButton)) {
	        	//TODO; Add credits screen
	        }
		}
        
	}
	

	public void dispose () {
		game.assetManager.disposeAllAssets();
		continueButton.dispose();
		creditsButton.dispose();
		newGameButton.dispose();
		gameSelectButton.dispose();
		gameCamera.discard();
		
		continueButton = null;
		creditsButton = null;
		newGameButton = null;
		gameSelectButton = null;
		gameCamera = null;

		super.dispose();
	}
	
	private void setNextScreen() {
		dispose();
    	SpikeQuestScreenManager.popNextScreen(this, game);
	}
	
	
	
}
