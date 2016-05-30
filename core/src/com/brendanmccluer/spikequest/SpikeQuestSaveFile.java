package com.brendanmccluer.spikequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * I access save file information stored on GDX preferences
 * see https://github.com/libgdx/libgdx/wiki/Preferences
 * 
 * @author Brendan
 *
 */
public final class SpikeQuestSaveFile {
	public static final String INTEGER_BITS_KEY_NAME = "Bits";
	public static final String INTEGER_GEMS_KEY_NAME = "Gems";
	public final static String IS_BALLOON_GAME_NORMAL_KEY = "isBalloonGameNormal";
	public final static String IS_BALLOON_GAME_COMPLETE_KEY = "isBalloonGameComplete";
	public final static String IS_SHY_AND_SEEK_COMPLETE_KEY = "isShyAndSeekComplete";
	private static Preferences gdxPreferenceFile = null;
	
	/**
	 * I set the save file to be read
	 * @param name
	 */
	public static void setSaveFile (String name) {
		gdxPreferenceFile = Gdx.app.getPreferences(name);
		gdxPreferenceFile.flush();
		
	}
	
	/**
	 * I create a new save file and set it as the Save file
	 * @param name
	 */
	public static void createNewSaveFile (String name) {
		gdxPreferenceFile = Gdx.app.getPreferences(name);
		
		gdxPreferenceFile.putInteger(INTEGER_BITS_KEY_NAME, 0);
		gdxPreferenceFile.putInteger(INTEGER_GEMS_KEY_NAME, 0);
	}
	
	/**
	 * I delete the save file passed. Make sure it is not the Set save file!
	 * @param name
	 */
	public static void deleteSaveFile (String name) {
		Gdx.app.getPreferences(name).clear();
	}
	
	/**
	 * I get the number of bits stored
	 * @return
	 */
	public static int getBits () {
		gdxPreferenceFile.flush();
		return gdxPreferenceFile.getInteger(INTEGER_BITS_KEY_NAME);
	}
	
	/**
	 * I add to the total bits
	 * @param bits
	 */
	public static void addBits (int bits) {
		gdxPreferenceFile.putInteger(INTEGER_BITS_KEY_NAME, bits + getBits());
		gdxPreferenceFile.flush();

	}
	
	/**
	 * I get the number of gems stored
	 * @return
	 */
	public static int getGems() {
		gdxPreferenceFile.flush();
		return gdxPreferenceFile.getInteger(INTEGER_GEMS_KEY_NAME);
	}
	
	/**
	 * I add to the number of gems stored
	 * @param gems
	 */
	public static void addGems (int gems) {
		gdxPreferenceFile.putInteger(INTEGER_GEMS_KEY_NAME, gems + getGems());
		gdxPreferenceFile.flush();
	}
	
	

	/**
	 * I return a boolean value from the save file. Use the list of static fields
	 * in this class.
	 * 
	 * @param gameName
	 * @return
	 */
	public static boolean getBooleanValue(String booleanName) {
		gdxPreferenceFile.flush();
		return gdxPreferenceFile.getBoolean(booleanName);
		
	}
	
	public static void setBooleanValue(String booleanName, boolean aBoolean) {
		
		gdxPreferenceFile.putBoolean(booleanName, aBoolean);
		gdxPreferenceFile.flush();
	}

}
