package com.delmesoft.editor2d.graphics.g2d;

import com.delmesoft.editor2d.graphics.RenderContext;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.NumberUtils;

public class SpriteRenderer {

	private static final int VERTEX_SIZE = 2 + 1 + 2;
	private static final int SPRITE_SIZE = VERTEX_SIZE * 4;

	private final Mesh mesh;

	private final float[] vertices;
	private int idx;

	private final Matrix4 transformMatrix;
	private final Matrix4 projectionMatrix;
	private final Matrix4 combinedMatrix;
	
	private RenderContext renderContext;

	private ShaderProgram shaderProgram;
	
	private int u_projTrans;
	private int u_texture;

	private Texture lastTexture;

	private float color;
	
	private boolean drawing;
	
	public SpriteRenderer() {
		this(1000);
	}

	public SpriteRenderer(int size) {

		Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;

		if (Gdx.gl30 != null) {
			vertexDataType = Mesh.VertexDataType.VertexBufferObjectWithVAO;
		}

		mesh = new Mesh(vertexDataType, false, size * 4, size * 6, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE), 
																   new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
																   new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));


		vertices = new float[size * SPRITE_SIZE];

		int len = size * 6;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = j;
		}

		mesh.setIndices(indices);
		
		transformMatrix  = new Matrix4();
		projectionMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		combinedMatrix   = new Matrix4();
		
		setColor(Color.WHITE);
				
	}
		
	public float getColor() {
		return color;
	}

	public void setColor (Color tint) {
		color = tint.toFloatBits();
	}

	public void setColor (float r, float g, float b, float a) {
		
		int intBits = (int) (255 * a) << 24 | 
				      (int) (255 * b) << 16 | 
				      (int) (255 * g) << 8 | 
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
		
		if(drawing) {
			flush();
			shaderProgram.setUniformMatrix(u_projTrans, combinedMatrix.set(projection).mul(transformMatrix));
		}
		
		projectionMatrix.set(projection);
	}

	public Matrix4 getTransformMatrix() {
		return transformMatrix;
	}

	public void setTransformMatrix(Matrix4 transform) {
		
		if(drawing) {
			flush();
			shaderProgram.setUniformMatrix(u_projTrans, combinedMatrix.set(projectionMatrix).mul(transform));
		}
		
		transformMatrix.set(transform);
	}
	
	public void setShader(ShaderProgram shader) {

		if(shader != this.shaderProgram) {
			
			u_projTrans = shader.getUniformLocation("u_projTrans");
			u_texture   = shader.getUniformLocation("u_texture");
			
			if(drawing) {
				
				flush();

				//this.shaderProgram.end();
				
				shader.bind();
				
				shader.setUniformMatrix(u_projTrans, combinedMatrix.set(projectionMatrix).mul(transformMatrix));
				
				if(lastTexture != null) {
					
					shader.setUniformi(u_texture, renderContext.textureBinder.bind(lastTexture));
					
				}
								
			}
						
			this.shaderProgram = shader;
			
		}

	}
	
	public ShaderProgram getShader() {
		return shaderProgram;
	}
	
	public void setTexture(Texture texture) {

		if (texture != lastTexture) {

			flush();
			
			lastTexture = texture;
			
			shaderProgram.setUniformi(u_texture, renderContext.textureBinder.bind(lastTexture));

		}

	}
	
	public void addSprite(float[] vertices) {
		
		final float[] _vertices = this.vertices;
		
		if (idx == _vertices.length) {
			flush();
		}		

		float color = this.color;

		int idx = this.idx;
		
		_vertices[idx]      = vertices[0];
		_vertices[idx + 1]  = vertices[1];
		_vertices[idx + 2]  = color;
		_vertices[idx + 3]  = vertices[2];
		_vertices[idx + 4]  = vertices[3];

		_vertices[idx + 5]  = vertices[4];
		_vertices[idx + 6]  = vertices[5];
		_vertices[idx + 7]  = color;
		_vertices[idx + 8]  = vertices[6];
		_vertices[idx + 9]  = vertices[7];

		_vertices[idx + 10] = vertices[8];
		_vertices[idx + 11] = vertices[9];
		_vertices[idx + 12] = color;
		_vertices[idx + 13] = vertices[10];
		_vertices[idx + 14] = vertices[11];

		_vertices[idx + 15] = vertices[12];
		_vertices[idx + 16] = vertices[13];
		_vertices[idx + 17] = color;
		_vertices[idx + 18] = vertices[14];
		_vertices[idx + 19] = vertices[15];
		
		this.idx += 20;
	}
	
	public void addSprite(float x00, float y00, float x10, float y10, float x11, float y11, float x01, float y01, float u1, float v1, float u2, float v2) {
		
		final float[] vertices = this.vertices;
		
		if (idx == vertices.length) {
			flush();
		}		

		float color = this.color;

		int idx = this.idx;

		vertices[idx]      = x00;
		vertices[idx + 1]  = y00;
		vertices[idx + 2]  = color;
		vertices[idx + 3]  = u1;
		vertices[idx + 4]  = v1;

		vertices[idx + 5]  = x10;
		vertices[idx + 6]  = y10;
		vertices[idx + 7]  = color;
		vertices[idx + 8]  = u2;
		vertices[idx + 9]  = v1;

		vertices[idx + 10] = x11;
		vertices[idx + 11] = y11;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u2;
		vertices[idx + 14] = v2;

		vertices[idx + 15] = x01;
		vertices[idx + 16] = y01;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u1;
		vertices[idx + 19] = v2;

		this.idx += 20;
		
	}
	
	public void addSprite(float x0, float y0, float x1, float y1, float u1, float v1, float u2, float v2) {
		
		final float[] vertices = this.vertices;
		
		if (idx == vertices.length) {
			flush();
		}		

		float color = this.color;

		int idx = this.idx;

		vertices[idx]      = x0;
		vertices[idx + 1]  = y0;
		vertices[idx + 2]  = color;
		vertices[idx + 3]  = u1;
		vertices[idx + 4]  = v1;

		vertices[idx + 5]  = x1;
		vertices[idx + 6]  = y0;
		vertices[idx + 7]  = color;
		vertices[idx + 8]  = u2;
		vertices[idx + 9]  = v1;

		vertices[idx + 10] = x1;
		vertices[idx + 11] = y1;
		vertices[idx + 12] = color;
		vertices[idx + 13] = u2;
		vertices[idx + 14] = v2;

		vertices[idx + 15] = x0;
		vertices[idx + 16] = y1;
		vertices[idx + 17] = color;
		vertices[idx + 18] = u1;
		vertices[idx + 19] = v2;

		this.idx += 20;
		
	}

	public void begin(RenderContext renderContext) {
		
		if(drawing)
			throw new IllegalStateException("Error: Already drawing...");
		
		this.renderContext = renderContext;
		
		renderContext.setDepthMask(false);
		renderContext.setDepthTest(GL20.GL_NONE, 0f, 1f);
		renderContext.setCullFace(GL20.GL_BACK);
												
		shaderProgram.bind();
		
		shaderProgram.setUniformMatrix(u_projTrans, combinedMatrix.set(projectionMatrix).mul(transformMatrix));
				
		drawing = true;	
		
	}

	public void flush() {

		if(idx > 0) {
			
			int spritesInBatch = idx / 20;
			int count = spritesInBatch * 6;
															
			Mesh mesh = this.mesh;
			mesh.setVertices(vertices, 0, idx);
			mesh.getIndicesBuffer().position(0).limit(count);
			
			mesh.render(shaderProgram, GL20.GL_TRIANGLES, 0, count, true);
						
			idx = 0;
			
		}

	}
	
	public void end() {
		
		if(!drawing)
			throw new IllegalStateException("Error: Not drawing...");
		
		flush();
		
		drawing = false;
								
		// shaderProgram.end();
				
		lastTexture = null;
		
	}

	public void dispose() {
		mesh.dispose();		
	}

}
