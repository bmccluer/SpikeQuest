package com.brendanmccluer.spikequest.common.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestSpriteObject;

public class ScoreBoardObject extends AbstractSpikeQuestSpriteObject {
	private static final String[] filePaths = {"object/scoreBoard/ScoreZeroLives.atlas", "object/scoreBoard/ScoreOneLives.atlas", "object/scoreBoard/ScoreTwoLives.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas"}; 
	private static final int SCORE_OFFSET_X = 25;
	private static final int SCORE_OFFSET_Y = 15;
	private static final int GEMS_OFFSET_X = 60;
	private static final int GEMS_OFFSET_Y = 45;
	private static final float STARTING_SIZE = 0.5f;

	private int lives = 2;
	private int score = 0;
	private int gems = 0;
	private float countDownX = 0;
	private float countDownY = 0;
	private int countDownSecs = 0;
	private BitmapFont scoreFont = null;
	private BitmapFont countDownFont = null;
	private BitmapFont gemsFont = new BitmapFont();
	private TimerObject timerObject = null;
	private boolean usingTimer = false;
	
	
	public ScoreBoardObject() {
		super(filePaths, fileTypes);
		scoreFont = new BitmapFont();
		scoreFont.scale(1.5f);
		gemsFont.scale(1.5f);
	}
	
	/**
	 * Timer scoreboard does not show score and no lives are shown
	 * @param timerScoreBoard
	 */
	public ScoreBoardObject (boolean timerScoreBoard) {
		super(filePaths, fileTypes);
		
		if (timerScoreBoard) {
			timerObject = new TimerObject();
			timerObject.getFont().scale(1f);
			usingTimer = true;
			lives = 0;
			
			scoreFont = new BitmapFont();
			scoreFont.scale(1f);
			gemsFont.scale(1f);
		}
		
		else {
			scoreFont = new BitmapFont();
			scoreFont.scale(1.5f);
			gemsFont.scale(1.5f);
		}
		
	}
	


	public void spawn(float xPos, float yPos) {
		
		super.spawn(xPos, yPos, (TextureAtlas) getAsset(filePaths[2], "TextureAtlas"), "", 1);
		setSize(STARTING_SIZE);
		setCurrentTextureAtlas(lives);
	}
	
	/**
	 * I display the amount of seconds passed 
	 * in the screen position passed (only if timer used in scoreboard)
	 * CountDown xPos and yPos will be in center of text
	 * @param xPos
	 * @param yPos
	 */
	public void setFinalCountdown(float xPos, float yPos, int seconds) {
		countDownSecs = seconds;
		countDownX = xPos;
		countDownY = yPos;
		countDownFont = new BitmapFont();
		countDownFont.setColor(Color.YELLOW);
		countDownFont.scale(5);
	}

	public void draw (SpriteBatch batch) {
		
		super.draw(batch);
		
		if (usingTimer) {
			scoreFont.draw(batch, Integer.toString(score), currentSprite.getSpriteBoundingRectangle().getX() + 
					SCORE_OFFSET_X, currentPositionY + currentSprite.getSpriteBoundingRectangle().getHeight() - SCORE_OFFSET_Y);
			
			//countDown xPos and yPos will be in center of text
			if (countDownSecs > 0 && timerObject.getMinutes() == 0 && countDownSecs >= timerObject.getSeconds())
				countDownFont.draw(batch, Integer.toString(timerObject.getSeconds()), countDownX - countDownFont.getBounds(Integer.toString(countDownSecs)).width/2, 
						countDownY + countDownFont.getBounds(Integer.toString(timerObject.getSeconds())).height/2);
				
				timerObject.drawTimer(batch, currentSprite.getSpriteBoundingRectangle().getX() + 
					 currentSprite.getSpriteBoundingRectangle().getWidth() - (SCORE_OFFSET_X * 4), currentPositionY + currentSprite.getSpriteBoundingRectangle().getHeight() - SCORE_OFFSET_Y);
			
		}
		else {
			scoreFont.draw(batch, Integer.toString(score), currentSprite.getSpriteBoundingRectangle().getX() + 
					SCORE_OFFSET_X, currentPositionY + currentSprite.getSpriteBoundingRectangle().getHeight() - SCORE_OFFSET_Y);
		}
		
		gemsFont.draw(batch, Integer.toString(gems), currentSprite.getSpriteBoundingRectangle().getX() + 
				GEMS_OFFSET_X, currentPositionY + GEMS_OFFSET_Y);
		
	}
	
	private void setCurrentTextureAtlas(int numberOfLives) {
		
		try {
			currentSprite.setCurrentTextureAtlas((TextureAtlas) getAsset(filePaths[numberOfLives], "TextureAtlas"), 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getLives() {
		return lives;
	}

	public void subtractLife() {
		
		if (lives > 0) {
			lives--;
			setCurrentTextureAtlas(lives);
		}
		
		
	}

	public int getScore() {
		return score;
	}

	public void addScore(int score) {
		this.score += score;
	}
	
	/**
	 * return minutes (must call useTimer() first)
	 * 
	 * @return
	 */
	public int getMinutes () {
		return timerObject.getMinutes();
	}
	
	/**
	 * return seconds (must call useTimer() first)
	 * @return
	 */
	public int getSeconds () {
		return timerObject.getSeconds();
	}
	
	public void startTimer (int minutes, int seconds) {
		timerObject.startTimer(minutes, seconds);
	}
	
	public void stopTimer () {
		timerObject.stopTimer();
	}
	
	public boolean isTimerFinished() {
		return timerObject.isTimerFinished();
	}

	public int getGems() {
		return gems;
	}

	/**
	 * Can be negative or positive (total does not go below zero)
	 * @param gems
	 */
	public void addGems(int gems) {
		this.gems += gems;
		
		if (this.gems < 0)
			this.gems = 0;
	}
	
	@Override
	public void dispose() {
		
		if (usingTimer)
			timerObject.dispose();
	
		scoreFont.dispose();
		gemsFont.dispose();
		if (countDownFont != null) {
			countDownFont.dispose();
			countDownFont = null;
		}
		timerObject = null;
		scoreFont = null;
		gemsFont = null;
	}



	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);
	}
	
	
	
	
	
}