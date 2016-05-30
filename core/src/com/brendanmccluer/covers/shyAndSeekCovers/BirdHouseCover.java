package com.brendanmccluer.covers.shyAndSeekCovers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.objects.AbstractCoverObject;
import com.brendanmccluer.spikequest.objects.AbstractPopUpObject;
import com.brendanmccluer.spikequest.objects.animals.BatObject;

public class BirdHouseCover extends AbstractCoverObject {
	private static final float RIGHT_BOUNDRY_X = 750.5498f;
	private static final float LEFT_BOUNDRY_X = 695.12463f;
	
	
	public BirdHouseCover() {
		super("backdrop/shyAndSeekCovers/birdHouse.png", 668.1818f, 248.6437f, POPUP_SPEED, 0, ROTATE_RIGHT_DEGREES, POP_UP_SIZE_SML);
	}
	
	@Override
	public void setPopUpObject(AbstractPopUpObject aPopUpObject) {
		
		super.setPopUpObject(aPopUpObject);
		resetPopUp();
		
	}
	
	private void resetPopUp() {
		if (popUpObject instanceof BatObject)
			popUpObject.setCurrentPositionXY(LEFT_BOUNDRY_X, yPos + texture.getHeight()/4);
		else 
			popUpObject.setCurrentPositionXY(LEFT_BOUNDRY_X, yPos + texture.getHeight()/3);
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
		return popUpObject.getCurrentPositionX() + popUpObject.getCollisionRectangle().getWidth() <= RIGHT_BOUNDRY_X; 
	}
	
	@Override
	public int getPoints() {
		return 200;
	}
}
