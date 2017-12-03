package com.brendanmccluer.spikequest.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Comparator;

/**
 * Created by brend on 11/20/2017.
 */

public class SpikeQuestActorComparator implements Comparator< Actor > {
    @Override
    public int compare(Actor arg0, Actor arg1) {
        if (arg0.getY() > arg1.getY()) {
            return -1;
        } else if (arg0.getY() == arg1.getY()) {
            return 0;
        } else {
            return 1;
        }
    }
}
