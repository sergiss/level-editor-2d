package com.delmesoft.editor2d.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;

public class TiledTexture {
	
	protected Texture texture;
	
	protected TextureCoordinate[] textureCoordinates;
	
	protected int tileWidth, tileHeight;
	
	protected TiledTexture() {};
		
	public TiledTexture(Texture texture, int tileWidth, int tileHeight) {
		this(texture, 0, 0, texture.getWidth(), texture.getHeight(), tileWidth, tileHeight);		
	}
	
	public TiledTexture(TextureRegion textureRegion, int tileWidth, int tileHeight) {
		this(textureRegion.texture, textureRegion.getRegionX(), textureRegion.getRegionY(), textureRegion.width, textureRegion.height, tileWidth, tileHeight);
	}
	
	public TiledTexture(Texture texture, int regionX, int regionY, int regionWidth, int regionHeight, int tileWidth, int tileHeight) {
						
		final float invWidth  = 1f / texture.getWidth();
		final float invHeight = 1f / texture.getHeight();

		final float tWidth  = tileWidth * invWidth;
		final float tHeight = tileHeight * invHeight;

		final float xOff = regionX * invWidth;

		final int cols = texture.getWidth()  / tileWidth;
		final int rows = texture.getHeight() / tileHeight;

		final TextureCoordinate[] tCoords = new TextureCoordinate[cols * rows];

		int index;

		float u, v = regionY * invHeight;

		for (int j = 0; j < rows; j++) {

			index = j * cols;

			u = xOff;

			for (int i = 0; i < cols; i++) {

				tCoords[index + i] = new TextureCoordinate(u, v, u + tWidth, v + tHeight);

				u += tWidth;
			}

			v += tHeight;

		}

		textureCoordinates = tCoords;

		this.texture = texture;

		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		
	}
		
	public TiledTexture(Texture texture, int tileWidth, int tileHeight, int margin, int spacing) {
		this(texture, 0, 0, texture.getWidth(), texture.getHeight(), tileWidth, tileHeight, margin, spacing);
	}

	public TiledTexture(TextureRegion textureRegion, int tileWidth, int tileHeight, int margin, int spacing) {
		this(textureRegion.texture, textureRegion.getRegionX(), textureRegion.getRegionY(), textureRegion.width, textureRegion.height, tileWidth, tileHeight, margin, spacing);
	}
	
	public TiledTexture(Texture texture, int regionX, int regionY, int regionWidth, int regionHeight, int tileWidth, int tileHeight, int margin, int spacing) {
				
		final float invWidth  = 1f / texture.getWidth();
		final float invHeight = 1f / texture.getHeight();

		final float tWidth  = tileWidth  * invWidth;
		final float tHeight = tileHeight * invHeight;

		final float spacingX = spacing * invWidth;
		final float spacingY = spacing * invHeight;

		final float xOff = regionX * invWidth + margin * invWidth;

		final int cols = (regionWidth  - margin) / (tileWidth + spacing);
		final int rows = (regionHeight - margin) / (tileHeight + spacing);

		final TextureCoordinate[] tCoords = new TextureCoordinate[cols * rows];

		int index;

		float u, v = regionY * invHeight + margin * invHeight;

		for (int j = 0; j < rows; j++) {

			index = j * cols;

			u = xOff;

			for (int i = 0; i < cols; i++) {

				tCoords[index + i] = new TextureCoordinate(u, v, u + tWidth, v + tHeight);

				u += tWidth + spacingX;
			}

			v += tHeight + spacingY;
		}

		textureCoordinates = tCoords;

		this.texture = texture;

		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public TextureCoordinate[] getTextureCoordinates() {
		return textureCoordinates;
	}
	
	public void set(TiledTexture tiledTexture) {
		this.texture = tiledTexture.texture;
		this.textureCoordinates = tiledTexture.textureCoordinates;
		this.tileWidth = tiledTexture.tileWidth;
		this.tileHeight = tiledTexture.tileHeight;
	}

}
