package com.brendanmccluer.covers.shyAndSeekCovers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.objects.AbstractCoverObject;
import com.brendanmccluer.spikequest.objects.AbstractPopUpObject;

public class CaveLeftCover extends AbstractCoverObject {
	private static final float RIGHT_BOUNDRY_X = 430.32257f;
	private static final float LEFT_BOUNDRY_X = 420.37976f;
	
	public CaveLeftCover() {
		super("backdrop/shyAndSeekCovers/caveLeft.png", 413.37976f , 96.99414f, POPUP_SPEED, 0, ROTATE_RIGHT_DEGREES, POP_UP_SIZE_SML);
	}
	
	@Override
	public void setPopUpObject(AbstractPopUpObject aPopUpObject) {
		
		super.setPopUpObject(aPopUpObject);
		resetPopUp();
		
	}
	
	private void resetPopUp() {
		
		popUpObject.setCurrentPositionXY(LEFT_BOUNDRY_X, yPos);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		
		if (isObjectPoppingUp()) {
			
			if (popUpObject.getCurrentPositionX() > RIGHT_BOUNDRY_X) {
				popUpObject.setCurrentPositionX(RIGHT_BOUNDRY_X);
				super.popUpDirection *= -1;
				super.stopPopUp();
			}
				
			else if (popUpObject.getCurrentPositionX() < LEFT_BOUNDRY_X) {
				resetPopUp();
				super.popUpDirection *= -1;
				super.stopPopUp();
			}	
		}
		
		super.draw(batch);
	}
	
	@Override
	public boolean isPopUpHidden() {
		return  (popUpObject.getCurrentPositionX() < LEFT_BOUNDRY_X || popUpObject.getCurrentPositionX() + popUpObject.getCollisionRectangle().getWidth() <= RIGHT_BOUNDRY_X); 
	}
	
	@Override
	public int getPoints() {
		return 250;
	}
}
