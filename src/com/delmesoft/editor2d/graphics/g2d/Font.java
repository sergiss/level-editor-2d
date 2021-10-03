package com.delmesoft.editor2d.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;

public class Font extends TiledTexture {
	
	public float scale = 1f;
	
	public Font(Texture texture, int tileWidth, int tileHeight) {
		super(texture, tileWidth, tileHeight);		
	}
	
	public Font(TextureRegion textureRegion, int tileWidth, int tileHeight) {
		super(textureRegion, tileWidth, tileHeight);		
	}
	
	public void render(String text, int x, int y, SpriteRenderer spriteRenderer) {
		
		spriteRenderer.setTexture(texture);
				
		float width  = getCharWidth();
		float height = getCharHeight();

		TextureCoordinate[] tCoords = textureCoordinates;
		TextureCoordinate tCoord;
		
		float x0;

		for (int i = 0, n = text.length(); i < n; i++) {

			tCoord = tCoords[text.charAt(i)];
			
			x0 = x + i * width;
			
			spriteRenderer.addSprite(x0, y, x0 + width, y + height, tCoord.u1, tCoord.v2, tCoord.u2, tCoord.v1);

		}
		
	}
	
	public float getCharWidth() {
		return tileWidth * scale;
	}

	public float getCharHeight() {
		return tileHeight * scale;
	}

	public Texture getTexture() {
		return texture;
	}

	public TextureCoordinate[] getTextureCoordinates() {
		return textureCoordinates;
	}

}
