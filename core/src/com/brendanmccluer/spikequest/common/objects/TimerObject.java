package com.brendanmccluer.spikequest.common.objects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TimerObject  {
	private BitmapFont font = null;
	private int minutes = 0;
	private int seconds = 0;
	private TimerEventListener listener = new TimerEventListener();
	private Timer timer = new Timer(1000, listener);

	public TimerObject() {}

	public TimerObject(int minutes, int seconds) {
		this.seconds = seconds;
		this.minutes = minutes;
	}
	/**
	 * I start the timer
	 */
	public void startTimer (int minutes, int seconds) {
		this.minutes = minutes;
		this.seconds = seconds;

		timer.start();
	}

	public void startTimer() {
		if(seconds > 0 || minutes > 0)
			timer.start();
	}
	
	public void stopTimer () {
		timer.stop();
		//timer.purge()?
	}
	
	public void resetTimer () {
		minutes = 0;
		seconds = 0;
	}
	
	public boolean isTimerFinished () {
		return minutes == 0 && seconds == 0;
	}
	
	public void drawTimer (SpriteBatch batch, float xPos, float yPos) {
		
		if (font == null)
			font = new BitmapFont();
		
		if (seconds < 10)
			font.draw(batch, minutes + " : 0" + seconds, xPos, yPos);
		else
			font.draw(batch, minutes + " : " + seconds, xPos, yPos);
		
	}
	
	public BitmapFont getFont () {
		if (font == null)
			font = new BitmapFont();
		
		return font;
	}
	
	public int getMinutes() {
		return minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void dispose () {
		if (font != null)
			font.dispose();
		font = null;
		listener = null;
	}
	
	/**
	 * I listen for the timer and set the minutes and seconds
	 */
	private class TimerEventListener implements ActionListener {
		

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//start seconds back at 59
			if (seconds == 0) {
				
				if (minutes != 0) {
					seconds = 59;
					minutes--;
				}
					
			}
			
			else
				seconds--;
			
		}
	};
	
	
	
}
