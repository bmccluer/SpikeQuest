package com.brendanmccluer.spikequest.common.objects;

import java.util.ArrayList;



import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * I create score text, animate it, draw it, and then remove it
 * after a set time.
 * @author Brendan
 *
 */
public class ScoreControlObject {
	private static final float Y_INCREASE_AMOUNT = 5;
	private static final int DISPLAY_TIMER_MAX = 300;
	
	private ArrayList<scoreObject> scoreList = new ArrayList<scoreObject>();
	private ArrayList<scoreObject> destroyList = new ArrayList<scoreObject>();
	private BitmapFont bitMapFont = new BitmapFont();
	private boolean move = true;
	
	private class scoreObject { 
		int score;
		int mult;
		float xPos;
		float yPos;
		int displayTimer;
		public scoreObject (int score, int mult, float xPos, float yPos, int displayTimer) {
			this.score = score;
			this.mult = mult;
			this.xPos = xPos;
			this.yPos = yPos;
			this.displayTimer = displayTimer;
		}
	}
	
	public ScoreControlObject () {
		bitMapFont.scale(1);
	}
	
	public void disableMove() {
		move = false;
	}
	
	/**
	 * Create a new score
	 * @param score
	 * @param xPos
	 * @param yPos
	 */
	public void createNewScore (int score, float xPos, float yPos) {	
		scoreList.add(new scoreObject(score, 0, xPos, yPos, DISPLAY_TIMER_MAX));
	}
	
	/**
	 * Create a new score with multiplier
	 * @param score
	 * @param xPos
	 * @param yPos
	 */
	public void createNewScore (int score, float xPos, float yPos, int mult) {	
		scoreList.add(new scoreObject(score, mult, xPos, yPos, DISPLAY_TIMER_MAX));
	}
	
	public void draw (SpriteBatch batch) {
		for (scoreObject aScoreObject : scoreList) {	
			//check timer
			if (aScoreObject.displayTimer <= 0) {
				destroyList.add(aScoreObject);
			}
			else  {
				aScoreObject.displayTimer--;
				
				//set colors
				if (aScoreObject.score < 0) {
					bitMapFont.setColor(Color.RED);
				}
				else {
					bitMapFont.setColor(Color.WHITE);
				}
				bitMapFont.draw(batch, Integer.toString(aScoreObject.score), aScoreObject.xPos, aScoreObject.yPos);
				if (aScoreObject.mult != 0.0) {
					bitMapFont.setColor(Color.YELLOW);
					bitMapFont.draw(batch, " x " + Integer.toString(aScoreObject.mult),aScoreObject.xPos + bitMapFont.getBounds(Integer.toString(aScoreObject.score)).width, aScoreObject.yPos);
				}
				if (move)
					aScoreObject.yPos += Y_INCREASE_AMOUNT;	
			}
		}
		//remove elements that are no longer to be drawn 
		destroyElements();
	}

	private void destroyElements() {
		//remove score elements
		for (int i=0; i < destroyList.size(); i++) {
			scoreList.remove(destroyList.get(i));
		}
		destroyList.clear();
	}
	
	public void discard() {
		scoreList.clear();
		destroyList.clear();
		bitMapFont.dispose();
		bitMapFont = null;
	}
}


