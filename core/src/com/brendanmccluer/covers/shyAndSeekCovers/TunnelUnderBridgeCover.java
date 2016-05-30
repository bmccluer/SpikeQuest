package com.brendanmccluer.covers.shyAndSeekCovers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.objects.AbstractCoverObject;
import com.brendanmccluer.spikequest.objects.AbstractPopUpObject;

public class TunnelUnderBridgeCover extends AbstractCoverObject {
	private static final float RIGHT_BOUNDRY_X = 563.48975f;
	private static final float LEFT_BOUNDRY_X = 535.6188f;
	
	public TunnelUnderBridgeCover() {
		super("backdrop/shyAndSeekCovers/tunnelUnderBridge.png", 454.17886f , 30.022034f, POPUP_SPEED, 0, ROTATE_RIGHT_DEGREES, POP_UP_SIZE_SML);
	}
	
	@Override
	public void setPopUpObject(AbstractPopUpObject aPopUpObject) {
		
		super.setPopUpObject(aPopUpObject);
		resetPopUp();
		
	}
	
	private void resetPopUp() {
		
		popUpObject.setCurrentPositionXY(LEFT_BOUNDRY_X, yPos + 15);
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
		return popUpObject.getCurrentPositionX() < LEFT_BOUNDRY_X || popUpObject.getCurrentPositionX() + popUpObject.getCollisionRectangle().getWidth() <= RIGHT_BOUNDRY_X; 
	}
	
	@Override
	public int getPoints() {
		return 250;
	}
}
