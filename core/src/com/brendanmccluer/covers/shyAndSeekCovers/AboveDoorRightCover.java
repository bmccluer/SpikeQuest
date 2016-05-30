package com.brendanmccluer.covers.shyAndSeekCovers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.objects.AbstractCoverObject;
import com.brendanmccluer.spikequest.objects.AbstractPopUpObject;

public class AboveDoorRightCover extends AbstractCoverObject {
	private static final float TOP_BOUNDRY_Y = 365.96924f;
	private static final float LOWER_BOUNDRY_Y = 350.34314f;
	
	public AboveDoorRightCover() {
		super("backdrop/shyAndSeekCovers/aboveDoorRight.png", 548.3871f, 333.0645f, POPUP_SPEED, POPUP_SPEED, ROTATE_RIGHT_DEGREES + 45, POP_UP_SIZE_SML);
	}

	@Override
	public void setPopUpObject(AbstractPopUpObject aPopUpObject) {
		
		super.setPopUpObject(aPopUpObject);
		resetPopUp();
		
	}
	
	private void resetPopUp() {
		popUpObject.setCurrentPositionXY(xPos + texture.getWidth()/2, LOWER_BOUNDRY_Y);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		float leftCorner = 0;
		
		if (isObjectPoppingUp()) {
			leftCorner = popUpObject.getCurrentPositionY() + distanceToCornerY;
											
			 if (leftCorner > TOP_BOUNDRY_Y) {
				popUpObject.setCurrentPositionY(TOP_BOUNDRY_Y - (leftCorner - popUpObject.getCurrentPositionY()));
				super.popUpDirection *= -1;
				super.stopPopUp();
			}
				
			//check bottom of object
			if (leftCorner < LOWER_BOUNDRY_Y) {
				resetPopUp();
				super.popUpDirection *= -1;
				super.stopPopUp();
			}	
		}
		
		super.draw(batch);
	}

	@Override
	public boolean isPopUpHidden() {
		return popUpObject.getCurrentPositionY() < LOWER_BOUNDRY_Y || popUpObject.getCurrentPositionY() + popUpObject.getCollisionRectangle().getHeight() <= TOP_BOUNDRY_Y; 
	}

	@Override
	public int getPoints() {
		return 150;
	}
}
