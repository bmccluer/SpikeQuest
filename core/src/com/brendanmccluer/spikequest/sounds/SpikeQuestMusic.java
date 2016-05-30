package com.brendanmccluer.spikequest.sounds;

import com.badlogic.gdx.audio.Music;

public class SpikeQuestMusic {
	private Music music = null;
	
	public SpikeQuestMusic (Music aMusic) {
		music = aMusic;
	}
	
	/**
	 * I play the sound. Set looping with boolean value
	 * 
	 * @param canRepeat
	 */
	public void playMusic (boolean canLoop) {
		music.setLooping(canLoop);
		music.play();
	}
	
	public void setVolume (float aVolume) {
		music.setVolume(aVolume);
	}
	
	public void pauseMusic () {
		music.pause();
	}
	
	public void stopMusic () {
		music.stop();
	}

	public void discard() {
		music.dispose();
		music = null;
	}
	
}
