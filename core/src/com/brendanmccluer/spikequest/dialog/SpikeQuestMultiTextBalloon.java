package com.brendanmccluer.spikequest.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Holds multiple texts for different objects
 */
public class SpikeQuestMultiTextBalloon extends AbstractSpikeQuestObject {
	private final static int TEXT_BALLOON_LENGTH = 350;
	private final static int TEXT_BALLOON_CHANGE_TIMER = 12;
	private final static int TEXT_BALLOON_X_OFFSET = 8;
	private final static int TEXT_BALLOON_Y_OFFSET = 160;

	private int TextBalloonWrapWidth = 0;
	private BitmapFont message = null;
	private Texture messageBox = null;
	private String dialog = ""; // entire dialog read from file
	private FileHandle textFile = null;
	private InputStream fileReader = null;
	private char nextChar = 0;
	private String drawMessage = ""; // portion of dialog that is drawn
	private int dialogIndex = 0;
	private int dialogTimer = 0; // used to keep dialog from being called
	private boolean textBoxLoaded, isFinished = false;
	private float currentXPos = 0;
	private float currentYPos = 0;
    private SpikeQuestTextObject currentTextObject;

    //used by Dialog Controller
    protected String soundName, animationName = null;
	protected float soundVolume = 0;
	protected SpikeQuestTextObject[] textObjects;

	/**
	 * Create new text balloon with no sound effects
	 */
	public SpikeQuestMultiTextBalloon(String textFilePath, SpikeQuestTextObject... spikeQuestTextObjects) {
		super(SpikeQuestStaticFilePaths.TEXT_BALLOON_PATH, "Texture");
		textFile = Gdx.files.internal(textFilePath);
		textObjects = spikeQuestTextObjects;
	}

	@Override
	public boolean isLoaded() {
		if (!textBoxLoaded && super.isLoaded()) {
			messageBox = (Texture) getAsset(SpikeQuestStaticFilePaths.TEXT_BALLOON_PATH, "Texture");
			TextBalloonWrapWidth = messageBox.getWidth() - 10;
			message = new BitmapFont();
			message.setColor(Color.BLACK);
			textBoxLoaded = true;
			loadObjectAttributes();
            //open the file reader
            fileReader = textFile.read();
			setNextDialog();
		}
		return textBoxLoaded;
	}

	/**
	 * I load all sounds and animations stored in the text file
	 *
	 */
	private void loadObjectAttributes() {
        dialog = "";
        String aTitleName = null;
        try {
            fileReader = textFile.read();
            aTitleName = readDialog();
            while (aTitleName != null) {
                setCurrentObject(aTitleName);
                if (currentTextObject != null) {
                    if (soundName != null)
                        currentTextObject.object.loadSounds(soundName);
                    if (animationName != null);
                        //TODO load animation name
                }
                aTitleName = readDialog();
            }
            return;
        } catch (Exception e) {
            System.err.println("Error while reading text file.");
            e.printStackTrace();
            return;
        }
        finally {
            try {
				reset();
                fileReader.close();
            }
            catch (Exception e) {
                System.err.println("Error: Could not close file reader.");
                e.printStackTrace();
            }
        }
    }

	/**
	 * I reset indexes and other attributes
	 */
	private void reset() {
		dialog = "";
		for (SpikeQuestTextObject textObject : textObjects) {
			textObject.index = 0;
		}
	}

	/**
	 * I return if the dialog has finished
	 * 
	 * @return
	 */
	public boolean isAtEndOfDialog() {
		return isFinished;
	}
	
	/**
	 * Draw next dialog in message box. Return false if it is not ready to draw next section
	 *
	 */
	public void setNextDialog() {
		// make sure this is not being called multiple times at once
		if (dialogTimer <= 0) {
			// end of dialog
			if (dialogIndex >= dialog.length()) {
				// keep timer at zero
				dialogTimer = 0;
				dialog = "";
                try {
                    String nextTitle = null;
                    currentTextObject.index++;
                    nextTitle = readDialog();
                    if (nextTitle == null) {
                        isFinished = true;
                        return;
                    }
                    setCurrentObject(nextTitle);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
	}

    /**
	 * Continuously draw message balloon. Call drawNextDialog to move to next
	 * 
	 * @param batch
	 * @param xPos
	 * @param yPos
	 */
	public void drawDialog(SpriteBatch batch, AbstractSpikeQuestObject anObject, float xPos, float yPos) {
		currentXPos = xPos;
		currentYPos = yPos;
		drawDialog(batch);
	}

	/**
	 * Continuously draw message balloon. Call drawNextDialog to move to next. Draws starting
	 * at the middle. Also plays sound effect and animation from current object if it is capable
	 *  
	 * @param batch
	 */
	public void drawDialog(SpriteBatch batch) {
	/*	if (currentTextObject != null) {
            currentXPos = currentTextObject.object.getCenterX();
            currentYPos = currentTextObject.object.getCenterY();
        }*/
        batch.draw(messageBox, currentXPos - messageBox.getWidth()/2, currentYPos);
		message.drawWrapped(batch, drawMessage, currentXPos + TEXT_BALLOON_X_OFFSET - messageBox.getWidth()/2, currentYPos
				+ TEXT_BALLOON_Y_OFFSET, TextBalloonWrapWidth);

		if (dialogTimer > 0)
			dialogTimer--;
	}

    /**
     * I set the object currently focused on
     */
    private void setCurrentObject(String aTitleName) {
        currentTextObject = null;
        for (SpikeQuestTextObject textObject : textObjects) {
            if (textObject.title.equalsIgnoreCase(aTitleName)) {
                currentTextObject = textObject;
            }

        }
    }

    public SpikeQuestTextObject getCurrentObject() {
        return currentTextObject;
    }

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
	 * I read the next dialog for the next title name. Returns the title name
	 * 
	 * @throws IOException
     * @return String
	 */
	private String readDialog() throws IOException {
		int next = 0;
        String aTitleName = null;

		next = fileReader.read();

		while (next != -1) {
			nextChar = (char) next;
			// Look for title names
			if ('[' == nextChar) {
                aTitleName = readWord(']');
                nextChar = (char) fileReader.read();
                while (nextChar != '"' && nextChar != -1) {

                    //check for sound effects
                    if (soundName == null && '{' == nextChar) {
                        soundName = readWord(',');
                        soundVolume = Float.parseFloat(readWord('}'));
                    }
                    else if (animationName == null && '@' == nextChar) {
                        animationName = readWord(';');
                    }

                    nextChar = (char) fileReader.read();
                }
                dialog = dialog + readWord('"');
                break;
			}

			next = fileReader.read();
		}
        return aTitleName;
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
	public void discard() {
		try {
			fileReader.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		super.discard();
        textObjects = null;
	}
}
