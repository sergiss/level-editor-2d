package com.delmesoft.editor2d.editor.tools;

import javax.swing.DefaultListModel;

import com.delmesoft.editor2d.editor.Editor;
import com.delmesoft.editor2d.editor.Layer;
import com.delmesoft.editor2d.graphics.RenderContext;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer;
import com.delmesoft.editor2d.graphics.polygon.Polygon;
import com.delmesoft.editor2d.ui.LevelEditor;

import com.badlogic.gdx.math.Vector2;

public class AddCircleTool implements Tool {
	
	private static final float MIN_RADIUS = Editor.SIZE * 0.25F;
	
	private Polygon currentPolygon;
	
	private Vector2 startPoint = new Vector2();
	
	private float len = -1f;

	@Override
	public void render(Editor editor, ShapeRenderer shapeRenderer, RenderContext renderContext) {
		
		if(currentPolygon != null) {
			
			shapeRenderer.begin(renderContext);
			
			currentPolygon.render(shapeRenderer, renderContext);
									
			shapeRenderer.end();
			 
		}

	}

	@Override
	public void touchDragged(Editor editor) {
		
		if(len < 0F) return;

		len = startPoint.dst(editor.worldX, editor.worldY);

		if (len > MIN_RADIUS * 0.25F) {
			
			currentPolygon = new Polygon();
			
			currentPolygon.addVertex(len + startPoint.x, startPoint.y);
			
			int n = Math.max(1, (int)(5 * (float)Math.cbrt(len)));
			
			float theta = (float) (Math.PI * 2.0) / n;
			
			for(int i = 1; i < n; i++) {
				
				float tmp = i * theta;
				float cos = (float) Math.cos(tmp);
				float sin = (float) Math.sin(tmp);
				
				currentPolygon.addVertex(cos * len + startPoint.x, sin * len + startPoint.y);
			}
			
			currentPolygon.addVertex(len + startPoint.x, startPoint.y);
			
		} else {
			
			currentPolygon = null;

		}

	}

	@Override
	public void touchDown(Editor editor) {
		
		if(editor.mouseButton3) {
			
			clear();
			
		} else if(editor.mouseButton1) {	
			
			float worldX = editor.worldX;
			float worldY = editor.worldY;

			if (editor.ctrlKey) {

				float mod = worldX % Editor.SIZE;

				if (mod > Editor.SIZE * 0.5F) {
					worldX += Editor.SIZE - mod;
				} else {
					worldX -= mod;
				}
				mod = worldY % Editor.SIZE;
				if (mod > Editor.SIZE * 0.5F) {
					worldY += Editor.SIZE - mod;
				} else {
					worldY -= mod;
				}

			}
			
			startPoint.set(worldX, worldY);		
			len = 0F;
			
		}

	}

	@Override
	public void touchUp(Editor editor) {
		
		if (currentPolygon != null && len > MIN_RADIUS) {
			
			final LevelEditor levelEditor = LevelEditor.getInstance();

			DefaultListModel<Layer> model = levelEditor.layerPanel.model;
			
			levelEditor.undoRedo.nextChange();
			
			levelEditor.undoRedo.mem(levelEditor.layerPanel);
			levelEditor.updateUndoRedoButtons();
			
			Layer layer = model.getElementAt(levelEditor.layerPanel.list.getSelectedIndex());

			layer.polygons.add(currentPolygon);
			
			currentPolygon.parent = layer;
			
		} 
				
		clear();
		
	}
	
	@Override
	public void clear() {		
		currentPolygon = null;
		len = -1F;
	}

}
