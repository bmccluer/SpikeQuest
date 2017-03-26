package com.brendanmccluer.spikequest.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.common.objects.TimerObject;

public class SpikeQuestMultipleDialogController {
	private final int MARGIN_Y_ABOVE_OBJECT = 25;
	private TimerObject timer = null;
	public boolean dialogEnabled = true;
	private SpikeQuestMultiTextBalloon textBalloon;


	/**
	 * I control text balloons passed
	 * @param textObjects
     */
	public SpikeQuestMultipleDialogController(String fileName, SpikeQuestTextObject... textObjects) {
		textBalloon = new SpikeQuestMultiTextBalloon(fileName, textObjects);
	}

	/**
	 * I set if the dialog is controlled by a timer as opposed to the Space bar
	 */
	public void useTimer() {
		timer = new TimerObject();
		timer.startTimer(0, 3);
	}

	/**
	 * I return if the text balloons are loaded and ready to be drawn.
	 * 
	 * @return
	 */
	public boolean isLoaded() {
        return textBalloon.isLoaded();
	}

	public void updateTextAndObjects(float delta) {
        // listen for space bar or timer
		if ((timer == null && (Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isTouched()))
				|| (timer != null && timer.isTimerFinished())) {

			if (timer != null)
				timer.startTimer(0, 3);
			
			textBalloon.setNextDialog();
		}
		for (SpikeQuestTextObject textObject : textBalloon.textObjects) {
            if (textBalloon.getCurrentObject() == textObject) {
				//set text balloon over current object
				textBalloon.setCurrentXPos(textObject.object.getCenterX());
				textBalloon.setCurrentYPos(textObject.object.getCenterY() +
						textObject.object.getCollisionRectangle().getHeight() + MARGIN_Y_ABOVE_OBJECT);

				//play talk animation if one is not defined
				if (textBalloon.methodName == null)
					textObject.object.talk();
			}
			//TODO use standard update method instead of stand still
			else
				textObject.object.standStill();
		}

	}

    public boolean areTextBalloonsFinished() {
		return textBalloon.isAtEndOfDialog();
	}

	/**
	 * I draw the text balloons, and set animations for SpikeQuestSpriteObjects
	 * and listen for space input only when dialogEnabled is true.
	 * 
	 * @param batch
	 */
	public void drawText(SpriteBatch batch) {
		if (dialogEnabled && !textBalloon.isAtEndOfDialog()) {
            textBalloon.drawDialog(batch);
		}
      /*  for (SpikeQuestTextObject textObject : textBalloon.textObjects) {
            textObject.object.draw(batch);
        }*/
	}

	public void discard() {
		textBalloon.discard();
		if (timer != null)
			timer.dispose();
	}
}
