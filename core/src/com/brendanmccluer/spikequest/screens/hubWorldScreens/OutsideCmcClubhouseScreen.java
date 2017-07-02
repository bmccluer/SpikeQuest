package com.brendanmccluer.spikequest.screens.hubWorldScreens;

import com.badlogic.gdx.graphics.Texture;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.dialog.SpikeQuestMultipleDialogController;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextObject;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.TankObject;
import com.brendanmccluer.spikequest.objects.ponies.AppleBloomObject;
import com.brendanmccluer.spikequest.objects.ponies.ScootalooObject;
import com.brendanmccluer.spikequest.objects.ponies.SweetieBelleObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestStandardScreen;

public class OutsideCmcClubhouseScreen extends AbstractSpikeQuestStandardScreen {
	private SweetieBelleObject sweetieBelle;
	private ScootalooObject scootaloo;
	private AppleBloomObject applebloom;
	private TankObject tank;
	private SpikeQuestMultipleDialogController dialogController;
	private Texture treeTexture;

	public OutsideCmcClubhouseScreen(SpikeQuestGame game, String aScreenType, String aSpikePosition) {
		super(game, 1280, 720, 1200, "backdrop/outsideCmcClubhouse.png", aScreenType, aSpikePosition);
		if((aScreenType == null || aScreenType.isEmpty()) && !SpikeQuestSaveFile.getBooleanValue(SpikeQuestSaveFile.CMC_TANK_INTRO_COMPLETE))
			screenType = "cmcTankIntro";
		treeTexture = new Texture("backdrop/outsideCmcClubhouseTree.png");
	}

	@Override
	public void initialize() {
		super.initialize();
		if("cmcTankIntro".equals(screenType)) {
			sweetieBelle = new SweetieBelleObject();
			scootaloo = new ScootalooObject();
			applebloom = new AppleBloomObject();
			tank = new TankObject();
			SpikeQuestTextObject[] textObjects = new SpikeQuestTextObject[] {new SpikeQuestTextObject(spikeObject, "Spike"), new SpikeQuestTextObject(sweetieBelle, "SweetieBelle"),
					new SpikeQuestTextObject(scootaloo, "Scootaloo"), new SpikeQuestTextObject(applebloom, "AppleBloom"), new SpikeQuestTextObject(tank, "Tank")};
			dialogController = new SpikeQuestMultipleDialogController("dialog/rainbowRaceStory/cmcTankUpgradeDialog.txt", textObjects);
			addToLoader(sweetieBelle, scootaloo, applebloom, dialogController, tank);
		}
	}

	@Override
	public void render(float delta) {
		if("cmcTankIntro".equals(screenType))
			renderTankIntro(delta);
		else
			renderNormal(delta);
	}

	private void renderTankIntro(float delta) {
		refresh();
		useLoadingScreen(delta);
		if (isLoaded()) {
			if (!screenStart) {
				startScreen(false);
				screenStart = true;
				scootaloo.spawn(gameCamera.getCameraPositionX() - 400, 0);
				sweetieBelle.spawn(gameCamera.getCameraPositionX() - 250, 0);
				applebloom.spawn(gameCamera.getCameraPositionX() - 100, 0);
				spikeObject.spawn(gameCamera.getCameraWidth() + 150, 0);
				tank.spawn(spikeObject.getCenterX() + 100, 100);
				tank.setSize(0.25f);
				tank.setWeight(0);
				tank.controlsDisabled = true;
			}
			if(dialogController.areTextBalloonsFinished()) {
				SpikeQuestSaveFile.setBooleanValue(SpikeQuestSaveFile.CMC_TANK_INTRO_COMPLETE, true);
				dispose();
				SpikeQuestScreenManager.forwardScreen(new OutsideCmcClubhouseScreen(game, "normal", "right"), game);
				return;
			}
			tank.hover(100, 150, 25 * delta);
			dialogController.updateTextAndObjects(delta);
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);
			game.batch.begin();
			drawBackdrop();
			//drawBitsAndGems();
			sweetieBelle.draw(game.batch);
			scootaloo.draw(game.batch);
			applebloom.draw(game.batch);
			tank.draw(game.batch);
			spikeObject.draw(game.batch);
			game.batch.draw(treeTexture,0,0);
			dialogController.drawText(game.batch);
			drawEffects(delta);
			game.batch.end();
		}
	}

	private void renderNormal(float delta) {
		refresh();
		useLoadingScreen(delta);
		if (isLoaded()) {
			if (!screenStart) {
				startScreen(true);
				screenStart = true;
			}
			//attach to batch after initialization
			gameCamera.attachToBatch(game.batch);

			game.batch.begin();
			drawBackdrop();
			spikeObject.draw(game.batch);
			game.batch.draw(treeTexture,0,0);
			drawBitsAndGems();
			game.batch.end();

			controlSpike();

			//determine end of screen
			/*if (("left").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(this, new SugarCubeCornerScreen(game, "normal", "right"), game);
				return;
			}*/
			//determine end of screen
			if (("right").equals(getEdgeTouched())) {
				dispose();
				SpikeQuestScreenManager.forwardScreen(new SweetAppleAcresPathScreen(game,"normal","left"),game);
				return;
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
