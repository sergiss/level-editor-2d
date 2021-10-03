package com.delmesoft.editor2d.editor.tools;

import javax.swing.DefaultListModel;

import com.delmesoft.editor2d.editor.Editor;
import com.delmesoft.editor2d.editor.Layer;
import com.delmesoft.editor2d.graphics.RenderContext;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer.ShapeType;
import com.delmesoft.editor2d.graphics.polygon.Polygon;
import com.delmesoft.editor2d.graphics.polygon.Vertex;
import com.delmesoft.editor2d.ui.LevelEditor;

public class AddVertexTool implements Tool {

	private Polygon currentPolygon;
	
	@Override
	public void render(Editor editor, ShapeRenderer shapeRenderer, RenderContext renderContext) {
				
		if(currentPolygon != null) {
			
			shapeRenderer.begin(renderContext);
						
			currentPolygon.render(shapeRenderer, renderContext);
			
			shapeRenderer.setShapeType(ShapeType.Line);
						
			shapeRenderer.setColor(Polygon.EDGE_COLOR);
			
			Vertex lastVertex = currentPolygon.getVertex(currentPolygon.getVertexCount() - 1);
			
			shapeRenderer.line(lastVertex.x, lastVertex.y, editor.worldX, editor.worldY);
						
			shapeRenderer.end();
			
		}
		
	}

	@Override
	public void touchDragged(Editor editor) {}

	@Override
	public void touchDown(Editor editor) {
		
		if(editor.mouseButton3) {
			clear();
			return;
		}
		
		final LevelEditor levelEditor = LevelEditor.getInstance();

		DefaultListModel<Layer> model = levelEditor.layerPanel.model;

		if(model.size() > 0) {

			if(currentPolygon == null) {

				currentPolygon = new Polygon();

			}
			
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

			currentPolygon.addVertex(worldX, worldY);

			if(currentPolygon.isClosed()) {
				
				levelEditor.undoRedo.nextChange();
				
				levelEditor.undoRedo.mem(levelEditor.layerPanel);
				levelEditor.updateUndoRedoButtons();
				
				Layer layer = model.getElementAt(levelEditor.layerPanel.list.getSelectedIndex());

				layer.polygons.add(currentPolygon);
				
				currentPolygon.parent = layer;

				currentPolygon = null;

			}

		}
		
	}

	@Override
	public void touchUp(Editor editor) {}
	
	@Override
	public void clear() {

		currentPolygon = null;

	}
	
}
