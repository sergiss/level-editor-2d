package com.delmesoft.editor2d.editor.tools;

import javax.swing.DefaultListModel;

import com.delmesoft.editor2d.editor.Editor;
import com.delmesoft.editor2d.editor.Layer;
import com.delmesoft.editor2d.graphics.RenderContext;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer.ShapeType;
import com.delmesoft.editor2d.ui.LevelEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class DropperTool implements Tool {

	@Override
	public void render(Editor editor, ShapeRenderer shapeRenderer, RenderContext renderContext) {

		renderContext.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		shapeRenderer.setColor(new Color(0.45F, 0.45F, 0.45F, 0.75F));

		shapeRenderer.setShapeType(ShapeType.Filled);			

		shapeRenderer.begin(renderContext);

		final int size = Editor.SIZE;

		int col = editor.currentTile / editor.mapHeight;
		int row = editor.currentTile % editor.mapHeight;

		shapeRenderer.rect(col * size       , row * size       , 
						   col * size + size, row * size       , 
						   col * size + size, row * size + size, 
						   col * size       , row * size + size);

		shapeRenderer.end();

		renderContext.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

	}

	@Override
	public void clear() {}

	@Override
	public void touchDragged(Editor editor) {
		
		LevelEditor levelEditor = LevelEditor.getInstance();
		DefaultListModel<Layer> model = levelEditor.layerPanel.model;

		int size = model.getSize();
		
		if (size > 0) {
			
			Layer layer;
			
			for(int i = size - 1; i >= 0; i--) {
				
				layer = model.get(i);
				
				int tileType = layer.tileData[editor.currentTile];
				
				if(tileType > 0) {
					
					LevelEditor.getInstance().tilesetPanel.selectedTile = tileType - 1;
					LevelEditor.getInstance().tilesetPanel.repaint();
					return;
					
				}
				
			}		
					
		}
		
	}

	@Override
	public void touchDown(Editor editor) {
		if(editor.mouseButton3) return;
		touchDragged(editor);		
	}

	@Override
	public void touchUp(Editor editor) {}	

}
