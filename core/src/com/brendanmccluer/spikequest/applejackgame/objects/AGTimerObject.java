package com.brendanmccluer.spikequest.applejackgame.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brend on 1/29/2018.
 */

public class AGTimerObject {
    public enum State {
        HIDDEN,
        RUNNING,
        PAUSE
    }
    public State state = State.HIDDEN;
    public float percentFull = 0;
    public Vector2 pos = new Vector2();

    public void reset() {
        percentFull = 0;
        state = State.HIDDEN;
    }

    public void updatePercentage(float percent) {
        percentFull = Math.min(1, percentFull + percent);
    }

    public void draw() {
        //TODO draw timer
        //if (state != State.HIDDEN)
    }


}
