package com.brendanmccluer.spikequest.applejackgame.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.brendanmccluer.spikequest.actors.SpikeQuestAbstractAnimatedActor;
import com.brendanmccluer.spikequest.applejackgame.objects.AGTimerObject;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by brend on 11/4/2017.
 */

public class BigMacActor extends SpikeQuestAbstractAnimatedActor {
    private static final float strength = 0.01f;
    private Texture texture;
    private Sprite sprite;
    public Animation animStand, animWalk, animBuck;
    public AppleTreeActor tree1, tree2, tree3;
    public AppleTreeActor targetTree;
    public boolean isLocation1Ready, isLocation2Ready, isLocation3Ready;

    public int speed = 100;
    public State state = State.REST;
    public AGTimerObject timerObject;
    public float percentAddToBucket = 0.01f;

    public enum State {
        REST,
        TARGETING,
        BUCKING_TREE
    }

    private Action atTreeAction = new Action() {
        @Override
        public boolean act(float delta) {
            Gdx.app.debug(tag, "Start bucking tree");
            state = State.BUCKING_TREE;
            targetTree.state = AppleTreeActor.State.SHAKE;
            return true;
        }
    };

    public BigMacActor(String tag) {
        super(tag);
        timerObject = new AGTimerObject();
    }

    public void buck() {
        Gdx.app.debug(tag, "Bucking Animation started");
    }

    public void stopBucking() {
        //TODO: CHANGE ANIMATION
        Gdx.app.debug(tag, "Bucking Animation stopped");
    }

    public void reset() {
        state = State.REST;
    }

    @Override
    protected void update(float delta) {
        if (state == State.TARGETING) {
            if ((!isLocation1Ready && targetTree == tree1) || (!isLocation2Ready && targetTree == tree2)
                    || (!isLocation3Ready && targetTree == tree3)) {
                stop();
            }
        }
        if (state == State.REST) {
            //TODO calculate closest location
            if (isLocation1Ready) {
                targetNewLocation(tree1);
            }
            else if (isLocation2Ready) {
                targetNewLocation(tree2);
            }
            else if (isLocation3Ready) {
                targetNewLocation(tree3);
            }
            return;
        }
        if (state == State.BUCKING_TREE) {
            if (timerObject.percentFull >= 1) {
                Gdx.app.debug(tag, "Finished bucking tree");
                state = State.REST;
                targetTree.state = AppleTreeActor.State.REST;
                timerObject.reset();
                targetTree = null;
            }
            else {
                float percentage = percentAddToBucket * Math.min(0.25f, delta);
                timerObject.updatePercentage(percentage);
            }

        }
    }

    private void targetNewLocation(AppleTreeActor tree) {
        Vector2 location = new Vector2(tree.getCenterX() + getWidth()/2, tree.getY());
        Gdx.app.debug(tag, "Targeting new location " + location.x + "," + location.y);
        float distance = location.dst(getX(), getY());
        targetTree = tree;
        addAction(sequence(moveTo(location.x, location.y, distance/ speed, Interpolation.linear), atTreeAction));
        state = State.TARGETING;
    }

    private void stop() {
        Gdx.app.debug(tag, "Stop moving");
        targetTree = null;
        state = State.REST;
        clearActions();
    }

    @Override
    protected void setAnimations(TextureAtlas textureAtlas, String regionName) {
        animStand = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_stand"), Animation.PlayMode.LOOP);
        animBuck = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_buck"), Animation.PlayMode.LOOP);
        animWalk = new Animation(1.0f/10.0f, textureAtlas.findRegions(regionName + "_walk"), Animation.PlayMode.LOOP);
    }
}
