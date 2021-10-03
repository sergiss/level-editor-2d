package com.delmesoft.editor2d.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;

public class TextureRegion extends TextureCoordinate {
	
	public Texture texture;
	
	public int width, height;
	
	public TextureRegion() {}
	
	public TextureRegion(Texture texture, float u1, float v1, float u2, float v2) {
		
		this.texture = texture;
		
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
		
		width  = Math.round((u2 - u1) * texture.getWidth());
		height = Math.round((v2 - v1) * texture.getHeight());
				
	}
	
	public TextureRegion(Texture texture, int x, int y, int width, int height) {
		
		this.texture = texture;
		
		float invTexWidth  = 1f / texture.getWidth();
		float invTexHeight = 1f / texture.getHeight();
		
		this.u1 = x * invTexWidth;
		this.u2 = (x + width ) * invTexWidth;
		this.v1 = y * invTexHeight;
		this.v2 = (y + height) * invTexHeight;
		
		this.width  = width;
		this.height = height;
		
	}
	
	public int getRegionX() {
		return Math.round(u1 * texture.getWidth());
	}

	public int getRegionY() {
		return Math.round(v1 * texture.getHeight());
	}

}
