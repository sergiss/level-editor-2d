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

public class BrushTool implements Tool {
	
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
	public void touchDragged(Editor editor) {

		DefaultListModel<Layer> model = LevelEditor.getInstance().layerPanel.model;

		if(model.size() > 0) {

			final LevelEditor levelEditor = LevelEditor.getInstance();

			Layer layer = model.getElementAt(levelEditor.layerPanel.list.getSelectedIndex());

			layer.setTileType(editor.currentTile, levelEditor.tilesetPanel.selectedTile);

		}

	}

	@Override
	public void touchDown(Editor editor) {
		
		if(editor.mouseButton3) return;

		DefaultListModel<Layer> model = LevelEditor.getInstance().layerPanel.model;

		if(model.size() > 0) {

			final LevelEditor levelEditor = LevelEditor.getInstance();

			levelEditor.undoRedo.nextChange();

			levelEditor.undoRedo.mem(levelEditor.layerPanel);
			levelEditor.updateUndoRedoButtons();

			touchDragged(editor);

		}

	}

	@Override
	public void touchUp(Editor editor) {}
	
	@Override
	public void clear() {}

}
