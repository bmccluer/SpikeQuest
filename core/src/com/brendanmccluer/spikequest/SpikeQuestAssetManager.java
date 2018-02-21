package com.brendanmccluer.spikequest;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * I handle all loading and deleting of assets 
 * to prevent memory leaks
 * @author Brendan
 *
 */
public class SpikeQuestAssetManager {
	public AssetManager manager = null;
	public SpikeQuestAssetManager () {
		manager = new AssetManager();
	}
	
	/**
	 * I set an Asset in the load queue depending on the type of asset
	 * being loaded.
	 * Return false if fail
	 * 
	 * @param filepath
	 * @param assetType
	 */
	public boolean setAsset(String filepath, String assetType) {
		
		if (filepath == null || assetType == null) {
			//Cannot load null types
			return false;
		}
		
		if (assetType.equalsIgnoreCase("Texture")) {
			manager.load(filepath, Texture.class);
		}
		else if (assetType.equalsIgnoreCase("BitmapFont")) {
			manager.load(filepath, BitmapFont.class);
		}
		else if (assetType.equalsIgnoreCase("Sound")) {
			manager.load(filepath, Sound.class);
		}
		else if (assetType.equalsIgnoreCase("Music")) {
			manager.load(filepath, Music.class);
		}
		else if (assetType.equalsIgnoreCase("TextureAtlas")) {
			manager.load(filepath, TextureAtlas.class);
		}
		else {
			//Loader type not recognized
			return false;
		}
		return true;
	}

	/**
	 * I return the asset requested.
	 * Return null if not loaded or does not exist
	 * 
	 * @param filepath
	 * @param assetType
	 * @return
	 */
	public Object loadAsset(String filepath, String assetType) {
		
		//Cannot continue if string are null or asset is not loaded
		if (filepath == null || assetType == null || !(manager.isLoaded(filepath))) {
			return null;
		}

		if (assetType.equalsIgnoreCase("Texture")) {
			return manager.get(filepath, Texture.class);
		}
		else if (assetType.equalsIgnoreCase("BitmapFont")) {
			return manager.get(filepath, BitmapFont.class);
		}
		else if (assetType.equalsIgnoreCase("Sound")) {
			return manager.get(filepath, Sound.class);
		}
		else if (assetType.equalsIgnoreCase("Music")) {
			return manager.get(filepath, Music.class);
		}
		else if (assetType.equalsIgnoreCase("TextureAtlas")) {
			return manager.get(filepath, TextureAtlas.class);
		}
		else {
			//Loader type not recognized
			return null;
		}
	}
	
	/**
	 * I load the assets and return true if done loading
	 * @return
	 */
	public boolean loadAssets () {
		return manager.update();
	}
	
	/**
	 * I dispose a single asset
	 * @param filepath
	 */
	public void disposeAsset (String filepath) {
		manager.unload(filepath);
	}
	
	/**
	 * I dispose all assets
	 */
	public void disposeAllAssets () {
		manager.clear();
	}
	
	/**
	 * I dispose all assets and the manager itself
	 * DO NOT USE THIS ASSET MANAGER AGAIN IF CALLED
	 */
	public void disposeAssetsAndManager () {
		manager.dispose();
	}
}
