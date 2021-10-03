package com.delmesoft.editor2d.ui;

public class Settings {
		
	public static final Settings DEFAULT_SETTINGS = new Settings();
	
	static {
		DEFAULT_SETTINGS.mapWidth  = 10;
		DEFAULT_SETTINGS.mapHeight = 10;
	}
	
	// Tileset
	
	public String texturePath;
	
	public int tileWidth, tileHeight;
	
	public int margin, spacing;
	
	// Map size
	
	public int mapWidth, mapHeight;
	
	public String toString() {
		
		String s = texturePath + "\n" +
		tileWidth + ", " + tileHeight + "\n" +
		margin + ", " + spacing + "\n" + 
		mapWidth + ", " + mapHeight;
		
		return s;
	}
	
}
