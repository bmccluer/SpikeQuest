package com.brendanmccluer.spikequest.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.common.objects.TimerObject;
import com.brendanmccluer.spikequest.interfaces.LoadableObject;

public class SpikeQuestMultipleDialogController implements LoadableObject {
	private final int MARGIN_Y_ABOVE_OBJECT = 25;
	private TimerObject timer = null;
	public boolean dialogEnabled = true;
	private SpikeQuestMultiTextBalloon textBalloon;
	private boolean waiting, started = false;

	public SpikeQuestMultipleDialogController(String fileName, SpikeQuestTextObject... textObjects) {
		textBalloon = new SpikeQuestMultiTextBalloon(fileName, textObjects);
	}

	/**
	 * I set if the dialog is controlled by a timer as opposed to the Space bar
	 * @deprecated set time in text file
	 */
	@Deprecated
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
		if(!started) {
			started = true;
			textBalloon.executeNext();
		}
		if (dialogEnabled && !waiting) {
			if ((timer == null && (textBalloon.noInput || Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isTouched()))
					|| (timer != null && timer.isTimerFinished())) {

				if (timer != null)
					timer.startTimer(0, 3);
				textBalloon.executeNext();
			}
		}
		waiting = false;
		for (SpikeQuestTextObject textObject : textBalloon.textObjects) {
			textObject.object.update(delta);
			if (textBalloon.waitForAnimation && textObject.object.isAnimating()) {
				waiting = true;
			}
			else if (textBalloon.timer != null && !textBalloon.timer.isTimerFinished()) {
				waiting = true;
			}
			else {
				if (!textBalloon.isDialogEmpty() && !areTextBalloonsFinished() && textBalloon.getCurrentObject() == textObject) {
					//set text balloon over current object
					textBalloon.setCurrentXPos(textObject.object.getCenterX());
					textBalloon.setCurrentYPos(textObject.object.getCenterY() +
							textObject.object.getCollisionRectangle().getHeight() + MARGIN_Y_ABOVE_OBJECT);

					//play talk animation if a current animation is NOT set
					if (!textObject.object.isAnimationSet())
						textObject.object.talk();
				}
				//play stand animation if not animating or a current animation is set
				if (!textObject.object.isAnimationSet())
					textObject.object.standStill();
			}
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
		if (!textBalloon.isDialogEmpty() && !waiting && dialogEnabled && !textBalloon.isAtEndOfDialog()) {
            textBalloon.drawDialog(batch);
		}
      /*  for (SpikeQuestTextObject textObject : textBalloon.textObjects) {
            textObject.object.draw(batch);
        }*/
	}

	public void dispose() {
		textBalloon.dispose();
		if (timer != null)
			timer.dispose();
	}
}
