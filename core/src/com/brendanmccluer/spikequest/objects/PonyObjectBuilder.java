/** Not using this currently
package com.brendanmccluer.spikequest.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.Gdx;

public class PonyObjectBuilder {
	private static List<String> filePaths;
	private static List<String> frameLengths;
	private static List<String> soundList;
	private static float size = 0f;
	private static Element doc = null;
	private static NodeList aNodeList = null;
	private static Document dom = null;
	private static DocumentBuilderFactory dbf = null;
	private static Exception xmlException = null;
	
	/**
	 * read settings from pony object
	 * see SpikeQuest.xml and http://stackoverflow.com/questions/7373567/java-how-to-read-and-write-xml-files/7373596#7373596
	 * @param aPonyName
	 
	public static PonyObject buildPonyObject(String aPonyName) {
		
		dbf = DocumentBuilderFactory.newInstance();;
		filePaths = new ArrayList<String>();
		frameLengths = new ArrayList<String>();

		
		try {
            
			// use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the    
            // XML file
            dom = db.parse(Gdx.files.internal("xml/Ponies.xml").read());
            
            doc = dom.getDocumentElement();
            
            if (doc == null) {
            	xmlException = new Exception("Could not find settings for Pony " + aPonyName);
            	throw xmlException;
            }
            
            //Grab size if it exists (optional field)
            if (doc.getElementsByTagName("Size").getLength() > 0) {
            	System.out.println(doc.getElementsByTagName("Size").item(0).getTextContent());}
            
            //Grab Still Animation
            getAnimationProperties("Still", 0);
            
            //Grab Move Animation if it exists
            getAnimationProperties("Move", 1);
            
            //Grab Talk Animation if it exists
            getAnimationProperties("Talk", 2);
           
            
            //Grab SoundEffects if they exist.
            //Sound effects are in a specific order. See Ponies.xml
            //for (int i=0; i < doc.getElementsByTagName("SoundEffects").getLength();i++) {
            	//soundList.add(doc.getElementsByTagName("SoundEffects").item(i));
            //}
            
	    }
		catch (Exception e) {
			System.out.println(e.toString());
		} 
		
		
		return buildObject();
		
		}
		
	private static PonyObject buildObject() {
		//Build arrays
		String[] filePathStrings = (String)filePaths.toArray();
		int[] frames = new int[frameLengths.size()];
		String[] types = new String[filePaths.size()];
		
		//done with these. Discard
		filePaths = null;
		frameLengths = null;

		Arrays.fill(types, "TextureAtlas");
		
		//Build the object
		return new PonyObject (filePathStrings, types, frames, size);
	}
	
	/**
	 * I set the TextureAtlas file path and Frames for an animation if it exists 
	 * an animation element
	 * @param anAnimationName
	 
	private static void getAnimationProperties (String anAnimationName, int anIndex) {
		NodeList textureAtlasElements = doc.getElementsByTagName("TextureAtlas");
		NodeList FrameElements = doc.getElementsByTagName("Frames");
		
		if (textureAtlasElements.item(anIndex) != null && FrameElements.item(anIndex) != null &&
				textureAtlasElements.item(anIndex).getParentNode().getNodeName().equals(anAnimationName)) {
			System.out.println(textureAtlasElements.item(anIndex).getTextContent());
			//TextureAtlas node
			filePaths.add(textureAtlasElements.item(anIndex).getTextContent());
			//Frame node
			frameLengths.add(textureAtlasElements.item(anIndex).getTextContent());
		}
			
	}
		
		
}	
*/