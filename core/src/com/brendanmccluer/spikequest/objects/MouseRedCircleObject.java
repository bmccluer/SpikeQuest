package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class MouseRedCircleObject extends AbstractSpikeQuestSpriteObject {
	private static final String[] assetPaths = {"mouse/RedCircle.atlas"};
	private static final String[] assetTypes = {"TextureAtlas"};
	
	/**
	 * used for drawing a "radar" red circle
	 * 
	 */
	public MouseRedCircleObject() {
		super(assetPaths, assetTypes);
	}
	
	@Override
	public boolean isLoaded() {
		boolean isLoaded = super.isLoaded();
		
		if (isLoaded && currentSprite == null) {
			spawn(0, 0, (TextureAtlas) getAsset(assetPaths[0], assetTypes[0]), "StandStill", 3);
		}
		
		return isLoaded;
		
	}
	
	/**
	 * I adjust the object so it draws at the tip of the mouse coordinates
	 * 
	 * @param batch
	 * @param xPos
	 * @param yPos
	 */
	public void drawOnMouse(SpriteBatch batch, float xPos, float yPos) {
		
		currentPositionX = xPos - currentSprite.getSpriteBoundingRectangle().getWidth()/2;
		currentPositionY = yPos - currentSprite.getSpriteBoundingRectangle().getHeight()/2;
		
		currentSprite.setPosition(currentPositionX, currentPositionY);
		
		//animate the object
		standStill();
		
		super.draw(batch);
	}
	
	@Override
	/**
	 * NOT IMPLEMENTED
	 */
	public void resetSize() {}
}
