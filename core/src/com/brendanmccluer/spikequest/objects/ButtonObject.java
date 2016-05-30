package com.brendanmccluer.spikequest.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ButtonObject extends AbstractSpikeQuestObject {
	private final static String BUTTON_PATH = "buttons/mainMenuButton.png";
	private Texture buttonTexture = null;
	private BitmapFont buttonText = new BitmapFont();
	private Sprite buttonSprite = new Sprite();
	//private Rectangle buttonRectangle = new Rectangle ();
	private String buttonTextString = "";
	
	public ButtonObject(String aText) {
		super(BUTTON_PATH, "Texture");
			
		buttonTextString = aText;
		buttonText.scale(2);
		buttonText.setColor(Color.PURPLE);
		
	}
	
	/**
	 * I set the text on the button
	 * @param aText
	 */
	public void setText (String aText) {
		
		if (aText == null)
			buttonTextString = "";
		else
			buttonTextString = aText;
	}
	
	/**
	 * I return if the mouse position is over the button and
	 * also change the color of the text
	 * 
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public boolean checkMouseOver (float xMousePos, float yMousePos) {
		
	
		if (buttonSprite.getBoundingRectangle().contains(xMousePos, yMousePos)) {
			buttonText.setColor(Color.WHITE);
			return true;
		}

		buttonText.setColor(Color.PURPLE);
		return false;
	}
	
	/**
	 * I draw the button (if it is loaded)
	 * @param aSpriteBatch
	 * @param xPos
	 * @param yPos
	 */
	public void draw(SpriteBatch aSpriteBatch) {
		
		if (objectLoaded) {
			
			buttonSprite.draw(aSpriteBatch);
			//aSpriteBatch.draw(buttonSprite.getTexture(), buttonSprite.getX(), buttonSprite.getY());
			buttonText.draw(aSpriteBatch, buttonTextString, buttonSprite.getX()+15, buttonSprite.getY()+55);
			
		}
		
	}
	
	public void setPosition (float xPos, float yPos) {
		buttonSprite.setX(xPos);
		buttonSprite.setY(yPos);
	}
	
	@Override
	public boolean isLoaded () {
		
		boolean isLoaded = super.isLoaded();
		
		//if object is loaded prepare the button
		if (buttonTexture == null && isLoaded) {
			
			buttonTexture = (Texture) getAsset(BUTTON_PATH, "Texture");
			buttonSprite.setTexture(buttonTexture);
			buttonSprite.setRegion(buttonTexture);
			buttonSprite.setBounds(buttonSprite.getX(), buttonSprite.getY(), buttonTexture.getWidth(), buttonTexture.getHeight());
		}
		
		return isLoaded;
	}
	
	@Override
	public void discard () {
		buttonText = null;
		
		super.discard();
	}
}
