package com.brendanmccluer.spikequest.applejackgame.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.brendanmccluer.spikequest.applejackgame.objects.AppleTreeObject;

/**
 * Created by brend on 11/10/2017.
 */

public class ShakeTreeAction extends Action {
    AppleTreeObject treeObject;

    public ShakeTreeAction(AppleTreeObject treeObject) {
        this.treeObject = treeObject;
    }

    @Override
    public boolean act(float delta) {
        treeObject.shake();
        return true;
    }
}
