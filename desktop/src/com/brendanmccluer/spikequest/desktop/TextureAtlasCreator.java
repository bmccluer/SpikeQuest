package com.brendanmccluer.spikequest.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TextureAtlasCreator {
	
	//For bigger images
	//Settings aSettings = new Settings();
	//aSettings.maxHeight = 2048;
	//TexturePacker.process(aSettings, "C:\\Users\\Brendan\\Documents\\SpikeQuestAssets\\Pinkie\\Walk", "C:\\Users\\Brendan\\Documents\\SpikeQuest\\android\\assets\\pinkie\\walk", "PinkieWalk");
	
	
	public static void main(String[] args) {

		//TexturePacker.process("C:\\Users\\Brendan\\Documents\\SpikeQuestAssets\\Rarity\\Stand", "C:\\Users\\Brendan\\Documents\\SpikeQuest\\android\\assets\\rarity\\stand", "RarityStand");
		//TexturePacker.process("C:\\Users\\brend\\Google Drive\\SpikeQuest\\RainbowRace\\Clouds\\Clouds", "C:\\Users\\brend\\Documents\\SpikeQuest\\android\\assets\\object\\clouds\\white", "CloudStand");
		TexturePacker.process("C:\\Users\\brend\\Google Drive\\SpikeQuest\\RainbowRace\\Tank", "C:\\Users\\brend\\Documents\\SpikeQuest\\android\\assets\\animals\\tank", "TankStand");
	}

}
