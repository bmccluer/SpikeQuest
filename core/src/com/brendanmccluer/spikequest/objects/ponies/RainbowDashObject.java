package com.brendanmccluer.spikequest.objects.ponies;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brendanmccluer.spikequest.objects.StandardObject;

public class RainbowDashObject extends StandardObject {
	private static final String[] filePaths = {"rainbowDash/stand/RainbowDashStand.atlas", "rainbowDash/fly/RainbowDashFly.atlas", "rainbowDash/talk/RainbowDashTalk.atlas", "rainbowDash/crouch/RainbowDashCrouch.atlas"};
	private static final String[] fileTypes = {"TextureAtlas", "TextureAtlas", "TextureAtlas", "TextureAtlas"};
	private static final int[] maxFrames = {12,8,12,1};
	private static final float STARTING_SIZE = 0.1f;
	//private static final int[] soundLengths = {10};
	//private static final String[] soundPaths = {"twilight/talk/TwilightSpike.wav"};
	//private static final String[] soundNames = {"spike"};

	public RainbowDashObject() {
		super(filePaths, fileTypes, maxFrames, STARTING_SIZE, true);
	}

	@Override
	public void resetSize() {
		setSize(STARTING_SIZE);	
	}

    /**
     * Do Rainbow Dash Crouch animation
     */
    public void crouch() {
        changeAnimation("crouch", maxFrames[3], getTextureAtlas(3));
    }

    /**
     * Change Rainbow so move animation is flying
     */
    public void changeToFlying() {
        setMoveAtlas(getTextureAtlas(1), maxFrames[1]);
    }

    /**
     * I return the flying texture atlas
     * @return
     */
    private TextureAtlas getTextureAtlas(int aFilePathIndex) {
        return (TextureAtlas) getAsset(filePaths[aFilePathIndex], "TextureAtlas");
    }

}
