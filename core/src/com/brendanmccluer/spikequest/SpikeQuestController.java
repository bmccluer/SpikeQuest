package com.brendanmccluer.spikequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.objects.SpikeObject;

/**
 * a list of control listeners to be used statically
 * @author Brendan
 *
 */
public class SpikeQuestController {
	public boolean isJumping = false;
	public boolean canBreatheFire = true;
	public boolean canJump = true;
	
	public void controlListener(SpikeObject aSpikeObject, SpikeQuestCamera aSpikeQuestCamera, int aTranslationSpeed) {
		
		//check jump
		if (canJump && isJumping) {
			isJumping = aSpikeObject.jump();
		}
		
		//Move spike if key press
		if (aSpikeObject.getCenterX() <= aSpikeQuestCamera.getWorldWidth() && (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isTouched())) {

				aSpikeObject.moveRight(aTranslationSpeed);
				
				if (aSpikeObject.getCurrentPositionX() >= (aSpikeQuestCamera.getCameraPositionX())) 
					aSpikeQuestCamera.translateCamera(aTranslationSpeed, 0);
		}
		else if (aSpikeObject.getCenterX() >= 0 && Gdx.input.isKeyPressed(Keys.LEFT)) {
			
			aSpikeObject.moveLeft(aTranslationSpeed);
			
			if (aSpikeObject.getCurrentPositionX() <= (aSpikeQuestCamera.getCameraPositionX()))
			aSpikeQuestCamera.translateCamera(-aTranslationSpeed, 0);
		}
		else if (canBreatheFire && !isJumping && Gdx.input.isKeyPressed(Keys.DOWN)) {
			aSpikeObject.breatheFire();
					
		}
		else {
			
			aSpikeObject.standStill();
		}
		
		//check jump press
		if (!isJumping && Gdx.input.isKeyPressed(Keys.UP)) {
			isJumping = true;
		}
	}
	

}
