package com.brendanmccluer.spikequest.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.reflect.Method;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;


/**
 * Created by brend on 11/20/2016.
 */
public interface ButtonObjectInterface {
	boolean checkMouseOver(float xMousePos, float yMousePos);

	void update(float delta, SpikeQuestCamera gameCamera);

	void draw(SpriteBatch aSpriteBatch);

	void setPosition(float xPos, float yPos);

	boolean isLoaded();

	void setButtonAction(ButtonObjectAction action);

	void discard();
}
