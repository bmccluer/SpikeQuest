package com.brendanmccluer.covers.shyAndSeekCovers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.objects.AbstractCoverObject;
import com.brendanmccluer.spikequest.objects.AbstractPopUpObject;

public class TopLeftWindowCover extends AbstractCoverObject {
	private static final float RIGHT_BOUNDRY_X = 300.73752f;
	private static final float LEFT_BOUNDRY_X = 277.89587f;
	
	public TopLeftWindowCover() {
		super("backdrop/shyAndSeekCovers/topLeftWindow.png", 270.19794f , 410.3006f, -POPUP_SPEED, 0, ROTATE_LEFT_DEGREES, POP_UP_SIZE_SML);
	}
	
	@Override
	public void setPopUpObject(AbstractPopUpObject aPopUpObject) {
		
		super.setPopUpObject(aPopUpObject);
		resetPopUp();
		
	}
	
	private void resetPopUp() {
		
		popUpObject.setCurrentPositionXY(RIGHT_BOUNDRY_X - popUpObject.getCollisionRectangle().getWidth(), yPos + 15);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		
		if (isObjectPoppingUp()) {
			
			if (popUpObject.getCurrentPositionX() + popUpObject.getCollisionRectangle().getWidth() < LEFT_BOUNDRY_X) {
				popUpObject.setCurrentPositionX(LEFT_BOUNDRY_X - popUpObject.getCollisionRectangle().getWidth());
				super.popUpDirection *= -1;
				super.stopPopUp();
			}
				
			else if (popUpObject.getCurrentPositionX() + popUpObject.getCollisionRectangle().getWidth() > RIGHT_BOUNDRY_X) {
				resetPopUp();
				super.popUpDirection *= -1;
				super.stopPopUp();
			}	
		}
		
		super.draw(batch);
	}
	
	@Override
	public boolean isPopUpHidden() {
		return popUpObject.getCurrentPositionX() + popUpObject.getCollisionRectangle().getWidth() > RIGHT_BOUNDRY_X || popUpObject.getCurrentPositionX() >= LEFT_BOUNDRY_X; 
	}
	
	@Override
	public int getPoints() {
		return 250;
	}
}
