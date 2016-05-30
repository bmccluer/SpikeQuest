package com.brendanmccluer.spikequest.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.brendanmccluer.spikequest.SpikeQuestGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.fullscreen = true;
		config.height = 1080;
		config.width = 1920;
		config.resizable = false;
		new LwjglApplication(new SpikeQuestGame(), config);
		
	}
}
