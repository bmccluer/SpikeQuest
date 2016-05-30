package com.brendanmccluer.spikequest;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class SpikeQuestStaticMethods {
	
	/**
	 * I check if the bitmap font overlaps the xPos and yPos	
	 * @param aBitmapFont
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public static boolean isOverlapping (BitmapFont aBitmapFont, float xPos, float yPos) {
			
		return isOverlapping (xPos, yPos, aBitmapFont.getRegion().getRegionX(), aBitmapFont.getRegion().getRegionY(), 
					aBitmapFont.getRegion().getRegionWidth(), aBitmapFont.getRegion().getRegionHeight());
	}
		
	
	
	/**
	 * I check if the xPos and yPos are inside the four range positions
	 * @param xPos
	 * @param yPos
	 * @param minimumRangeXPos
	 * @param minimumRangeYPos
	 * @param rangeLength
	 * @param rangeHeight
	 * @return
	 */
	private static boolean isOverlapping(float xPos, float yPos, float minimumRangeXPos, float minimumRangeYPos,
				float rangeLength, float rangeHeight) {
				
		float rangeLeft = minimumRangeXPos;
		float rangeRight = minimumRangeXPos + rangeLength;
		float rangeBottom = minimumRangeYPos;
		float rangeTop = minimumRangeYPos + rangeHeight;
		
		return xPos >= rangeLeft && xPos <= rangeRight && yPos >= rangeBottom && yPos <= rangeTop;
		
	}

}
