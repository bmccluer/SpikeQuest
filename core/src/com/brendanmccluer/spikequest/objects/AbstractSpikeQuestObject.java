package com.brendanmccluer.spikequest.objects;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.SpikeQuestAssetManager;

/**
 * All objects using game extend this class.
 * Uses AssetManager to load all assets needed for
 * the object.
 * @author Brendan
 *
 */
public abstract class AbstractSpikeQuestObject {
	private SpikeQuestAssetManager objectManager = null;
	protected boolean objectLoaded = false;
	
	/**
	 * set object to have multiple files
	 * @param assets
	 * @param types
	 */
	protected AbstractSpikeQuestObject (String[] assets, String[] types) {
		objectManager = new SpikeQuestAssetManager();
		
		for (int i = 0; i < assets.length; i++) {
			
			objectManager.setAsset(assets[i], types[i]);
		}
	}
	
	/**
	 * set object to have one asset file
	 * @param asset
	 * @param type
	 */
	protected AbstractSpikeQuestObject (String asset, String type) {
		objectManager = new SpikeQuestAssetManager();
		
		objectManager.setAsset(asset, type);
		
	}
	
	
	/**
	 * I update the manager and return if all assets are 
	 * loaded
	 * 
	 * @return
	 */
	public boolean isLoaded () {
		if (!objectLoaded && objectManager.loadAssets()) {
			objectLoaded = true;
		}
		
		return objectLoaded;
	}
	
	/** I return an asset loaded for the object.
	 *  If object is not loaded I return null
	 * @param filePath
	 * @param assetType
	 * @return
	 */
	protected Object getAsset(String filePath, String assetType) {
		if (objectLoaded) {
			return objectManager.loadAsset(filePath, assetType);
		}
			return null;
	}
	
	/**
	 * set more assets to the object manager
	 * @param filePath
	 * @param assetType
	 */
	protected void setAsset(String filePath, String assetType) {
		objectManager.setAsset(filePath, assetType);
	}
	
	public void discard () {
		objectManager.disposeAssetsAndManager();
	}
	
	
	/**
	 * I draw a Texture for the object
	 * @param batch
	 * @param xPosition
	 * @param yPosition
	 */
	protected void drawTexture (Texture texture, SpriteBatch batch, float xPosition, float yPosition) {
		if (objectLoaded) {
			batch.draw(texture, xPosition, yPosition);
		}
	}
	
	/**I draw a sprite for the object
	 * 
	 * @param sprite
	 * @param batch
	 * @param xPosition
	 * @param yPosition
	 */
	protected void drawSprite (SpikeQuestSprite sprite, SpriteBatch batch) {
		if (objectLoaded) {
			try {
				sprite.drawSprite(batch);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}
