package com.brendanmccluer.covers.shyAndSeekCovers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.objects.AbstractCoverObject;
import com.brendanmccluer.spikequest.objects.AbstractPopUpObject;
import com.brendanmccluer.spikequest.objects.animals.BatObject;

public class TreeTopRightCover extends AbstractCoverObject {
	private static final float TOP_BOUNDRY_Y = 600.62616f;
	private static final float LOWER_BOUNDRY_Y = 563.25226f;
	
	public TreeTopRightCover() {
		super("backdrop/shyAndSeekCovers/treeTopRight.png", 935.30054f , 535.77716f, 0, -POPUP_SPEED, ROTATE_FLIP_DEGREES, POP_UP_SIZE_LRG);
	}
	
	@Override
	public void setPopUpObject(AbstractPopUpObject aPopUpObject) {
		
		super.setPopUpObject(aPopUpObject);
		if (aPopUpObject instanceof BatObject)
			aPopUpObject.resize(0.75f);
		resetPopUp();
		
	}
	
	private void resetPopUp() {
	
		popUpObject.setCurrentPositionXY(953.7903f, TOP_BOUNDRY_Y);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		
		if (isObjectPoppingUp()) {
			
			if (popUpObject.getCurrentPositionY() + popUpObject.getCollisionRectangle().getHeight() < LOWER_BOUNDRY_Y) {
				popUpObject.setCurrentPositionY(LOWER_BOUNDRY_Y - popUpObject.getCollisionRectangle().getHeight());
				//popUpObject.setCurrentPositionY(popUpObject.getCurrentPositionY() + popUpObject.getCollisionRectangle().getHeight());
				super.popUpDirection *= -1;
				super.stopPopUp();
			}
				
			else if (popUpObject.getCurrentPositionY() > TOP_BOUNDRY_Y) {
				resetPopUp();
				super.popUpDirection *= -1;
				super.stopPopUp();
			}	
		}
		
		super.draw(batch);
	}

	@Override
	public boolean isPopUpHidden() { 
		return popUpObject.getCurrentPositionY() > TOP_BOUNDRY_Y;
	}
	
	@Override
	public int getPoints() {
		return 75;
	}
}
