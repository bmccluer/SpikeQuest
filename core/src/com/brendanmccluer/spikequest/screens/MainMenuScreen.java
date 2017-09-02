package com.brendanmccluer.spikequest.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;

import java.awt.Checkbox;

import javafx.scene.control.ChoiceBox;
import sun.security.provider.ConfigFile;

/**
 * I render the MainMenu screen
 * @author Brendan
 *
 */
public class MainMenuScreen extends AbstractSpikeQuestScreen {
	private Stage stage;
	private Skin skinMainMenu, skinUi;
	private Image imgBackground, imgScroll;
	private Button btnContinue, btnNewGame, btnGameSelect, btnOptions;
	private Window winOptions, winNewGame;
	private TextButton btnWinOptPlayScreen;
	private CheckBox chkDebug;
	private ChoiceBox chcBxScreen;

	//debug only
	private final float DEBUG_REBUILD_INTERVAL = 5.0f;
	private float debugRebuildStage;

	public MainMenuScreen(SpikeQuestGame game) {
		super(game);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void show() {
		gameCamera = new SpikeQuestCamera(1700, 1554, 917);
		game.assetManager.setAsset(SpikeQuestStaticFilePaths.MAIN_MENU_BACKDROP_PATH, "Texture");
		stage = new Stage(new StretchViewport(gameCamera.getCameraWidth(), gameCamera.getCameraHeight()));
		Gdx.input.setInputProcessor(stage);
		rebuildStage();
	}

	private void rebuildStage () {
		skinMainMenu = new Skin(Gdx.files.internal("mainMenu/MainMenuUI.json"), new TextureAtlas("mainMenu/MainMenuUI.atlas"));
		skinUi = new Skin(Gdx.files.internal("defaultUi/UiSkin.json"), new TextureAtlas("defaultUi/Uiskin.atlas"));
		Table layerBackground = buildBackgroundLayer();
		Table layerScrollImage = buildScrollImageLayer();
		Table layerControls = buildControlsLayer();
		Table layerOptionsWindow = buildOptionsWindowLayer();
		Table layerNewGameWindow = buildNewGameWindowLayer();
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(gameCamera.getCameraWidth(), gameCamera.getCameraHeight());
		stack.add(layerBackground);
		stack.add(layerScrollImage);
		stack.add(layerControls);
		stage.addActor(layerOptionsWindow);
	}
	
	public void render (float delta){
		refresh();
		gameCamera.attachToBatch(game.batch);
		//rebuild stage so changes can be seen immediately
		/*if(SpikeQuestGame.debugMode) {
			debugRebuildStage -= delta;
			if(debugRebuildStage <= 0) {
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}*/

		stage.act(delta);
		stage.draw();
		stage.setDebugAll(SpikeQuestGame.debugMode);
	}

	private Table buildBackgroundLayer() {
		Table layer = new Table();
		imgBackground = new Image(skinMainMenu, "Background");
		layer.add(imgBackground);
		return layer;
	}

	private Table buildScrollImageLayer() {
		Table layer = new Table();
		imgScroll = new Image(skinMainMenu, "Scroll");
		//"addActor" means it won't effect how the actor is drawn
		//use "add" to confine to table
		layer.addActor(imgScroll);
		imgScroll.setScale(0.9f);
		imgScroll.setPosition(stage.getWidth() - imgScroll.getWidth() + 120,
				stage.getHeight() - imgScroll.getHeight() + 85);
		return layer;
	}

	private Table buildControlsLayer() {
        Table layer = new Table();
		btnContinue = new Button(skinMainMenu, "continue");
		btnNewGame = new Button(skinMainMenu, "newGame");
		btnGameSelect = new Button(skinMainMenu, "gameSelect");
		btnOptions = new Button(skinMainMenu, "options");
		layer.addActor(btnContinue);
		layer.addActor(btnNewGame);
		layer.addActor(btnGameSelect);
		layer.addActor(btnOptions);
		btnContinue.setPosition(stage.getWidth() - 525, stage.getHeight() - 400);
		btnNewGame.setPosition(stage.getWidth() - 562, stage.getHeight() - 515);
		btnGameSelect.setPosition(stage.getWidth() - 590, stage.getHeight() - 630 );
		btnOptions.setPosition(stage.getWidth() - 640, stage.getHeight() - 745);
		btnContinue.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//SpikeQuestSaveFile.addContinueScreens(game);
				SpikeQuestScreenManager.popNextScreen(MainMenuScreen.this, game);
			}
		});
		btnGameSelect.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SpikeQuestScreenManager.forwardScreen(new GameSelectScreen(game), game);
			}
		});
        btnOptions.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                winOptions.setVisible(true);
				btnOptions.setDisabled(true);
                btnContinue.setDisabled(true);
                btnGameSelect.setDisabled(true);
                btnNewGame.setDisabled(true);
            }
        });


		return layer;
	}

	private Table buildOptionsWindowLayer() {
		winOptions = new Window("Options", skinUi);
        winOptions.add(buildOptWinDebugSettings()).row();
        winOptions.setColor(1, 1, 1, 0.8f);
        winOptions.setVisible(false);
        if(SpikeQuestGame.debugMode)
            winOptions.debug();
        winOptions.pack();
        winOptions.setCenterPosition(gameCamera.getCameraPositionX(), gameCamera.getCameraPositionY());
        return winOptions;
	}

	private Table buildOptWinDebugSettings() {
		Table table = new Table();
		table.pad(10, 10, 10, 10);
		table.add(new Label("Debug", skinUi, "default-font", Color.RED)).colspan(3);
		table.row();
		table.columnDefaults(0).padRight(10);
		table.columnDefaults(1).padRight(10);
		CheckBox chkboxDebugMode = new CheckBox("", skinUi);
        chkboxDebugMode.setChecked(SpikeQuestGame.debugMode);
        chkboxDebugMode.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpikeQuestGame.debugMode = !SpikeQuestGame.debugMode;
            }
        });
		table.add(new Label("Debug Mode", skinUi));
		table.add(chkboxDebugMode);
		table.row().padTop(25);
		table.add(new Label("Enter Screen", skinUi));
		String debugScreen = SpikeQuestSaveFile.getStringValue(SpikeQuestSaveFile.DEBUG_SCREEN);
		TextField textField = new TextField(debugScreen, skinUi);
		textField.setWidth(50);
		table.row().padLeft(50);
        table.add(textField);
		TextButton screenButton = new TextButton("GO", skinUi);
		TextButton closeButton = new TextButton("Close", skinUi);

        screenButton.setWidth(25);
        table.add(screenButton);
        table.row();
		table.add(closeButton).padTop(50);
        //Label errorLabel = new Label("", skinUi);
		screenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    AbstractSpikeQuestScreen screen = getScreen(textField.getText());
                    game.screenStack.add(screen);
                    SpikeQuestSaveFile.setStringValue(SpikeQuestSaveFile.DEBUG_SCREEN, textField.getText());
                    stage.clear();
                    SpikeQuestScreenManager.forwardScreen(screen, game);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				rebuildStage();
			}
		});
        return table;
	}

    private AbstractSpikeQuestScreen getScreen(String screenName) throws Exception {
        Class<?> screen = Class.forName(screenName);
        return (AbstractSpikeQuestScreen) screen.getConstructor(SpikeQuestGame.class).newInstance(new Object[] {game});
    }

    private Table buildNewGameWindowLayer() {
		Table layer = new Table();
		return layer;
	}

	public void dispose () {
		game.assetManager.disposeAllAssets();
		stage.dispose();
		skinMainMenu.dispose();
		super.dispose();
	}
}
