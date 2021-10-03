package com.delmesoft.editor2d.editor;

import com.delmesoft.editor2d.graphics.polygon.Polygon;
import com.delmesoft.editor2d.utils.datastructure.Array;

public class Layer {
	
	public final int[] tileData;
	
	public final Array<Polygon> polygons;
	
	public String name;
	
	public boolean visible;
	
	public Layer(String name, boolean visible, int cols, int rows) {
		
		this.name = name;
		
		this.visible = visible;
		
		tileData = new int[cols * rows];
		
		polygons = new Array<Polygon>();
		
	}
	
	public Layer(Layer layer) {
		
		this.name    = layer.name;
		this.visible = layer.visible;
		
		tileData = new int[layer.tileData.length];
		
		System.arraycopy(layer.tileData, 0, tileData, 0, tileData.length);
		
		polygons = new Array<Polygon>();
		
		for(int i = 0, n = layer.polygons.size; i < n; i++) {
			
			polygons.add(layer.polygons.get(i).copy(this));
			
		}
		
	}
	
	public int getTileType(int index) {
		return tileData[index] - 1;
	}

	public void setTileType(int index, int tileType) {
		tileData[index] = tileType + 1;
	}
	
	public int getDataLength() {
		return tileData.length;
	}

	public Layer copy() {
		return new Layer(this);
	}
	
}
