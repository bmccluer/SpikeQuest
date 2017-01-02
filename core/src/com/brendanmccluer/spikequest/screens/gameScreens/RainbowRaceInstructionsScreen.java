package com.brendanmccluer.spikequest.screens.gameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;

/**
 * Created by brend on 1/2/2017.
 */

public class RainbowRaceInstructionsScreen extends AbstractSpikeQuestScreen {
    private static final String BACKDROP_TEXTURE_PATH = "backdrop/rainbowRaceInstructions.png";

    public RainbowRaceInstructionsScreen(SpikeQuestGame game) {
        super(game);
    }

    @Override
    public void initialize() {
        gameCamera = new SpikeQuestCamera(1920, 1080, 5000); //screen properties
        //move the camera slightly to center the picture
        game.assetManager.setAsset(BACKDROP_TEXTURE_PATH, "Texture");
    }

    @Override
    public void render(float delta) {
        gameCamera.attachToBatch(game.batch);
        useLoadingScreen(delta);
        refresh();

        if (game.assetManager.loadAssets()) {

            if (!screenStart) {
                currentBackdropTexture = (Texture) game.assetManager.loadAsset(BACKDROP_TEXTURE_PATH, "Texture");
                screenStart = true;
            }

            game.batch.begin();
            game.batch.draw(currentBackdropTexture, 0, 0);
            game.batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
                dispose();

                //if Game has not been set as next,
                if (game.screenStack.isEmpty() || !(game.screenStack.peek() instanceof ShyAndSeekScreen))
                    game.screenStack.push(new RainbowRaceScreen(game));

                SpikeQuestScreenManager.popNextScreen(game);
                //SpikeQuestScreenManager.forwardScreen(this, new ShyAndSeekScreen(game), game);
            }

        }
    }

    @Override
    public void dispose() {
        game.assetManager.disposeAllAssets();

        gameCamera.discard();
        gameCamera = null;

        super.dispose();

    }
}
