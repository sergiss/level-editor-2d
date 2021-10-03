package com.delmesoft.editor2d.graphics.g2d;

import java.nio.FloatBuffer;

import com.delmesoft.editor2d.graphics.RenderContext;
import com.delmesoft.editor2d.utils.datastructure.Array;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.NumberUtils;

public class SpriteCache implements Disposable {
	
	static final int VERTEX_SIZE = 2 + 1 + 2;
	static final int SPRITE_SIZE = VERTEX_SIZE * 4;
	static final int VERTEX_OFFSET = 20 / SPRITE_SIZE * 6;
	
	private static final float[] tmpVertices = new float[20];
		
	private final Mesh mesh;
	
	private final Matrix4 transformMatrix;
	private final Matrix4 projectionMatrix;
	private final Matrix4 combinedMatrix;
	
	private RenderContext renderContext;
	
	private ShaderProgram shaderProgram;
	
	private int u_projTrans;
	private int u_texture;
		
	private final Array<Texture> textures;
	private final IntArray counts;
	
	private final Array<Cache> caches;
	private Cache currentCache;
	
	private float color;
			
	private boolean drawing;
	
	public SpriteCache() {
		this(1000);
	}

	public SpriteCache(int size) {
		
		mesh = new Mesh(true, size * 4, size * 6, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE), 
												  new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
												  new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

		mesh.setAutoBind(false);
		
		int lenght = size * 6;
		
		short[] indices = new short[lenght];
		
		short j = 0;
		
		for(int i = 0; i < lenght; i+= 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = j;				
		}
		
		mesh.setIndices(indices);
		
		transformMatrix  = new Matrix4();
		projectionMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		combinedMatrix   = new Matrix4();
		
		setColor(Color.WHITE);
				
		textures = new Array<Texture>(8);
		counts = new IntArray(8);
		
		caches = new Array<Cache>(8);
		
	}
	
	public float getColor() {
		return color;
	}

	public void setColor (Color color) {
		this.color = color.toFloatBits();
	}

	public void setColor (float r, float g, float b, float a) {
		
		int intBits = (int) (255 * a) << 24 | 
				      (int) (255 * b) << 16 | 
				      (int) (255 * g) << 8  | 
				      (int) (255 * r);
		
		color = NumberUtils.intToFloatColor(intBits);
		
	}

	public void setColor (float color) {
		this.color = color;
	}
	
	public Matrix4 getProjectionMatrix () {
		return projectionMatrix;
	}

	public void setProjectionMatrix (Matrix4 projection) {
		projectionMatrix.set(projection);
	}

	public Matrix4 getTransformMatrix () {
		return transformMatrix;
	}

	public void setTransformMatrix (Matrix4 transform) {
		transformMatrix.set(transform);
	}
	
	public void setShader(ShaderProgram shader) {
		
		if(drawing) throw new IllegalStateException();
		
		if(shader != this.shaderProgram) {
						
			u_projTrans = shader.getUniformLocation("u_projTrans");
			u_texture   = shader.getUniformLocation("u_texture");
					
			this.shaderProgram = shader;
		
		}
		
	}
	
	public ShaderProgram getShader() {
		return shaderProgram;
	}
	
	public void setTexture(Texture texture) {
		
		if (textures.size == 0 || textures.get(textures.size - 1) != texture) {
			textures.add(texture);
			counts.add(0);
		}
		
	}
	
	public void addSprite(float[] vertices) {
		
		final float color = this.color;
		
		final float[] _vertices = SpriteCache.tmpVertices;
		
		_vertices[0]  = vertices[0];
		_vertices[1]  = vertices[1];
		_vertices[2]  = color;
		_vertices[3]  = vertices[2];
		_vertices[4]  = vertices[3];

		_vertices[5]  = vertices[4];
		_vertices[6]  = vertices[5];
		_vertices[7]  = color;
		_vertices[8]  = vertices[6];
		_vertices[9]  = vertices[7];

		_vertices[10] = vertices[8];
		_vertices[11] = vertices[9];
		_vertices[12] = color;
		_vertices[13] = vertices[10];
		_vertices[14] = vertices[11];

		_vertices[15] = vertices[12];
		_vertices[16] = vertices[13];
		_vertices[17] = color;
		_vertices[18] = vertices[14];
		_vertices[19] = vertices[15];
		
		counts.incr(textures.size - 1, VERTEX_OFFSET);
		mesh.getVerticesBuffer().put(_vertices, 0, 20);
		
	}
	
	public void addSprite(float x00, float y00, float x10, float y10, float x11, float y11, float x01, float y01, float u1, float v1, float u2, float v2) {
		
		final float color = this.color;
		
		final float[] vertices = SpriteCache.tmpVertices;
		
		vertices[0]  = x00;
		vertices[1]  = y00;
		vertices[2]  = color;
		vertices[3]  = u1;
		vertices[4]  = v1;
		
		vertices[5]  = x10;
		vertices[6]  = y10;
		vertices[7]  = color;
		vertices[8]  = u2;
		vertices[9]  = v1;
		
		vertices[10] = x11;
		vertices[11] = y11;
		vertices[12] = color;
		vertices[13] = u2;
		vertices[14] = v2;
		
		vertices[15] = x01;
		vertices[16] = y01;
		vertices[17] = color;
		vertices[18] = u1;
		vertices[19] = v2;
		
		counts.incr(textures.size - 1, VERTEX_OFFSET);
		mesh.getVerticesBuffer().put(vertices, 0, 20);
		
	}
	
	public void addSprite(float x0, float y0, float x1, float y1, float u1, float v1, float u2, float v2) {
		
		final float color = this.color;
		
		final float[] vertices = SpriteCache.tmpVertices;
		
		vertices[0]  = x0;
		vertices[1]  = y0;
		vertices[2]  = color;
		vertices[3]  = u1;
		vertices[4]  = v1;
		
		vertices[5]  = x1;
		vertices[6]  = y0;
		vertices[7]  = color;
		vertices[8]  = u2;
		vertices[9]  = v1;
		
		vertices[10] = x1;
		vertices[11] = y1;
		vertices[12] = color;
		vertices[13] = u2;
		vertices[14] = v2;
		
		vertices[15] = x0;
		vertices[16] = y1;
		vertices[17] = color;
		vertices[18] = u1;
		vertices[19] = v2;
		
		counts.incr(textures.size - 1, VERTEX_OFFSET);
		mesh.getVerticesBuffer().put(vertices, 0, 20);
		
	}
		
	public void beginCache() {
		
		if (currentCache != null) throw new IllegalStateException();
		
		currentCache = new Cache(caches.size, mesh.getVerticesBuffer().limit());
		caches.add(currentCache);
		mesh.getVerticesBuffer().compact();
		
	}

	public void beginCache(int cacheId) {
		
		if (currentCache != null) throw new IllegalStateException();
		
		if (cacheId == caches.size - 1) {
			
			Cache oldCache = caches.removeIndex(cacheId);
			mesh.getVerticesBuffer().limit(oldCache.offset);
			beginCache();

		} else {
			
			currentCache = caches.get(cacheId);
			mesh.getVerticesBuffer().position(currentCache.offset);
			
		}
				
	}

	public int endCache() {
		
		if (currentCache == null) throw new IllegalStateException("beginCache must be called before endCache.");
		
		Cache cache = currentCache;
		int cacheCount = mesh.getVerticesBuffer().position() - cache.offset;
		
		if (cache.textures == null) {
			
			// New cache.
			cache.maxCount = cacheCount;
			cache.textureCount = textures.size;
			cache.textures = textures.toArray(Texture.class);
			cache.counts = new int[cache.textureCount];
			for (int i = 0, n = counts.size; i < n; i++)
				cache.counts[i] = counts.get(i);

			mesh.getVerticesBuffer().flip();
			
		} else {
			
			// Redefine existing cache.
			if (cacheCount > cache.maxCount) {
				throw new GdxRuntimeException(
					"If a cache is not the last created, it cannot be redefined with more entries than when it was first created: "
						+ cacheCount + " (" + cache.maxCount + " max)");
			}

			cache.textureCount = textures.size;

			if (cache.textures.length < cache.textureCount) cache.textures = new Texture[cache.textureCount];
			for (int i = 0, n = cache.textureCount; i < n; i++)
				cache.textures[i] = textures.get(i);

			if (cache.counts.length < cache.textureCount) cache.counts = new int[cache.textureCount];
			for (int i = 0, n = cache.textureCount; i < n; i++)
				cache.counts[i] = counts.get(i);

			FloatBuffer vertices = mesh.getVerticesBuffer();
			vertices.position(0);
			Cache lastCache = caches.get(caches.size - 1);
			vertices.limit(lastCache.offset + lastCache.maxCount);
		}

		currentCache = null;
		textures.clear();
		counts.clear();

		return cache.id;
	}
	
	public void begin(RenderContext renderContext) {
		
		if(drawing) throw new IllegalStateException();
		
		this.renderContext = renderContext;
		
		renderContext.setDepthMask(false);
		renderContext.setDepthTest(GL20.GL_NONE, 0f, 1f);
		renderContext.setCullFace(GL20.GL_BACK);
		
		shaderProgram.bind();
				
		shaderProgram.setUniformMatrix(u_projTrans, combinedMatrix.set(projectionMatrix).mul(transformMatrix));	
		
		mesh.bind(shaderProgram);
		
		drawing = true;
		
	}

	public void draw(int cacheId) {
		
		if (!drawing) throw new IllegalStateException();

		Cache cache = caches.get(cacheId);
		
		int offset = cache.offset / SPRITE_SIZE * 6;
		Texture[] textures = cache.textures;
		int[] counts = cache.counts;
		int textureCount = cache.textureCount;

		for (int i = 0; i < textureCount; i++) {
			
			int count = counts[i];
			shaderProgram.setUniformi(u_texture, renderContext.textureBinder.bind(textures[i]));
			mesh.render(shaderProgram, GL20.GL_TRIANGLES, offset, count);

			offset += count;
			
		}
		
	}

	public void end() {
		
		if(!drawing) throw new IllegalStateException();
		
		drawing = false;
		
		mesh.unbind(shaderProgram);
		
		// shaderProgram.end();
		
	}
		
	public void clear() {
		caches.clear();
		mesh.getVerticesBuffer().clear().flip();
	}

	@Override
	public void dispose() {
		
		mesh.dispose();
				
	}
	
	private static class Cache {
		
		public int maxCount;
		final int id;
		final int offset;
		
		Texture[] textures;
		int textureCount;
		
		int[] counts;
		
		public Cache(int id, int offset) {
			this.id = id;
			this.offset = offset;
		}
								
	}
}
