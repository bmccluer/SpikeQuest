package com.brendanmccluer.spikequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.brendanmccluer.spikequest.dialog.SpikeQuestTextBalloon;
import com.brendanmccluer.spikequest.common.objects.TimerObject;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;

public class SpikeQuestDialogController implements Disposable {
	private static final int DEFAULT_OFFSET_Y = 300;
	
	private SpikeQuestTextBalloon firstTextBalloon = null;
	private SpikeQuestTextBalloon secondTextBalloon = null;
	private TimerObject timer = null;
	public String firstTextBalloonName = null;
	public String secondTextBalloonName = null;
	public int firstTextIndex = 0;
	public int secondTextIndex = 0;
	public boolean drawFirstTextFlag = false;
	public boolean drawSecondTextFlag = false;
	private int firstEndingIndex = 0;
	private int secondEndingIndex = 0;
	public boolean dialogEnabled = true;
	private boolean textBalloonsLoaded = false;
	private boolean atEndOfDialog = false;
	

	/**
	 * I control two text balloons passed. Notice that you must call this
	 * controller to set properties of the text balloons after passing.
	 * 
	 * @param firstTextBalloon
	 * @param firstTextBalloonName
	 * @param firstEndingIndex
	 * @param secondTextBalloon
	 * @param secondTextBalloonName
	 * @param secondEndingIndex
	 */
	public SpikeQuestDialogController(AbstractSpikeQuestSpriteObject anObjectForFirstText, SpikeQuestTextBalloon firstTextBalloon, String firstTextBalloonName,
			int firstEndingIndex, AbstractSpikeQuestSpriteObject anObjectForSecondText, SpikeQuestTextBalloon secondTextBalloon, String secondTextBalloonName,
			int secondEndingIndex) {

		this.firstTextBalloon = firstTextBalloon;
		this.secondTextBalloon = secondTextBalloon;
		this.firstTextBalloonName = firstTextBalloonName;
		this.secondTextBalloonName = secondTextBalloonName;
		this.firstEndingIndex = firstEndingIndex;
		this.secondEndingIndex = secondEndingIndex;
		
		firstTextBalloon.loadSounds(anObjectForFirstText, firstTextBalloonName, firstEndingIndex);
		secondTextBalloon.loadSounds(anObjectForSecondText, secondTextBalloonName, secondEndingIndex);
		
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
	public boolean areTextBalloonsLoaded() {

		if (!textBalloonsLoaded && firstTextBalloon.isLoaded() && secondTextBalloon.isLoaded()) {

			firstTextBalloon.loadDialog(firstTextBalloonName + firstTextIndex);
			firstTextBalloon.setNextDialog();
			secondTextBalloon.loadDialog(secondTextBalloonName + secondTextIndex);
			secondTextBalloon.setNextDialog();
			
			textBalloonsLoaded = true; //keep dialog from being reset by using this boolean
			
		}

		return textBalloonsLoaded;
	}
	
	/**
	 * I reset the dialog
	 */
	public void reset() {
		firstTextIndex = 0;
		secondTextIndex = 0;
		textBalloonsLoaded = false;
		atEndOfDialog = false;
	}
	

	/**
	 * I set flags for two dialog balloons (drawFirstText and drawSecondText)
	 * when the space bar is pressed and load the next dialog if it exists for
	 * both text balloons
	 * 
	 * @param firstTextBalloon
	 * @param firstTextBalloonName
	 * @param secondTextBalloon
	 * @param secondTextBalloonName
	 */
	private void setDrawingFlags() {

		if (firstTextIndex == 0) {
			drawFirstTextFlag = true;
		}

		// listen for space bar or timer
		if ((timer == null && (Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isTouched()))
				|| (timer != null && timer.isTimerFinished())) {

			if (timer != null)
				timer.startTimer(0, 3);
			
			// if first textBalloon is done go to second talk balloon
			if (drawFirstTextFlag && !firstTextBalloon.setNextDialog()) {

				if (secondTextIndex <= secondEndingIndex) {
					firstTextIndex++;
					firstTextBalloon.loadDialog(firstTextBalloonName + firstTextIndex);
					firstTextBalloon.setNextDialog();
				}

				drawFirstTextFlag = false;

				// drawSecondText if it's not at the end
				if (secondTextIndex <= secondEndingIndex) {
					drawSecondTextFlag = true;
				}
				// are both first and second finished?
				else 
					atEndOfDialog = firstTextBalloon.isAtEndOfDialog() && secondTextBalloon.isAtEndOfDialog();
			}

			else if (drawSecondTextFlag && !secondTextBalloon.setNextDialog()) {

				if (secondTextIndex <= secondEndingIndex) {
					secondTextIndex++;
					secondTextBalloon.loadDialog(secondTextBalloonName + secondTextIndex);
					secondTextBalloon.setNextDialog();
				}

				drawSecondTextFlag = false;

				// drawFirstText if it's not at the end
				if (firstTextIndex <= firstEndingIndex) {
					drawFirstTextFlag = true;
				}
				// are both first and second finished?
				else 
					atEndOfDialog = firstTextBalloon.isAtEndOfDialog() && secondTextBalloon.isAtEndOfDialog();

			}

/*			//both dialogs finished
			else if (atEndOfDialog()) { 
				drawSecondTextFlag = false; 
				drawFirstTextFlag = false;
			}*/
			 
		}

	}
	
	public boolean areTextBalloonsFinished() {
		return atEndOfDialog;
	}

	/**
	 * I draw the text balloons, and set animations for SpikeQuestSpriteObjects
	 * and listen for space input only when dialogEnabled is true.
	 * 
	 * @param batch
	 */
	public void drawTheDialogAndAnimateObjects(SpriteBatch batch, AbstractSpikeQuestSpriteObject anObjectForFirstText,
			AbstractSpikeQuestSpriteObject anObjectForSecondText) {

		if (dialogEnabled && areTextBalloonsLoaded()) {

			if (drawFirstTextFlag) {
				firstTextBalloon.drawDialog(batch, anObjectForFirstText);
				anObjectForFirstText.talk();
			} else
				anObjectForFirstText.standStill();

			if (drawSecondTextFlag) {
				secondTextBalloon.drawDialog(batch, anObjectForSecondText);
				anObjectForSecondText.talk();
			} else
				anObjectForSecondText.standStill();

			// set flags for next iteration
			setDrawingFlags();
			
			return;
		}
	}

	/**
	 * I set the default locations over the objects passed.
	 * 
	 * @param anObjectForFirstText
	 * @param anObjectForSecondText
	 */
	public void setTextBalloonDefaultPositionsOverObjects (AbstractSpikeQuestSpriteObject anObjectForFirstText,
			AbstractSpikeQuestSpriteObject anObjectForSecondText) {
		
		if (areTextBalloonsLoaded()) {
			
			setFirstTextBalloonPositionX(anObjectForFirstText.getCenterX());
			setFirstTextBalloonPositionY(anObjectForFirstText.getCurrentPositionY() + DEFAULT_OFFSET_Y);
			
			setSecondTextBalloonPositionX( anObjectForSecondText.getCenterX());
			setSecondTextBalloonPositionY(anObjectForSecondText.getCurrentPositionY() + DEFAULT_OFFSET_Y);

			
		}
		
		
	}
	
	

	/**
	 * I set the positions of the first balloon
	 * 
	 * @param xPos
	 */
	public void setFirstTextBalloonPositionX(float xPos) {
		
		if (areTextBalloonsLoaded())
			firstTextBalloon.setCurrentXPos(xPos);

	}

	/**
	 * I set the positions of the first balloon
	 * 
	 * @param xPos
	 * @param yPos
	 */
	public void setFirstTextBalloonPositionY(float yPos) {
		
		if (areTextBalloonsLoaded())
			firstTextBalloon.setCurrentYPos(yPos);

	}
	
	
	/**
	 * I set the positions of the second balloon
	 * 
	 * @param xPos
	 */
	public void setSecondTextBalloonPositionX(float xPos) {
		
		if (areTextBalloonsLoaded())
			secondTextBalloon.setCurrentXPos(xPos);

	}

	/**
	 * I set the positions of the second balloon
	 * 
	 * @param xPos
	 * @param yPos
	 */
	public void setSecondTextBalloonPositionY(float yPos) {
		
		if (areTextBalloonsLoaded())
			secondTextBalloon.setCurrentYPos(yPos);

	}
	
	public float getFirstTextBalloonPositionX() {
		
		if (areTextBalloonsLoaded())
			return firstTextBalloon.getCurrentXPos();
		
		return 0;
	}
	
	public float getFirstTextBalloonPositionY() {
		
		if (areTextBalloonsLoaded())
			return firstTextBalloon.getCurrentYPos();
		
		return 0;
	}
	
	public float getSecondTextBalloonPositionX() {
		
		if (areTextBalloonsLoaded())
			return secondTextBalloon.getCurrentXPos();
		
		return 0;
	}
	
	public float getSecondTextBalloonPositionY() {
		
		if (areTextBalloonsLoaded())
			return secondTextBalloon.getCurrentYPos();
		
		return 0;
	}
	
	/**
	 * I clear the text balloons from memory
	 */
	public void dispose() {
		firstTextBalloon.dispose();
		secondTextBalloon.dispose();
		
		firstTextBalloon = null;
		secondTextBalloon = null;
		if (timer != null)
			timer.dispose();
		
	}
}
