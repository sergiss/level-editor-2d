package com.delmesoft.editor2d.graphics.g2d;

import com.delmesoft.editor2d.graphics.RenderContext;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class ShapeRenderer implements Disposable {
	
	private static final int VERTEX_SIZE = 3 + 1;
		
	public enum ShapeType {
		
		Point(GL20.GL_POINTS), Line(GL20.GL_LINES), Filled(GL20.GL_TRIANGLES);

		public final int glType;

		ShapeType (int glType) {
			this.glType = glType;
		}
		
	}
	
	private static final Vector2 tmp = new Vector2();
					
	private final Mesh mesh;
	
	private final float[] vertices;
	private int idx;
	
	private ShapeType shapeType;
	
	private final Matrix4 projectionMatrix;
	private final Matrix4 transformMatrix;
	private final Matrix4 combinedMatrix;
	
	private ShaderProgram shaderProgram;
	
	private int u_projTrans;
	
	private float color;
	
	private float thickness;
	
	private boolean drawing;
		
	public ShapeRenderer() {
		this(2048);
	}
		
	public ShapeRenderer(int maxVertices) {
		
		mesh = new Mesh(false, maxVertices, 0, new VertexAttribute[]{new VertexAttribute(Usage.Position,    3, ShaderProgram.POSITION_ATTRIBUTE),
											   						 new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE)   });
		
		vertices = new float[maxVertices * VERTEX_SIZE];
				
		transformMatrix  = new Matrix4();
		projectionMatrix = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		combinedMatrix   = new Matrix4();
		
		color = Color.WHITE.toFloatBits();
		
		shapeType = ShapeType.Line;
		
		thickness = 0.25F;
		
	}
	
	public void setColor(float color) {
		this.color = color;
	}

	public void setColor(Color color) {
		this.color = color.toFloatBits();
	}

	public void setColor(float r, float g, float b, float a) {
		color = Color.toFloatBits(r, g, b, a);
	}

	public float getColor() {
		return color;
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
		
		if(shader != shaderProgram) {
			
			u_projTrans = shader.getUniformLocation("u_projTrans");
			
			if(drawing) {
				
				flush();

				// this.shaderProgram.end();
				
				shader.bind();
				
				shader.setUniformMatrix(u_projTrans, combinedMatrix.set(projectionMatrix).mul(transformMatrix));
								
			}
						
			shaderProgram = shader;
			
		}			
		
	}
	
	public ShaderProgram getShader() {
		return shaderProgram;
	}
	
	public void setShapeType(ShapeType shapeType) {
		
		if(drawing) flush();
		
		this.shapeType = shapeType;
	}
	
	public ShapeType getShapeType() {
		return shapeType;
	}
	
	public void setThickness(float thickness) {
		this.thickness = thickness;
	}
	
	public float getThickness(){
		return thickness;
	}
		
	public void line(float x0, float y0, float x1, float y1) {
		
		final float color = this.color;
		
		final float[] vertices = this.vertices;
		
		if(shapeType == ShapeType.Filled) {
							
			if(idx + 25 > vertices.length){ // + 24 >= vertices.length
				flush();
			}
			
			float width = thickness * 0.5F;
			
			Vector2 t = tmp.set(y1 - y0, x0 - x1).nor();
			
			float tx = t.x * width;
			float ty = t.y * width;
			
			int idx = this.idx;
						
			vertices[idx     ] = x0 - tx;
			vertices[idx + 1 ] = y0 - ty;
			vertices[idx + 2 ] = 0F;			
			vertices[idx + 3 ] = color;
			
			vertices[idx + 4 ] = x0 + tx;
			vertices[idx + 5 ] = y0 + ty;
			vertices[idx + 6 ] = 0F;			
			vertices[idx + 7 ] = color;
			
			vertices[idx + 8 ] = x1 - tx;
			vertices[idx + 9 ] = y1 - ty;
			vertices[idx + 10] = 0F;			
			vertices[idx + 11] = color;
			
			vertices[idx + 12] = x1 + tx;
			vertices[idx + 13] = y1 + ty;
			vertices[idx + 14] = 0F;			
			vertices[idx + 15] = color;
			
			vertices[idx + 16] = x1 - tx;
			vertices[idx + 17] = y1 - ty;
			vertices[idx + 18] = 0F;			
			vertices[idx + 19] = color;
			
			vertices[idx + 20] = x0 + tx;
			vertices[idx + 21] = y0 + ty;
			vertices[idx + 22] = 0F;			
			vertices[idx + 23] = color;
			
			this.idx += 24;			
						
		} else {
			
			if(idx + 9 > vertices.length){ // + 8 >= vertices.length
				flush();
			}
			
			int idx = this.idx;
						
			vertices[idx    ] = x0;
			vertices[idx + 1] = y0;
			vertices[idx + 2] = 0F;			
			vertices[idx + 3] = color;
			
			vertices[idx + 4] = x1;
			vertices[idx + 5] = y1;
			vertices[idx + 6] = 0F;			
			vertices[idx + 7] = color;
			
			this.idx += 8;
						
		}
		
	}
	
	public void triangle(float x0, float y0, float x1, float y1, float x2, float y2) {
		
		final float color = this.color;
		
		final float[] vertices = this.vertices;
		
		if(shapeType == ShapeType.Filled) {
							
			if(idx + 13 > vertices.length){ // + 12 >= vertices.length
				flush();
			}
						
			int idx = this.idx;
						
			vertices[idx     ] = x0;
			vertices[idx + 1 ] = y0;
			vertices[idx + 2 ] = 0F;			
			vertices[idx + 3 ] = color;
			
			vertices[idx + 4 ] = x1;
			vertices[idx + 5 ] = y1;
			vertices[idx + 6 ] = 0F;			
			vertices[idx + 7 ] = color;
			
			vertices[idx + 8 ] = x2;
			vertices[idx + 9 ] = y2;
			vertices[idx + 10] = 0F;			
			vertices[idx + 11] = color;
			
			this.idx += 12;		
						
		} else {
								
			if(idx + 25 > vertices.length){ // + 32 >= vertices.length
				flush();
			}
			
			int idx = this.idx;
						
			vertices[idx     ] = x0;
			vertices[idx + 1 ] = y0;
			vertices[idx + 2 ] = 0F;			
			vertices[idx + 3 ] = color;
			
			vertices[idx + 4 ] = x1;
			vertices[idx + 5 ] = y1;
			vertices[idx + 6 ] = 0F;			
			vertices[idx + 7 ] = color;
			
			vertices[idx + 8 ] = x1;
			vertices[idx + 9 ] = y1;
			vertices[idx + 10] = 0F;			
			vertices[idx + 11] = color;
			
			vertices[idx + 12] = x2;
			vertices[idx + 13] = y2;
			vertices[idx + 14] = 0F;			
			vertices[idx + 15] = color;
			
			vertices[idx + 16] = x2;
			vertices[idx + 17] = y2;
			vertices[idx + 18] = 0F;			
			vertices[idx + 19] = color;
			
			vertices[idx + 20] = x0;
			vertices[idx + 21] = y0;
			vertices[idx + 22] = 0F;			
			vertices[idx + 23] = color;
						
			this.idx += 24;			
						
		}
		
	}
	
	public void rect(float x00, float y00, float x10, float y10, float x11, float y11, float x01, float y01) {
		
		final float color = this.color;
		
		final float[] vertices = this.vertices;
		
		if(shapeType == ShapeType.Filled) {
							
			if(idx + 25 > vertices.length){ // + 24 >= vertices.length
				flush();
			}
						
			int idx = this.idx;
						
			vertices[idx     ] = x00;
			vertices[idx + 1 ] = y00;
			vertices[idx + 2 ] = 0F;			
			vertices[idx + 3 ] = color;
			
			vertices[idx + 4 ] = x10;
			vertices[idx + 5 ] = y10;
			vertices[idx + 6 ] = 0F;			
			vertices[idx + 7 ] = color;
			
			vertices[idx + 8 ] = x11;
			vertices[idx + 9 ] = y11;
			vertices[idx + 10] = 0F;			
			vertices[idx + 11] = color;
			
			vertices[idx + 12] = x11;
			vertices[idx + 13] = y11;
			vertices[idx + 14] = 0F;			
			vertices[idx + 15] = color;
			
			vertices[idx + 16] = x01;
			vertices[idx + 17] = y01;
			vertices[idx + 18] = 0F;			
			vertices[idx + 19] = color;
			
			vertices[idx + 20] = x00;
			vertices[idx + 21] = y00;
			vertices[idx + 22] = 0F;			
			vertices[idx + 23] = color;
			
			this.idx += 24;		
						
		} else {
								
			if(idx + 33 > vertices.length){ // + 32 >= vertices.length
				flush();
			}
			
			int idx = this.idx;
						
			vertices[idx     ] = x00;
			vertices[idx + 1 ] = y00;
			vertices[idx + 2 ] = 0F;			
			vertices[idx + 3 ] = color;
			
			vertices[idx + 4 ] = x10;
			vertices[idx + 5 ] = y10;
			vertices[idx + 6 ] = 0F;			
			vertices[idx + 7 ] = color;
			
			vertices[idx + 8 ] = x10;
			vertices[idx + 9 ] = y10;
			vertices[idx + 10] = 0F;			
			vertices[idx + 11] = color;
			
			vertices[idx + 12] = x11;
			vertices[idx + 13] = y11;
			vertices[idx + 14] = 0F;			
			vertices[idx + 15] = color;
			
			vertices[idx + 16] = x11;
			vertices[idx + 17] = y11;
			vertices[idx + 18] = 0F;			
			vertices[idx + 19] = color;
			
			vertices[idx + 20] = x01;
			vertices[idx + 21] = y01;
			vertices[idx + 22] = 0F;			
			vertices[idx + 23] = color;
			
			vertices[idx + 24] = x01;
			vertices[idx + 25] = y01;
			vertices[idx + 26] = 0F;			
			vertices[idx + 27] = color;
			
			vertices[idx + 28] = x00;
			vertices[idx + 29] = y00;
			vertices[idx + 30] = 0F;			
			vertices[idx + 31] = color;
			
			this.idx += 32;
			
		}
		
	}
	
	public void circle(float x, float y, float radius, int segments) {
		
		final float[] vertices = this.vertices;
		
		float color = this.color;
		
		float step = (float) MathUtils.PI2 / segments;
		
		float x1 = x + radius;
		float y1 = y;

		float x2, y2;
		
		int vCount, idx;

		if(shapeType == ShapeType.Filled) {
			
			vCount = segments * (VERTEX_SIZE * 3);
			
			if(this.idx + vCount >= vertices.length) {
				flush();
			}
			
			idx = this.idx;
									
			for(int i = 1; i < segments; i++, idx += 12) {
				
				x2 = MathUtils.cos(i * step) * radius + x;
				y2 = MathUtils.sin(i * step) * radius + y;
								
				vertices[idx     ] = x1;
				vertices[idx + 1 ] = y1;
				vertices[idx + 2 ] = 0F;			
				vertices[idx + 3 ] = color;
				
				vertices[idx + 4 ] = x2;
				vertices[idx + 5 ] = y2;
				vertices[idx + 6 ] = 0F;			
				vertices[idx + 7 ] = color;				
				
				vertices[idx + 8 ] = x;
				vertices[idx + 9 ] = y;
				vertices[idx + 10] = 0F;			
				vertices[idx + 11] = color;				

				x1 = x2;
				y1 = y2;
				
			}			
			
			vertices[idx     ] = x1;
			vertices[idx + 1 ] = y1;
			vertices[idx + 2 ] = 0F;			
			vertices[idx + 3 ] = color;
			
			vertices[idx + 4 ] = x + radius;
			vertices[idx + 5 ] = y;
			vertices[idx + 6 ] = 0F;			
			vertices[idx + 7 ] = color;
			
			vertices[idx + 8 ] = x;
			vertices[idx + 9 ] = y;
			vertices[idx + 10] = 0F;			
			vertices[idx + 11] = color;
						
			this.idx += vCount;	

		} else {
			
			vCount = segments * (VERTEX_SIZE << 1);
			
			if(this.idx + vCount >= vertices.length) {
				flush();
			}
			
			idx = this.idx;
						
			for(int i = 1; i < segments; i++, idx += 8) {

				x2 = MathUtils.cos(i * step) * radius + x;
				y2 = MathUtils.sin(i * step) * radius + y;
								
				vertices[idx     ] = x1;
				vertices[idx + 1 ] = y1;
				vertices[idx + 2 ] = 0F;			
				vertices[idx + 3 ] = color;
				
				vertices[idx + 4 ] = x2;
				vertices[idx + 5 ] = y2;
				vertices[idx + 6 ] = 0F;			
				vertices[idx + 7 ] = color;

				x1 = x2;
				y1 = y2;
				
			}
						
			vertices[idx     ] = x1;
			vertices[idx + 1 ] = y1;
			vertices[idx + 2 ] = 0F;			
			vertices[idx + 3 ] = color;
			
			vertices[idx + 4 ] = x + radius;
			vertices[idx + 5 ] = y;
			vertices[idx + 6 ] = 0F;			
			vertices[idx + 7 ] = color;
			
			this.idx += vCount;	
			
		}

	}

	public void begin(RenderContext renderContext) {
		
		if(drawing) {
			
			throw new IllegalStateException("Error: Already drawing...");
					
		} else {
						
			renderContext.setDepthMask(false);
			renderContext.setDepthTest(GL20.GL_NONE, 0f, 1f);
			renderContext.setCullFace(GL20.GL_BACK);
			
			shaderProgram.bind();
			
			shaderProgram.setUniformMatrix(u_projTrans, combinedMatrix.set(projectionMatrix).mul(transformMatrix));
			
			drawing = true;
			
		}
		
	}
	
	public void flush() {

		if (idx > 0) {

			mesh.setVertices(vertices, 0, idx);

			mesh.render(shaderProgram, shapeType.glType);

			idx = 0;
		}

	}

	public void end() {

		if (drawing) {

			flush();

			// shaderProgram.end();

			drawing = false;

		} else {

			throw new IllegalStateException("Error: Not drawing...");

		}

	}

	@Override
	public void dispose() {
		mesh.dispose();		
	}
		
}
