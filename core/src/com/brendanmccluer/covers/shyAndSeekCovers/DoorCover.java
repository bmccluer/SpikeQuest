package com.brendanmccluer.covers.shyAndSeekCovers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.objects.AbstractCoverObject;
import com.brendanmccluer.spikequest.objects.AbstractPopUpObject;

public class DoorCover extends AbstractCoverObject {
	private static final float TOP_BOUNDRY_Y = 257.11145f;
	private static final float LOWER_BOUNDRY_Y = 225.16132f;
	
	public DoorCover() {
		super("backdrop/shyAndSeekCovers/door.png", 522.6906f , 220.16132f, 0, POPUP_SPEED, 0, POP_UP_SIZE_SML);
	}
	
	@Override
	public void setPopUpObject(AbstractPopUpObject aPopUpObject) {
		
		super.setPopUpObject(aPopUpObject);
		resetPopUp();
		
	}
	
	private void resetPopUp() {
	
		popUpObject.setCurrentPositionXY(xPos, LOWER_BOUNDRY_Y);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		
		if (isObjectPoppingUp()) {
			
			if (popUpObject.getCurrentPositionY() > TOP_BOUNDRY_Y) {
				popUpObject.setCurrentPositionY(TOP_BOUNDRY_Y);
				super.popUpDirection *= -1;
				super.stopPopUp();
			}
				
			//check bottom of object
			else if (popUpObject.getCurrentPositionY() < LOWER_BOUNDRY_Y) {
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
		return 100;
	}
}