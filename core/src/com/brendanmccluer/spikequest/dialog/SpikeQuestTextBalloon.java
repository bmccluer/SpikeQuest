package com.brendanmccluer.spikequest.dialog;

import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;
import com.brendanmccluer.spikequest.objects.StandardObject;

public class SpikeQuestTextBalloon extends AbstractSpikeQuestObject {
	private final static int TEXT_BALLOON_LENGTH = 350;
	private final static int TEXT_BALLOON_CHANGE_TIMER = 12;
	private final static int TEXT_BALLOON_X_OFFSET = 8;
	private final static int TEXT_BALLOON_Y_OFFSET = 160;

	private int TextBalloonWrapWidth = 0;

	//private SpikeQuestSoundEffect[] spikeQuestSoundEffects = null;
	//private String[] spikeQuestSoundEffectNames = null;
	private BitmapFont message = null;
	private Texture messageBox = null;
	private String dialog = ""; // entire dialog read from file
	private FileHandle textFile = null;
	private InputStream fileReader = null;
	private char nextChar = 0;
	private String drawMessage = ""; // portion of dialog that is drawn
	private int dialogIndex = 0;
	private int dialogTimer = 0; // used to keep dialog from being called
									// multiple times at once
	private boolean textBoxLoaded = false;
	private float currentXPos = 0;
	private float currentYPos = 0;
	private String soundName = null;
	private float soundVolume = 0;

	//1/22/2017 added to object instead of using Dialog Controller
	protected String titleName = "";
	protected int titleIndex = 0;
	protected boolean draw = true;
	protected StandardObject object;
	
	/**
	 * Create new text balloon with no sound effects
	 */
	public SpikeQuestTextBalloon(String textFilePath) {
		super(SpikeQuestStaticFilePaths.TEXT_BALLOON_PATH, "Texture");
		textFile = Gdx.files.internal(textFilePath);
	}

	public SpikeQuestTextBalloon(String textFilePath, String titleName, StandardObject object) {
		super(SpikeQuestStaticFilePaths.TEXT_BALLOON_PATH, "Texture");
		textFile = Gdx.files.internal(textFilePath);
		this.titleName = titleName;
        this.object = object;
	}

	@Override
	public boolean isLoaded() {
		if (!textBoxLoaded && super.isLoaded()) {

			messageBox = (Texture) getAsset(SpikeQuestStaticFilePaths.TEXT_BALLOON_PATH, "Texture");
			TextBalloonWrapWidth = messageBox.getWidth() - 10;

			message = new BitmapFont();
			message.setColor(Color.BLACK);
			
			textBoxLoaded = true;
		}
		return textBoxLoaded;
	}

	/**
	 * I load sound effects for the name provided. If the dialog uses sound, call this before loading the text.
	 * 
	 * @param anObject
     * @deprecated
	 */
	public void loadSounds(AbstractSpikeQuestObject anObject, String aTitleName, int anEndingIndex) {

		if (anObject instanceof StandardObject) {
			
			for (int anIndex = 0; anIndex < anEndingIndex; anIndex++) {
				
				//load any sound names
				loadDialog(aTitleName + anIndex);
				
				if (soundName != null)
					((StandardObject) anObject).loadSounds(soundName);
				
				soundName = null;	
			}
			
		}
	}

    /**
     * Use Default object and title to load sounds
     */
    public void loadSounds() {
        do {
            loadDialog(titleName + dialogIndex++);
            if (soundName != null)
                object.loadSounds(soundName);
        } while (!isAtEndOfDialog());
    }
	
	/**
	 * I look for the Title in the text file which will be in format [Title] and
	 * load the dialog into memory.
	 * 
	 * @param titleName
     * @deprecated
	 */
	public void loadDialog(String titleName) {
			dialog = "";
			try {
				// Close any previous stream opened
				if (fileReader != null)
					fileReader.close();

				fileReader = textFile.read();
				readDialog(titleName);
				dialogIndex = 0;
				fileReader.close();
				return;
			} catch (Exception e) {
				System.err.println(e.getMessage());
				return;
			}
	}

	/**
	 * I return if the dialog has finished
	 * 
	 * @return
	 */
	public boolean isAtEndOfDialog() {
		//return dialogTimer <= 0 && dialogIndex >= dialog.length();
		return dialogIndex >= dialog.length();
	}
	
	/**
	 * Draw next dialog in message box. Return false if it is not ready to draw next section
	 *
	 */
	public boolean setNextDialog() {
		// make sure this is not being called multiple times at once
		if (dialogTimer <= 0) {

			// end of dialog
			if (dialogIndex >= dialog.length()) {
				// keep timer at zero
				dialogTimer = 0;
				return false;
			} else {
				// reset timer
				dialogTimer = TEXT_BALLOON_CHANGE_TIMER;
			}

			// split dialog if too long
			if (dialog != null && dialog.length() > TEXT_BALLOON_LENGTH) {
				int lastSpaceIndex = 0;
				char next = ' ';

				// Find last space of 50 or less characters
				for (int i = dialogIndex, j = dialogIndex + TEXT_BALLOON_LENGTH; (i < dialog.length()) && (i < j); i++) {
					next = dialog.charAt(i);

					// keep track of last space
					if (' ' == next)
						lastSpaceIndex = i;

					// set last word of dialog
					if (i >= dialog.length() - 1) {
						lastSpaceIndex = dialog.length();
					}
				}
				// Grab words to last space
				drawMessage = dialog.substring(dialogIndex, lastSpaceIndex);
				dialogIndex = lastSpaceIndex;
			} else {
				drawMessage = dialog;
				dialogIndex = dialog.length();
			}
		}
		return true;
	}

	/**
	 * Continuously draw message balloon. Call drawNextDialog to move to next
	 * 
	 * @param batch
	 * @param xPos
	 * @param yPos
     * @deprecated
	 */
	public void drawDialog(SpriteBatch batch, AbstractSpikeQuestObject anObject, float xPos, float yPos) {
		currentXPos = xPos;
		currentYPos = yPos;
		
		drawDialog(batch, anObject);

	}

    /**
     * Draw the object with default positions
     * @param batch
     */
    public void drawDialog(SpriteBatch batch) {
        currentXPos = object.getCenterX();
        currentYPos = object.getCenterY() + TEXT_BALLOON_Y_OFFSET;
        if (draw)
            drawDialog(batch, object);
    }
	
	/**
	 * Continuously draw message balloon. Call drawNextDialog to move to next. Draws starting
	 * at the middle. Also plays sound effect from object if it is capable
	 *  
	 * @param batch
	 */
	public void drawDialog(SpriteBatch batch, AbstractSpikeQuestObject anObject) {
		
		batch.draw(messageBox, currentXPos - messageBox.getWidth()/2, currentYPos);
		
		message.drawWrapped(batch, drawMessage, currentXPos + TEXT_BALLOON_X_OFFSET - messageBox.getWidth()/2, currentYPos
				+ TEXT_BALLOON_Y_OFFSET, TextBalloonWrapWidth);

		if (soundName != null) {
			
			if (anObject != null && anObject instanceof StandardObject) { 
				((StandardObject) anObject).playSoundEffect(soundName);
				((StandardObject) anObject).setSoundVolume(soundVolume);
			}
			
			soundName = null;
		}

		if (dialogTimer > 0)
			dialogTimer--;

	}

	// check for sounds
	// checkSounds(drawMessage);

	public float getCurrentXPos() {
		return currentXPos;
	}

	public void setCurrentXPos(float currentXPos) {
		this.currentXPos = currentXPos;
	}

	public float getCurrentYPos() {
		return currentYPos;
	}

	public void setCurrentYPos(float currentYPos) {
		this.currentYPos = currentYPos;
	}

	/**
	 * I read the whole dialog for a title. Rules are (SoundEffectName) plays
	 * sound effect if it exists. [Title] is the title of the dialog text looks
	 * for. "Text" all text, including Sound Effect Names, goes in the quotation
	 * marks under the Title
	 * 
	 * @throws IOException
	 */
	private void readDialog(String aTitleName) throws IOException {
		int next = 0;

		next = fileReader.read();

		while (next != -1) {
			nextChar = (char) next;
			// Look for title names
			if ('[' == nextChar) {
				if (readWord(']').equals(aTitleName)) {
					
					nextChar = (char) fileReader.read();
					while (nextChar != '"' && nextChar != -1) {
						
						//check for sound effects
						if (soundName == null && '{' == nextChar) {
							soundName = readWord(',');
							soundVolume = Float.parseFloat(readWord('}'));
						}
						
						nextChar = (char) fileReader.read();
					}

					dialog = dialog + readWord('"');
					break;
				}
			}

			next = fileReader.read();
		}

	}

	/**
	 * read word to the end character
	 * 
	 * @param endCharacter
	 * @throws IOException
	 */
	private String readWord(char endCharacter) throws IOException {
		String aWord = "";
		nextChar = (char) fileReader.read();

		while (nextChar != (char) -1 && nextChar != endCharacter) {
			aWord = aWord + nextChar;
			nextChar = (char) fileReader.read();
		}

		return aWord;
	}

	@Override
	public void dispose() {
		try {
			fileReader.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		super.dispose();
	}
}
