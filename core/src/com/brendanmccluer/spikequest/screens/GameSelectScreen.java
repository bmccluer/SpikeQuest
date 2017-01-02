package com.brendanmccluer.spikequest.screens;


import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.interfaces.ButtonObjectAction;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.buttons.ImageButtonObject;
import com.brendanmccluer.spikequest.screens.gameScreens.BalloonGameIntroScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.RainbowRaceInstructionsScreen;
import com.brendanmccluer.spikequest.screens.gameScreens.ShyAndSeekInstructionScreen;

import java.util.ArrayList;

/**
 * I render the GameSelect screen
 * @author Brendan
 *
 * TODO add logic to show only games that are unlocked
 */
public class GameSelectScreen extends AbstractSpikeQuestScreen {
	private ImageButtonObject  backButton;
    private ArrayList<ImageButtonObject> imageButtonList;
    private float rowHeight, columnWidth = 0;
    private static final int TABLE_PADDING = 100;

	public GameSelectScreen(SpikeQuestGame game) {
		super(game);
    }

    @Override
    public void initialize() {
        int rows = 5;
        int columns = 5;
        imageButtonList = new ArrayList<ImageButtonObject>();
        gameCamera = new SpikeQuestCamera(1961, 1121, 1000);
        currentBackdropTexture = new Texture("backdrop/gameSelectionBackdrop.png");

        addImageButton("buttons/gameSelectButtons/Pinkie'sBalloonGameButton.png", new BalloonGameIntroScreen(game));
        addImageButton("buttons/gameSelectButtons/ShyAndSeekButton.png", new ShyAndSeekInstructionScreen(game));
        addImageButton("buttons/gameSelectButtons/RainbowRaceButton.png", new RainbowRaceInstructionsScreen(game));
        setImageButtonPositions(rows, columns);

        backButton = new ImageButtonObject(new Texture("buttons/goBackButton.png"), this);
        backButton.setButtonAction(new ButtonObjectAction() {
            @Override
            public void handle(AbstractSpikeQuestScreen screen) {
                screen.dispose();
                game.screenStack.push(new MainMenuScreen(game));
                SpikeQuestScreenManager.popNextScreen(game);
            }
        });
        backButton.setPosition(gameCamera.getCameraPositionX() - gameCamera.getCameraWidth()/2 + 50, 50);
	}

    private void setImageButtonPositions(int rows, int columns) {
        rowHeight = currentBackdropTexture.getHeight()/rows;
        columnWidth = currentBackdropTexture.getWidth()/columns + TABLE_PADDING;
        int listPosition = 0;

        for(int i=rows-2; i > 0; i--)
            for(int j=0; j < columns; j++) {
                ImageButtonObject imageButton = imageButtonList.get(listPosition);
                imageButton.setPosition(columnWidth * j, rowHeight * i);
                listPosition++;
                if (listPosition >= imageButtonList.size())
                    return;
            }
    }

    private void addImageButton(String filePath, AbstractSpikeQuestScreen newScreen) {
        ImageButtonObject anImageButton = new ImageButtonObject(new Texture(filePath), this);
        anImageButton.setButtonAction(new ButtonObjectAction() {
            @Override
            public void handle(AbstractSpikeQuestScreen screen) {
                game.screenStack.push(new GameSelectScreen(game));
                game.screenStack.push(newScreen);
                SpikeQuestScreenManager.popNextScreen(game);
            }
        });
        imageButtonList.add(anImageButton);
    }

    public void render (float delta){
		refresh();
		gameCamera.attachToBatch(game.batch);

        for(ImageButtonObject imageButtonObject : imageButtonList)
            imageButtonObject.update(delta, gameCamera);

        backButton.update(delta, gameCamera);

        game.batch.begin();
        drawBackdrop();
        for(ImageButtonObject imageButtonObject : imageButtonList)
            imageButtonObject.draw(game.batch);
        backButton.draw(game.batch);
        game.batch.end();
	}

	public void dispose () {
		gameCamera.discard();
		gameCamera = null;
		super.dispose();
	}

}
