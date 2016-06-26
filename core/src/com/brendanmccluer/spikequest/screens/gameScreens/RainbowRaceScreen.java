package com.brendanmccluer.spikequest.screens.gameScreens;

import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.RainbowDashObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;

/**
 * Created by brend on 6/25/2016.
 */
public class RainbowRaceScreen extends AbstractSpikeQuestScreen {
    private static final String BACKGROUND_FILE_PATH = ""; //TODO Set background;
    private TankObject aTankObject = new TankObject();
    private RainbowDashObject aRainbowDashObject = new RainbowDashObject();

    public RainbowRaceScreen(SpikeQuestGame game) {
        super(game);
        gameCamera = new SpikeQuestCamera(1050, 1055, 594);
    }


    @Override
    public void render(float delta) {

        if (game.assetManager.loadAssets() && aTankObject.isLoaded() && aRainbowDashObject.isLoaded()) {
            refresh();
            gameCamera.attachToBatch(game.batch);
            if (!screenStart) {
                aTankObject.spawn(gameCamera.getWorldWidth()/2, gameCamera.getWorldHeight()/2);
                screenStart = true;
            }
            aTankObject.update(delta);
            game.batch.begin();
            aTankObject.draw(game.batch);
            game.batch.end();
        }


    }
}
