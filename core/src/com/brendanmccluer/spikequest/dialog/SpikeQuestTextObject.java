package com.brendanmccluer.spikequest.dialog;

import com.brendanmccluer.spikequest.objects.StandardObject;

import java.util.Stack;

/**
 * Created by brend on 1/22/2017.
 * Used in SpikeQuestMultiTextBalloon
 */

public class SpikeQuestTextObject {
    protected StandardObject object;
    protected int index = 0;
    protected String title = "";

    public SpikeQuestTextObject (StandardObject object, String title) {
        this.object = object;
        this.title = title;
    }
}
