package com.brendanmccluer.spikequest.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestAssets;
import com.brendanmccluer.spikequest.common.objects.TimerObject;
import com.brendanmccluer.spikequest.objects.AbstractSpikeQuestObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private String drawMessage = ""; // portion of dialog that is drawn
	private int dialogIndex = 0;
	private int dialogTimer = 0; // used to keep dialog from being called
	private boolean textBoxLoaded, isFinished = false;
	private float currentXPos = 0;
	private float currentYPos = 0;
    private SpikeQuestTextObject currentTextObject;
	private Scanner scanner = null;
	private String textFilePath;
    //used by Dialog Controller
	protected boolean waitForAnimation, noInput;
	protected SpikeQuestTextObject[] textObjects;
	protected TimerObject timer = null;

	/**
	 * Create new text balloon with no sound effects
	 */
	public SpikeQuestMultiTextBalloon(String textFilePath, SpikeQuestTextObject... spikeQuestTextObjects) {
		super(SpikeQuestAssets.TEXT_BALLOON_PATH, "Texture");
		textObjects = spikeQuestTextObjects;
		this.textFilePath = textFilePath;
		scanner = new Scanner(Gdx.files.internal(textFilePath).read());
		loadObjectAttributes();
	}

	@Override
	public boolean isLoaded() {
		if (!textBoxLoaded && super.isLoaded()) {
			messageBox = (Texture) getAsset(SpikeQuestAssets.TEXT_BALLOON_PATH, "Texture");
			TextBalloonWrapWidth = messageBox.getWidth() - 10;
			message = new BitmapFont();
			message.setColor(Color.BLACK);
			textBoxLoaded = true;
		}
		return textBoxLoaded;
	}

	public void playSound(String soundName, float soundVolume) {
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
        try {
            readImports();
        } catch (Exception e) {
            System.err.println("Error while reading text file.");
            e.printStackTrace();
            return;
        }
        finally {
            try {
				reset();
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
        scanner.close();
		scanner = new Scanner(Gdx.files.internal(textFilePath).read());
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
	 * Execute next dialog in message box.
	 *
	 */
	public void executeNext() {
		// make sure this is not being called multiple times at once
		if (dialogTimer <= 0) {
			// end of dialog
			if (dialogIndex >= dialog.length()) {
				// keep timer at zero
				dialogTimer = 0;
				dialog = "";
				noInput = false;

                try {
                    if(!readAndExecute())
                        isFinished = true;
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

	private void invokeMethod(Object object, String methodName, String[] methodParams) {
		boolean methodFound = false;
		Object t = object;
		Method[] allMethods = object.getClass().getMethods();
		try {
			for (Method m : allMethods) {
				String mname = m.getName();
				if (mname.equalsIgnoreCase(methodName) && m.getParameterCount() == methodParams.length) {
					try {
						methodFound = true;
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
		if(!methodFound) {
			System.err.println(String.format("Method '%s' with %s parameters not found for %s", methodName, methodParams.length, object.getClass().toString()));
		}
	}

	private Object castParameter(String aType, String aParameter) {
		if("INT".equalsIgnoreCase(aType))
			return Integer.parseInt(aParameter);
		if("STRING".equalsIgnoreCase(aType))
			return aParameter;
		if("FLOAT".equalsIgnoreCase(aType))
			return Float.parseFloat(aParameter);
		if("BOOLEAN".equalsIgnoreCase(aType))
			return Boolean.parseBoolean(aParameter);
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
		batch.draw(messageBox, currentXPos - messageBox.getWidth()/2, currentYPos);
		message.drawWrapped(batch, drawMessage, currentXPos + TEXT_BALLOON_X_OFFSET - messageBox.getWidth()/2, currentYPos
				+ TEXT_BALLOON_Y_OFFSET, TextBalloonWrapWidth);
		if (dialogTimer > 0)
			dialogTimer--;
	}

	public boolean isDialogEmpty() {
		return dialog == null || dialog.isEmpty();
	}

    /**
     * I set the object currently focused on
     */
    private SpikeQuestTextObject getTextObject(String aTitleName) {
        SpikeQuestTextObject returnTextObject = null;
        for (SpikeQuestTextObject textObject : textObjects) {
            if (textObject.title.equalsIgnoreCase(aTitleName)) {
                returnTextObject = textObject;
				break;
            }
        }
		return returnTextObject;
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

    private void readImports() throws IOException {
        String pattern = "\\+(.+);";
        boolean stop = false;
		//get next line with the pattern +...;
		while (!stop) {
			String line = scanner.findInLine(pattern);
			if (line != null) {
				String[] imports = extractPattern(pattern,line).split(",");
				for (int i=0; i < imports.length; i++) {
					setAsset(imports[i] , "Sound");
				}
				return;
			}
            else if(scanner.hasNextLine())
                scanner.nextLine();
            else
                stop = true;
        }
    }

	/**
	 * I look for all methods before "STOP" and execute them
	 * @return
	 * @throws IOException
     */
	private boolean readAndExecute() throws IOException {
		String pattern = "@(.+)";
		boolean commandFound = false;
        boolean stop = false;
        while(!stop) {
            //NOTE: If successful, findInLine will move scanner to next line automatically
			String command = scanner.findInLine(pattern);
			if(command != null) {
				commandFound = true;
				try {
					executeCommand(extractPattern(pattern, command));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(scanner.findInLine("STOP") != null)
				stop = true;
            else if(scanner.hasNextLine())
                scanner.nextLine();
            else
                stop = true;
		}
        return commandFound;
	}

	private void executeCommand(String command) throws Exception {
        String commandName = extractPattern("(.+)\\{", command);
		String commandBody = extractPattern("\\{(.*)\\}", command);
        if("Sound".equalsIgnoreCase(commandName)) {
            String[] commandArgs = commandBody.split(",");
            if(commandArgs.length != 2) {
                throwUsageError(commandName, 2);
                return;
            }
            String soundName = commandArgs[0];
            float soundVolume = Float.parseFloat(commandArgs[1]);
            playSound(soundName, soundVolume);
        }
        //Syntax methodObjectName,methodName(arguments...)
		//Use "Screen" in methodObjectName to perform game screen operations
        else if("Animate".equalsIgnoreCase(commandName)) {
            String[] commandArgsPlusMethod = commandBody.split("\\(");
            if(commandArgsPlusMethod.length != 2) {
                throw new Exception("Animate command missing '(' character");
            }
			String[] commandArgs = commandArgsPlusMethod[0].split(",");
			if(commandArgs.length != 2) {
				throwUsageError(commandName, 2);
			}
            String methodObjectName = commandArgs[0];
            String methodName = commandArgs[1];
            String[] methodParams = commandArgsPlusMethod[1].replace(")","").split(",");
			if(methodParams.length == 1 && methodParams[0].isEmpty())
				methodParams = new String[0]; //pass empty string array for null parameters
			if("Screen".equalsIgnoreCase(methodObjectName))
				invokeMethod(SpikeQuestGame.instance.getScreen(), methodName, methodParams);
			else
				invokeMethod(getTextObject(methodObjectName).object, methodName, methodParams);
        }
        else if("Talk".equalsIgnoreCase(commandName)) {
            String[] commandArgs = commandBody.split(",\"");
			if (commandArgs.length != 2) {
				throw new Exception("Talk command missing beginning '\"' character");
			}
			String text = commandArgs[1];
			if (text.charAt(text.length()-1) != '"') {
				throw new Exception("Talk command missing ending '\"' character");
			}
            currentTextObject = getTextObject(commandArgs[0]);
            currentTextObject.index++;
            dialog = text.substring(0,text.length()-1);
        }
        else if("Wait".equalsIgnoreCase(commandName)) {
			if(commandBody == null || commandBody.isEmpty())
				waitForAnimation = true;
			else {
				String[] commandArgs = commandBody.split(",");
				if(commandArgs.length != 2) {
					throwUsageError(commandName, 2);
					return;
				}
				try {
					int waitMinutes = Integer.parseInt(commandArgs[0]);
					int waitSeconds = Integer.parseInt(commandArgs[1]);
					timer = new TimerObject(waitMinutes, waitSeconds);
					timer.startTimer();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
        else if("NoInput".equalsIgnoreCase(commandName))
			noInput = true;
		else
            System.err.println("Unrecognized command " + commandName);
    }

    private String extractPattern(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(text);
        String result = "";
        if(!m.find())
            System.err.println("Pattern not found " + regex);
        else
            result = m.group(1);
        return result;
    }

    private Exception throwUsageError(String commandName, int i) {
		return new Exception(String.format("Usage error executing command {0}. Expected {1} arguments", commandName, i));
	}

	@Override
	public void dispose() {
		try {
			scanner.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		super.dispose();
        textObjects = null;
	}
}
