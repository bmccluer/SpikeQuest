package com.brendanmccluer.spikequest.applejackgame.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.brendanmccluer.spikequest.interfaces.CreatableObject;

/**
 * Created by brend on 11/5/2017.
 */

public class DropSpotObject extends Actor {
    private static String TAG = "DropSpotObject";
    public Actor heldActor;

    public boolean isFull() {
        return heldActor != null;
    }

    public Actor removeActor() {
        Gdx.app.debug(TAG, "Removing Actor");
        Actor removedActor = heldActor;
        heldActor = null;
        return removedActor;
    }
}
