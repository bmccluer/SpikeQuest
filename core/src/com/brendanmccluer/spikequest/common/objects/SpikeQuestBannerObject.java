package com.brendanmccluer.spikequest.common.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;

public class SpikeQuestBannerObject extends AbstractSpikeQuestObject {
	private static final String[] assets = { SpikeQuestStaticFilePaths.ORANGE_BANNER_PATH };
	private static final String[] types = { "Texture" };
	private static final int BANNER_PARAMETER = 50;
	
	private BitmapFont bitMapFont = new BitmapFont();
	private String bannerText = "";
	private Texture aBannerTexture = null;
	private float currentXPos = 0;
	private float currentYPos = 0;
	private float height = 0;
	private float width = 0;

	public SpikeQuestBannerObject(String aBannerText) {
		super(assets, types);

		bannerText = aBannerText;
		bitMapFont.setColor(Color.BLACK);

		setBannerParameters();
	}
	
	/**
	 * I create the texture region x, y, width, and height
	 * 
	 */
	private void setBannerParameters() {
		
		height = bitMapFont.getBounds(bannerText).height + BANNER_PARAMETER;
		width = bitMapFont.getBounds(bannerText).width + BANNER_PARAMETER;
		//currentXPos = bitMapFont.getRegion().getRegionX() + BANNER_PARAMETER;
		//currentYPos = bitMapFont.getRegion().getRegionY() + BANNER_PARAMETER;
		
	}

	@Override
	public boolean isLoaded() {
		boolean objectLoaded = super.isLoaded();

		if (objectLoaded)
			aBannerTexture = (Texture) getAsset(assets[0], types[0]);

		return objectLoaded;
	}

	public void draw(SpriteBatch batch) {

		if (isLoaded()) {
			
			//create the texture as "padding" around the text
			batch.draw(aBannerTexture, currentXPos, currentYPos, width, height);
			
			//draw the bitMapFont in middle of the banner
			bitMapFont.draw(batch, bannerText, currentXPos + BANNER_PARAMETER/2, currentYPos + bitMapFont.getBounds(bannerText).height + BANNER_PARAMETER/2);
		}

	}

	public String getBannerText() {
		return bannerText;
	}

	public void setBannerText(String bannerText) {
		this.bannerText = bannerText;
		setBannerParameters();
	}
	
	public void setBannerTextSize(float aRatio) {
		bitMapFont.scale(aRatio);
		
		setBannerParameters();
	}

	public float getCurrentXPos() {
		return currentXPos;
	}

	public void setCurrentXPos(float currentXPos) {
		this.currentXPos = currentXPos;
	}

	public float getCurrentYPos() {
		return currentYPos;
	}

	public void setCurrentYPos(float currentYPos) {
		this.currentYPos = currentYPos;
	}

	public float getHeight() {
		return height;
	}

	/*
	public void setCurrentHeight(float currentHeight) {
		this.currentHeight = currentHeight;
	}
	*/
	
	public float getWidth() {
		return width;
	}

	/*
	public void setCurrentWidth(float currentWidth) {
		this.currentWidth = currentWidth;
	}*/

}
