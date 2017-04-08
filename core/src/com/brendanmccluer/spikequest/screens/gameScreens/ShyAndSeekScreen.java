package com.brendanmccluer.spikequest.screens.gameScreens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.brendanmccluer.covers.shyAndSeekCovers.*;
import com.brendanmccluer.spikequest.SpikeQuestGame;
import com.brendanmccluer.spikequest.SpikeQuestSaveFile;
import com.brendanmccluer.spikequest.cameras.SpikeQuestCamera;
import com.brendanmccluer.spikequest.common.objects.ScoreBoardObject;
import com.brendanmccluer.spikequest.common.objects.ScoreControlObject;
import com.brendanmccluer.spikequest.common.objects.TimerObject;
import com.brendanmccluer.spikequest.managers.SpikeQuestScreenManager;
import com.brendanmccluer.spikequest.objects.AbstractCoverObject;
import com.brendanmccluer.spikequest.objects.AbstractPopUpObject;
import com.brendanmccluer.spikequest.objects.GemObject;
import com.brendanmccluer.spikequest.objects.MouseRedCircleObject;
import com.brendanmccluer.spikequest.objects.animals.AbstractAnimalObject;
import com.brendanmccluer.spikequest.objects.animals.BatObject;
import com.brendanmccluer.spikequest.objects.animals.BeaverObject;
import com.brendanmccluer.spikequest.objects.animals.FrogObject;
import com.brendanmccluer.spikequest.objects.animals.MouseObject;
import com.brendanmccluer.spikequest.objects.animals.OwlObject;
import com.brendanmccluer.spikequest.objects.ponies.DerpyObject;
import com.brendanmccluer.spikequest.objects.ponies.FluttershyObject;
import com.brendanmccluer.spikequest.screens.AbstractSpikeQuestScreen;
import com.brendanmccluer.spikequest.sounds.SpikeQuestMusic;

public class ShyAndSeekScreen extends AbstractSpikeQuestScreen {
	private static final String BACKDROP_SCREEN = "backdrop/fluttershyCottage.png";
	private static final String FLUTTERSHY_HEAD = "fluttershy/shyAndSeek/backOfHead.png";
	private static final String BACKGROUND_MUSIC = "music/shyAndSeekMusic.mp3";
	private static final String[] FLUTTERSHY_SOUNDS = {"comeOn","it'sOk","nothingToBeAfraidOf","yay"};
	private TimerObject animalTimer = null;
	private List<AbstractAnimalObject> availableAnimals = null;
	private List<AbstractCoverObject> availableCovers = null;
	private List<AbstractPopUpObject> animalDrawOnCoversList = null;
	private List<GemObject> gemObjects = null;
	private int animalTimerSeconds = 2;
	private int animalPopupSeconds = 5;
	private Random randomGenerator = null;
	private float animalMoveSpeed = 2;
	private int animalFoundScore = 50;
	private AbstractAnimalObject animalToFind = null;
	private boolean buttonHold;
	private boolean fluttershyPopUp = false;
	private float fluttershyPopUpSpeed = 3f;
	private SpikeQuestMusic backgroundMusic = null;
	private FluttershyObject fluttershyObject = null;
	private ScoreControlObject scoreControl = null;
	private int bonusMult = 2; 
	private BitmapFont bonusFont = null;
	private Sprite fluttershyHeadSprite = null;
	private DerpyObject derpyObject = null;
	private ScoreBoardObject scoreBoardObject = null;
	private AbstractCoverObject[] coverObjects = null;
	private AbstractAnimalObject[] animalObjects = null;
	private MouseRedCircleObject aCircleObject = null;

	/**
	 * Initial load
	 * @param game
	 */
	public ShyAndSeekScreen(SpikeQuestGame game) {
		super(game);
	}

	@Override
	public void initialize() {
		animalTimer = new TimerObject();
		availableAnimals = new ArrayList<>();
		availableCovers = new ArrayList<>();
		animalDrawOnCoversList = new ArrayList<>();
		gemObjects = new ArrayList<>();
		randomGenerator = new Random();
		fluttershyObject = new FluttershyObject();
		scoreControl = new ScoreControlObject();
		bonusFont = new BitmapFont();
		//use timer with score
		scoreBoardObject = new ScoreBoardObject(true);
		coverObjects = new AbstractCoverObject[] {
				new AboveDoorRightCover()
				,new BirdHouseCover()
				,new BridgeRightCover()
				,new BushLeftOfDoorCover()
				,new CaveLeftCover()
				,new DoorCover()
				,new LogCover()
				,new RoadLeftCover()
				,new TopLeftWindowCover()
				,new TopOfCottageCover()
				,new TreeCaveLeftCover()
				,new TreeTopRightCover()
				,new TunnelUnderBridgeCover()
				,new WindowFrontLeftCover()
		};
		aCircleObject = new MouseRedCircleObject();
		animalObjects = new AbstractAnimalObject[] {
				new BatObject()
				,new BeaverObject()
				,new FrogObject()
				,new MouseObject()
				,new OwlObject()
				,new BatObject()
				,new BeaverObject()
				,new FrogObject()
				,new MouseObject()
				,new OwlObject()
		};
		gameCamera = new SpikeQuestCamera(1050, 1055, 594);
		game.assetManager.setAsset(BACKDROP_SCREEN, "Texture");
		game.assetManager.setAsset(FLUTTERSHY_HEAD, "Texture");
		game.assetManager.setAsset(BACKGROUND_MUSIC, "Music");
		fluttershyObject.loadSounds(FLUTTERSHY_SOUNDS);

		if ("Intro".equalsIgnoreCase(screenType)) {
			derpyObject = new DerpyObject();
			derpyObject.setBanner("Don't forget about Gems!");
		}

		for (AbstractAnimalObject i : animalObjects) {
			availableAnimals.add(i);
			i.setWeight(0);
		}
		for (AbstractCoverObject i : coverObjects)
			availableCovers.add(i);

		createGemObjects(15);
	}

	@Override
	public void render(float delta) {
		
		refresh();
		useLoadingScreen(delta);
		gameCamera.attachToBatch(game.batch);
		
		if (game.assetManager.loadAssets() && coverObjectsLoaded() && aCircleObject.isLoaded() && scoreBoardObject.isLoaded() && animalsLoaded() && GemObject.gemsLoaded(gemObjects) && fluttershyObject.isLoaded() 
				&& (derpyObject == null || derpyObject.isLoaded())) {
			
			if (!screenStart) {
				
				screenStart = true;
				currentBackdropTexture = (Texture) game.assetManager.loadAsset(BACKDROP_SCREEN, "Texture");
				fluttershyHeadSprite = new Sprite((Texture) game.assetManager.loadAsset(FLUTTERSHY_HEAD, "Texture"));
				backgroundMusic = new SpikeQuestMusic((Music) game.assetManager.loadAsset(BACKGROUND_MUSIC, "Music"));
				backgroundMusic.playMusic(true);
				scoreBoardObject.spawn(0,0);
				scoreBoardObject.setFinalCountdown(gameCamera.getCameraWidth()/2, gameCamera.getCameraHeight()/2, 5);
				scoreBoardObject.startTimer(2,15);
				animalTimer.startTimer(0, animalTimerSeconds);
				bonusFont.scale(1.25f);
				
				//spawn Derpy for intro
				if (derpyObject != null) {
					derpyObject.spawn(0, 0);
					derpyObject.spawn(-200,gameCamera.getCameraHeight() - derpyObject.getCollisionRectangle().getHeight());
				}
					
				//scoreControl.disableMove();
				//spawn animals
				for (AbstractAnimalObject i : animalObjects) {
					i.spawn(-300, -300);
				}
				fluttershyHeadSprite.setY(-fluttershyHeadSprite.getHeight());	
				animalToFind = animalObjects[randomGenerator.nextInt(animalObjects.length)];
			}
			
			scoreBoardObject.setCurrentPositionX(gameCamera.getCameraWidth() - scoreBoardObject.getCollisionRectangle().getWidth());
			//aScoreBoardObject.setCurrentPositionY(aSpikeQuestCamera.getCameraHeight() - aScoreBoardObject.getCollisionRectangle().getHeight());
			
			if (scoreBoardObject.isTimerFinished()) {
				gameOver();
				return;
			}
				
			else {
				playGame();
				game.batch.begin();
				game.batch.draw(currentBackdropTexture, 0, 0);
				
				//DEBUGGING ONLY!
				//game.batch.draw(coverObjects[12].getTexture(), aSpikeQuestCamera.getMousePositionX(), aSpikeQuestCamera.getMousePositionY());
				//System.out.println(aSpikeQuestCamera.getMousePositionX() + " " + aSpikeQuestCamera.getMousePositionY());
				//----------------
				drawObjects();
				checkMouse();
				animateFluttershyPopup();
				if (derpyObject != null) {
					if (derpyObject.getCurrentPositionX() < gameCamera.getCameraWidth() + derpyObject.getCollisionRectangle().getWidth() + 100)
						derpyObject.moveRight(5);
					derpyObject.draw(game.batch);
				}	
				game.batch.end();
			}
				
		}
	}
	
	private void gameOver() {
		game.screenStack.push(new SaveScoreScreen(game, scoreBoardObject.getScore(), scoreBoardObject.getGems(), this));
		SpikeQuestSaveFile.setBooleanValue(SpikeQuestSaveFile.IS_SHY_AND_SEEK_COMPLETE_KEY, true);
		dispose();
		SpikeQuestScreenManager.popNextScreen(game);
	}

	/**
	 * I continue playing the game
	 */
	private void playGame() {
		int availableCoverIndex = -1;
		int availableAnimalIndex = -1;
		
		if (animalTimer.isTimerFinished()) {
			
			if (!availableAnimals.isEmpty()) {
				
				//50% random popup or running across screen
				if (randomGenerator.nextBoolean() && !availableCovers.isEmpty()) {
					
					//get random cover and animal
					availableAnimalIndex = randomGenerator.nextInt(availableAnimals.size());
					availableCoverIndex = randomGenerator.nextInt(availableCovers.size());
					
					//set to cover
					availableCovers.get(availableCoverIndex).setPopUpObject(availableAnimals.get(availableAnimalIndex));
					
					//start popup timer for animal (how long it can be "popping up")
					availableAnimals.get(availableAnimalIndex).getTimerObject().startTimer(0, animalPopupSeconds);
					
					//remove from available covers
					availableCovers.remove(availableCoverIndex);
					availableAnimals.remove(availableAnimalIndex);
				}
				else {
					//get random animal
					availableAnimalIndex = randomGenerator.nextInt(availableAnimals.size());
					availableAnimals.get(availableAnimalIndex).moveRandomDirectionX(-50, gameCamera.getWorldWidth() + 50, 0, availableAnimals.get(availableAnimalIndex).isFlyingAnimal() ? gameCamera.getWorldHeight() - 100 : 0, animalMoveSpeed);
					availableAnimals.remove(availableAnimalIndex);
				}
				
				//reset timer
				animalTimer.startTimer(0, animalTimerSeconds);
			}
			
		}
		
		for (AbstractAnimalObject i : animalObjects) {
			if (i.isMovingTowardsPoint()) {
				if (i.isAtPoint())
					resetAnimal(i);
			}
			else if ((i.isBehindCover() && i.getTimerObject().isTimerFinished() && i.isHidden()) 
					|| (i.isMovingRandomly() && ((i.isMovingRandomlyRight() && i.getCurrentPositionX() > gameCamera.getWorldWidth() + 50) 
							|| (!i.isMovingRandomlyRight() && i.getCurrentPositionX() < -50 - i.getCollisionRectangle().getWidth())))) 
							resetAnimal(i);
		}
			
		
	}

	
	private void resetAnimal(AbstractAnimalObject anAnimal) {
		anAnimal.resetSize();
		anAnimal.resetRotation();
		anAnimal.spawn(-200, -200);
		
		if (anAnimal.isBehindCover()) 
			removeAnimalFromCover(anAnimal, getCoverFromAnimal(anAnimal));
		
		if (anAnimal.isMovingRandomly())
			anAnimal.stopMovingRandomly();
		
		if (anAnimal.isMovingTowardsPoint())
			anAnimal.stopMovingTowardsPoint();
		
		availableAnimals.add(anAnimal);
	}

		//debugging only!
		//coverObjects[0].setPopUpObject(animalObjects[3]);
	private void removeAnimalFromCover(AbstractAnimalObject anAnimal, AbstractCoverObject aCover) {
		anAnimal.setBehindCover(false);
		aCover.removePopUpObject();
		availableCovers.add(aCover);
	}

	private AbstractCoverObject getCoverFromAnimal (AbstractAnimalObject anAnimal) {
		//find the animal object belonging to the cover object
		for (AbstractCoverObject z : coverObjects) {
			if (z.getPopUpObject() != null && z.getPopUpObject().equals(anAnimal)) {
				return z;
			}
		}
		return null;
	}
	
	private void checkMouse() {
		boolean allowClick = true;
		boolean screenClicked = Gdx.input.isButtonPressed(Buttons.LEFT);
		AbstractCoverObject aCover = null;
		int points = 0;
		boolean bonus = false;
		
		//do not let the user click and hold
		if (screenClicked) {
			if (buttonHold)
				allowClick = false;
			else
				buttonHold = true;
		}
		else
			buttonHold = false;
			
		//check for mouse over animal. Return if true
		for (AbstractAnimalObject i : animalObjects) {
			if (!i.isMovingTowardsPoint() && i.getCollisionRectangle().contains(gameCamera.getMousePositionX(), gameCamera.getMousePositionY())) {
				if (allowClick && screenClicked) {
					aCover = getCoverFromAnimal(i);
					points = aCover != null ? aCover.getPoints() : animalFoundScore;
					bonus = animalToFind.getClass().equals(i.getClass());
					//if bonus, add extra points and change animal
					if (bonus) {
						scoreControl.createNewScore(points, i.getCenterX(), i.getCenterY(),bonusMult);
						animalToFind = animalObjects[randomGenerator.nextInt(animalObjects.length)];
						scoreBoardObject.addScore(points * bonusMult);
					}
					else {
						scoreControl.createNewScore(points, i.getCenterX(), i.getCenterY());
						scoreBoardObject.addScore(points);
					}
					
					if (randomGenerator.nextBoolean())
						GemObject.spawnRandomGem(gemObjects, i.getCenterX(), i.getCenterY(), i.getCenterY(), false).setSize(i.getCurrentSize());;;
						
					//only change X if fluttershy hiding
					if (fluttershyHeadSprite.getY() <= -fluttershyHeadSprite.getHeight())
						fluttershyHeadSprite.setX(randomGenerator.nextInt(gameCamera.getWorldWidth() - (int) (scoreBoardObject.getCollisionRectangle().getWidth() - fluttershyHeadSprite.getWidth())));
					
					//popup Fluttershy if special animal
					if (bonus) {
						fluttershyPopUp = true;
						fluttershyObject.playSoundEffect(FLUTTERSHY_SOUNDS[randomGenerator.nextInt(4)]);
					}
					//play the animal's noise
					i.playSound();
					i.resetRotation();
					i.resetSize();
					if (i.isBehindCover()) 
						removeAnimalFromCover(i,aCover);
					if (!i.isFlyingAnimal())
						i.setCurrentPositionY(0);
					i.setGroundPosition(0);
					if (bonus)
						i.moveTowardsPoint(fluttershyHeadSprite.getX() + fluttershyHeadSprite.getWidth()/2, 0, 2f);
					else
						resetAnimal(i);
					allowClick = false;
				}
				else 
					aCircleObject.drawOnMouse(game.batch, gameCamera.getMousePositionX(), gameCamera.getMousePositionY());
				return;
			}
		}
		//check for mouse over gem
		for (GemObject i : gemObjects) {
			if (i.getIsActive() && i.getCollisionRectangle().contains(gameCamera.getMousePositionX(), gameCamera.getMousePositionY())) {
				if (allowClick && screenClicked) {
					scoreBoardObject.addGems(i.collectGem());
				}
				else
					aCircleObject.drawOnMouse(game.batch, gameCamera.getMousePositionX(), gameCamera.getMousePositionY());
			}
		}
		
	}
	
	private void animateFluttershyPopup() {
		if (fluttershyPopUp) {
			//go up
			if ((fluttershyHeadSprite.getY() + fluttershyPopUpSpeed) >= 0) 
			{
				fluttershyHeadSprite.setY(0);
				fluttershyPopUp = false;
				for (AbstractAnimalObject i : animalObjects)
					if (i.isMovingTowardsPoint())
						fluttershyPopUp = true;
			}
			else
				fluttershyHeadSprite.setY(fluttershyPopUpSpeed + fluttershyHeadSprite.getY());
		}
		else {
			//go down
			if ((fluttershyHeadSprite.getY() - fluttershyPopUpSpeed) < -fluttershyHeadSprite.getHeight()) 
			{
				fluttershyHeadSprite.setY(-fluttershyHeadSprite.getHeight()); 
			}
			else
				fluttershyHeadSprite.setY(fluttershyHeadSprite.getY() - fluttershyPopUpSpeed);
		}	
	}

	private void drawObjects() {
		
		//draw animals first if behind covers
		for (AbstractPopUpObject i : animalObjects) {
			i.draw(game.batch);
			
			if (i.isBehindCover())
				i.draw(game.batch);
			else
				animalDrawOnCoversList.add(i);
		}
		
		for (AbstractCoverObject i : coverObjects)
			i.draw(game.batch);
		
		scoreBoardObject.draw(game.batch);
		bonusFont.draw(game.batch, " BONUS:", 853.70233f, 47.727295f);
		//System.out.println(gameCamera.getMousePositionX() + " " + gameCamera.getMousePositionY());
		//game.batch.draw(animalToFind.getAnimalTexture(), scoreBoardObject.getCenterX() - 20, scoreBoardObject.getCenterY() - 50, animalToFind.getAnimalTexture().getWidth()/5, animalToFind.getAnimalTexture().getHeight()/5);
		if (animalToFind instanceof OwlObject)
			game.batch.draw(animalToFind.getAnimalTexture(), 994.57477f, -20 ,animalToFind.getAnimalTexture().getWidth()/6, animalToFind.getAnimalTexture().getHeight()/6);
		else 
			game.batch.draw(animalToFind.getAnimalTexture(), 994.57477f, animalToFind instanceof BatObject ? - 40 : 0,animalToFind.getAnimalTexture().getWidth()/5, animalToFind.getAnimalTexture().getHeight()/5);
		
		for (AbstractPopUpObject i : animalDrawOnCoversList)
			i.draw(game.batch);
		GemObject.drawGemObjects(gemObjects, game.batch);
		if (fluttershyHeadSprite.getX() < gameCamera.getCameraWidth()/2) {
			fluttershyHeadSprite.flip(true,false);
			fluttershyHeadSprite.draw(game.batch);
			fluttershyHeadSprite.flip(true,false);
		}
		else
			fluttershyHeadSprite.draw(game.batch);
		
		scoreControl.draw(game.batch);
		animalDrawOnCoversList.clear();
	}

	private boolean coverObjectsLoaded() {
		boolean allLoaded = true;
		
		for (AbstractCoverObject i : coverObjects) {
			
			if (!i.isLoaded())
				allLoaded = false;
		}

		return allLoaded;
	}
	
	private boolean animalsLoaded() {
		boolean allLoaded = true;
		
		for (AbstractPopUpObject i : animalObjects) {
			
			if (!i.isLoaded())
				allLoaded = false;
		}

		return allLoaded;
	}
	
	/**
	 * I create x amount of gem objects
	 * 
	 * @param number
	 */
	private void createGemObjects (int number) {
		
		for (int i = 0; i < number; i++) {
			gemObjects.add(new GemObject());
		}
		
	}
	
	@Override
	public void dispose () {
		
		for (AbstractCoverObject i : coverObjects) 
			i.dispose();
		
		for (AbstractPopUpObject i : animalObjects)
			i.dispose();
		
		for (GemObject i : gemObjects)
			i.dispose();
		
		scoreBoardObject.dispose();
		aCircleObject.dispose();
		gameCamera.discard();
		fluttershyHeadSprite.getTexture().dispose();		
		backgroundMusic.stopMusic();
		backgroundMusic.dispose();
		game.assetManager.disposeAsset(BACKDROP_SCREEN);
		game.assetManager.disposeAsset(FLUTTERSHY_HEAD);
		scoreControl.discard();
		if (derpyObject != null)
			derpyObject.dispose();
		
		derpyObject = null;
		scoreControl = null;
		scoreBoardObject = null;
		aCircleObject = null;
		gameCamera = null;
		coverObjects = null;
		gemObjects = null;
		animalObjects = null;
		fluttershyHeadSprite = null;
		backgroundMusic = null;
		
		super.dispose();
	}
	

	
	
	

}
