package com.delmesoft.editor2d.graphics;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.utils.BufferUtils;

public class TextureBinder {

	/** GLES only supports up to 32 textures */
	public final static int MAX_GLES_UNITS = 32;

	private final GLTexture[] textures;

	private final int[] weights;

	private final int count;

	public TextureBinder() {

		count = Math.min(getMaxTextureUnits(), MAX_GLES_UNITS);

		textures = new GLTexture[count];
		weights = new int[count];
	}

	public void begin() {

		for (int i = 0; i < count; i++) {
			textures[i] = null;
			weights[i] = 0;
		}

	}

	public int bind(GLTexture texture) {

		int mIndex = 0;

		for (int i = 0; i < count; i++) {

			if (texture == textures[i]) {
				weights[i]++;
				return i;
			}

			if (weights[mIndex] > --weights[i]) {
				mIndex = i;
			}

		}

		texture.bind(mIndex);

		textures[mIndex] = texture;
		weights[mIndex]  = 100;

		return mIndex;
	}

	public void end() {
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
	}

	private static int getMaxTextureUnits() {
		IntBuffer buffer = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
		return buffer.get(0);
	}

}
