package com.delmesoft.editor2d.graphics.polygon;

import com.delmesoft.editor2d.editor.Layer;
import com.delmesoft.editor2d.graphics.RenderContext;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer.ShapeType;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public class Polygon {

	public static final float VETEX_COLOR = new Color(1F, 1F, 0F, 1F).toFloatBits();
	public static final float SELECTION_COLOR = new Color(1F, 0,5F, 0F).toFloatBits();
	public static final float EDGE_COLOR  = new Color(0.44F, 0.77F, 0.96F, 1F).toFloatBits();	
	public static final float FACE_COLOR  = new Color(0.44F, 0.77F, 0.96F, 0.5F).toFloatBits();
	
	static EarClippingTriangulator triangulator = new EarClippingTriangulator();
	static FloatArray triangles = new FloatArray();
	
	public Layer parent;
	
	boolean closed;

	Vertex[] vertices;
	int vertexCount;

	public Polygon () {
		
		vertices = new Vertex[3];

	}
	
	public Polygon(float[] vertices, Layer parent) {		
		
		vertexCount = vertices.length >> 1;
		
		this.vertices = new Vertex[vertexCount];
		
		for(int i = 0; i < vertices.length; i+= 2) {
			this.vertices[i >> 1] = new Vertex(vertices[i], vertices[i + 1], this); 
		}
		
		this.closed = vertexCount > 2;
		
		this.parent = parent;
	}
	
	public Polygon(Polygon polygon, Layer parent) {

		this.vertexCount = polygon.vertexCount;

		this.vertices = new Vertex[vertexCount];

		for (int i = 0; i < vertexCount; i++) {
			vertices[i] = polygon.vertices[i].copy(this);
		}

		this.closed = polygon.closed;

		this.parent = parent;

	}

	public Vertex insertVertex(int index, float x, float y) {
		
		if(vertexCount == vertices.length) {
			Vertex[] tmp = new Vertex[(int) (vertexCount * 1.75F)];
			System.arraycopy(vertices, 0, tmp, 0, vertexCount);
			vertices = tmp;
		}
				
		if(index == vertexCount) {

			return vertices[vertexCount++] = new Vertex(x, y, this);	
			
		} else {
			
			System.arraycopy(vertices, index, vertices, index + 1, vertexCount - index);
			
			vertexCount++;
			return vertices[index] =  new Vertex(x, y, this);
		}
		
	}
	
	public Vertex addVertex(float x, float y) {
		
		if(vertexCount > 0 && vertices[0].hit(x, y)) { // Close
			closed = true;
			return vertices[0];
		}
		
		if(vertexCount == vertices.length) {
			Vertex[] tmp = new Vertex[(int) (vertexCount * 1.75F)];
			System.arraycopy(vertices, 0, tmp, 0, vertexCount);
			vertices = tmp;
		}

		return (vertices[vertexCount++] = new Vertex(x, y, this));		
	}
	
	public boolean isClosed() {
		return closed;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public Vertex getVertex(int index) {
		return vertices[index];
	}
	
	public int indexOf(Vertex vertex) {

		for(int i = 0; i < vertexCount; i++) {
			if(vertices[i] == vertex) {
				return i;
			}
		}
		
		return -1;
	}
	
	public boolean removeVertex(Vertex vertex) {
				
		for(int i = 0; i < vertexCount; i++) {
			
			if(vertices[i] == vertex) {
				removeVertex(i);
				return true;
			}
		}
		
		return false;
	}

	public Vertex removeVertex(int index) {

		Vertex v = vertices[index];
		vertexCount--;
		System.arraycopy(vertices, index + 1, vertices, index, vertexCount - index);
		vertices[vertexCount] = null;

		if(vertexCount < 3) closed = false;

		return v;
	}

	public void removeSelecteVertices() {

		for(int i = 0; i < vertexCount; i++) {

			if(vertices[i].selected) {
				removeVertex(i);
				if( i < 3) {
					closed = false;
				}
			}

		}

	}

	public Vertex getVertexAt(float x, float y) {

		final Vertex[] vertices = this.vertices;

		for(int i = 0, n = vertexCount; i < n; i++) {
			if(vertices[i].hit(x, y)) {
				return vertices[i];
			}
		}

		return null;
	}
	
	public void render(ShapeRenderer shapeRenderer, RenderContext renderContext) {

		if(vertexCount > 0) {

			Vertex v1, v0;

			int i, n;
			
			shapeRenderer.setShapeType(ShapeType.Line);
			
			if(vertexCount > 1) {

				shapeRenderer.setColor(EDGE_COLOR);				

				v0 = vertices[0];

				n = vertexCount;
				i = 1; 

				for(; i < n; i++) {

					v1 = vertices[i];

					shapeRenderer.line(v0.x, v0.y, v1.x, v1.y);

					v0 = v1;

				}

				if(closed) {
					
					v1 = vertices[0];

					shapeRenderer.line(v0.x, v0.y, v1.x, v1.y);

				}
				
			}
			
			float color = VETEX_COLOR;

			shapeRenderer.setColor(color);

			i = 0;
			n = vertexCount;

			for(; i < n; i++) {

				v0 = vertices[i];

				triangles.add(v0.x);
				triangles.add(v0.y);
				
				if(v0.selected && color == VETEX_COLOR) {
					color = SELECTION_COLOR;
					shapeRenderer.setColor(color);
				} else if(!v0.selected) {
					color = VETEX_COLOR;
					shapeRenderer.setColor(color);
				}

				v0.render(shapeRenderer);

			}

			if(closed && vertexCount > 2) {

				shapeRenderer.setColor(FACE_COLOR);
				shapeRenderer.setShapeType(ShapeType.Filled);

				renderContext.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

				final ShortArray indices = triangulator.computeTriangles(triangles.items, 0, triangles.size);					

				n = indices.size;

				for(i = 0; i < n; i += 3) {

					int i0 = indices.get(i)     << 1;
					int i1 = indices.get(i + 1) << 1;
					int i2 = indices.get(i + 2) << 1;				

					shapeRenderer.triangle(triangles.get(i2), triangles.get(i2 + 1),
							triangles.get(i1), triangles.get(i1 + 1),
							triangles.get(i0), triangles.get(i0 + 1));

				}

				shapeRenderer.flush();

				renderContext.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			}

			triangles.clear();
		}

	}
	
	public Polygon copy(Layer parent) {	
		
		return new Polygon(this, parent);
		
	}

}
