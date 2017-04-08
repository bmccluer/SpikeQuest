package com.brendanmccluer.spikequest.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.SpikeQuestStaticFilePaths;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;
import com.brendanmccluer.spikequest.objects.StandardObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.lang.System.err;

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
    protected String soundName, methodName = null;
	protected String[] methodParams = null;
	protected float soundVolume = 0;
	protected SpikeQuestTextObject[] textObjects;

	/**
	 * Create new text balloon with no sound effects
	 */
	public SpikeQuestMultiTextBalloon(String textFilePath, SpikeQuestTextObject... spikeQuestTextObjects) {
		super(SpikeQuestStaticFilePaths.TEXT_BALLOON_PATH, "Texture");
		textFile = Gdx.files.internal(textFilePath);
		textObjects = spikeQuestTextObjects;
		loadObjectAttributes();
	}

	@Override
	public boolean isLoaded() {
		if (!textBoxLoaded && super.isLoaded()) {
			messageBox = (Texture) getAsset(SpikeQuestStaticFilePaths.TEXT_BALLOON_PATH, "Texture");
			TextBalloonWrapWidth = messageBox.getWidth() - 10;
			message = new BitmapFont();
			message.setColor(Color.BLACK);
			textBoxLoaded = true;
			try {
				fileReader = textFile.read();
				String aTitleName = readDialog();
				setCurrentObject(aTitleName);
				setNextDialog();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return textBoxLoaded;
	}

	public void playSound() {
		Sound sound = null;
		if (soundName != null) {
			sound = (Sound) getAsset(soundName, "Sound");
			sound.play(soundVolume);
		}

	}

	/**
	 * I load all sounds stored in the text file
	 *
	 */
	private void loadObjectAttributes() {
        dialog = "";
        String aTitleName = null;
        try {
            fileReader = textFile.read();
            aTitleName = readDialog();
            if (aTitleName != null) {
				if ("IMPORT".equalsIgnoreCase(aTitleName))
					importSounds();
				return;
			}
        } catch (Exception e) {
            System.err.println("Error while reading text file.");
            e.printStackTrace();
            return;
        }
        finally {
            try {
				fileReader.close();
				reset();
            }
            catch (Exception e) {
                System.err.println("Error: Could not close file reader.");
                e.printStackTrace();
            }
        }
    }

	private void importSounds() {
		StringTokenizer tokenizer = new StringTokenizer(dialog,",");
		while (tokenizer.hasMoreTokens()) {
			setAsset(tokenizer.nextToken(), "Sound");
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
			if (soundName != null) {
				playSound();
				soundName = null;
			}
			if (methodName != null) {
				invokeMethod(getCurrentObject().object);
				methodName = null;
			}

		}
	}

	private void invokeMethod(StandardObject object) {
		try {
			Object t = object;

			Method[] allMethods = object.getClass().getMethods();
			for (Method m : allMethods) {
				String mname = m.getName();
				if (mname.startsWith(methodName)) {
					try {
						//convert parameters
						Object[] parameters = new Object[m.getParameterCount()];
						for (int i = 0; i < parameters.length; i++) {
							StringTokenizer tokenizer = new StringTokenizer(methodParams[i],"#");
							if (tokenizer.countTokens() == 2)
								parameters[i] = castParameter(tokenizer.nextToken(), tokenizer.nextToken());
						}
						Object o = m.invoke(t, parameters);

						// Handle any exceptions thrown by method to be invoked.
					} catch (InvocationTargetException x) {
						Throwable cause = x.getCause();
						err.format("invocation of %s failed: %s%n",
								mname, cause.getMessage());

					}
					break;
				}
			}

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private Object castParameter(String aType, String aParameter) {
		if ("INT".equalsIgnoreCase(aType))
			return Integer.parseInt(aParameter);
		if ("STRING".equalsIgnoreCase(aType))
			return aParameter;
		if ("FLOAT".equalsIgnoreCase(aType))
			return Float.parseFloat(aParameter);
		System.err.println("Unknown Type: " + aType);
		return null;
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
				return;
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
					//check for method calls
					//format: '@' + method + ',' + parameters + ';'
                    else if (methodName == null && '@' == nextChar) {
                        StringTokenizer aTokenizer = new StringTokenizer(readWord(';'),",");
						ArrayList<String> parameters = new ArrayList<String>();

						if (aTokenizer.hasMoreTokens())
							methodName = aTokenizer.nextToken();
						while (aTokenizer.hasMoreTokens()) {
							parameters.add(aTokenizer.nextToken());
						}


						methodParams = new String[parameters.size()];
						parameters.toArray(methodParams);
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
	public void dispose() {
		try {
			fileReader.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		super.dispose();
        textObjects = null;
	}
}
