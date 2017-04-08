package com.brendanmccluer.spikequest.sounds;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

/**
 * I play a sound effect using the Sound class.
 * 
 * @author Brendan
 *
 */
public class SpikeQuestSoundEffect implements Disposable {
	private Sound sound = null;
	private int soundLength = 0;
	private int soundIndex = 0;
	private long soundId = 0;
	private boolean soundPlaying = false;
	
	public SpikeQuestSoundEffect (Sound aSoundEffect, int aSoundLength) {
		sound = aSoundEffect;
		soundLength = aSoundLength;
	}
	
	/**
	 * Make sure to set sound effect before playing
	 */
	public SpikeQuestSoundEffect () {}
	
	public void setSoundEffect(Sound aSoundEffect, int aSoundLength) { 
		sound = aSoundEffect;
		soundLength = aSoundLength;
	}
	
	
	/**
	 * I play the sound. Will not overlap the sound if already
	 * playing unless canOverlap is true
	 * 
	 * @param canOverlap
	 */
	public void playSound (boolean canOverlap) {
		//System.out.println(soundIndex);
		//System.out.println(soundPlaying);
		if (canOverlap || !soundPlaying) {
			soundId = sound.play();
			soundIndex = 0;
			soundPlaying = true;
		}
		else {
			soundIndex++;
			if (soundIndex > soundLength)
				soundPlaying = false;
		}
	}

	public void playSound (boolean canOverlap, float pitch, float volume) {
		playSound(canOverlap);
		setPitch(pitch);
		setVolume(volume);
	}
	
	/**
	 * This method only works if playSound continues to be called with canOverlap set to "false"
	 * 
	 * @return
	 */
	public boolean isPlaying () {
		return soundPlaying;
	}
	
	/**
	 * set pitch (only between 0.5 and 2.0)
	 * 
	 * @param pitch
	 */
	public void setPitch(float pitch) {
		sound.setPitch(soundId, pitch);
	}
	
	/**
	 * set volume in range 0 to 1
	 * 
	 * @param aVolume
	 */
	public void setVolume (float aVolume) {
		sound.setVolume(soundId, aVolume);
	}
	
	public void pauseSound () {
		sound.pause();
	}
	
	public void stopSound () {
		sound.stop();
	}
	
	public void dispose() {
		if (sound != null) {
			sound.dispose();
			sound = null;
		}
	}
	
	
	
	 
}
