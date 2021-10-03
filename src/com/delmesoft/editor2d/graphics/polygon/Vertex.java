package com.delmesoft.editor2d.graphics.polygon;

import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer;

public class Vertex {
	
	public static float HALF_SIZE = 1F;
		
	public final Polygon parent;
	
	public float x, y;
	
	public boolean selected;
	
	public Vertex(float x, float y, Polygon parent) {
		
		this.x = x;
		this.y = y;
		
		this.parent = parent;
	}
	
	public Vertex(Vertex vertex, Polygon parent) {
		
		x = vertex.x;
		y = vertex.y;
		
		this.parent = parent;
	}

	public boolean hit(float x, float y) {
		return x > this.x - HALF_SIZE && y > this.y - HALF_SIZE && 
			   x < this.x + HALF_SIZE && y < this.y + HALF_SIZE;
	}
	
	public void render(ShapeRenderer shapeRenderer) {
		
		shapeRenderer.rect(x - HALF_SIZE, y - HALF_SIZE, 
				           x + HALF_SIZE, y - HALF_SIZE, 
				           x + HALF_SIZE, y + HALF_SIZE, 
				           x - HALF_SIZE, y + HALF_SIZE);
		
	}

	public Vertex copy(Polygon parent) {
		return new Vertex(this, parent);
	}

}
