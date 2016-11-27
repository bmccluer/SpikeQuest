package com.brendanmccluer.spikequest.objects.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by brend on 11/26/2016.
 *
 */

public class SpikeQuestButtonCreator{

    public static Button createButton(TextureAtlas buttonAtlas, Stage stage) {
        Skin skin;
        Button.ButtonStyle style;
        Button button = null;

        if (stage != null) {
            Gdx.input.setInputProcessor(stage);
            skin = new Skin();
            skin.addRegions(buttonAtlas);
            style = new Button.ButtonStyle();
            style.up = skin.getDrawable("up-button");
            style.down = skin.getDrawable("down-button");
            style.checked = skin.getDrawable("checked-button");
            button = new Button(style);
            stage.addActor(button);
        }

       return button;
    }
}
