package com.brendanmccluer.spikequest.interfaces;

import com.badlogic.gdx.Screen;

public interface StandardSpikeQuestScreenInterface extends Screen {
	
	public boolean load();
	
	public void initialize();
	
	@Override
	public void render(float delta);
	
	public void draw();
	
	@Override
	public void dispose();
	
	
}
