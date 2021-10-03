
package com.delmesoft.editor2d.editor.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.delmesoft.editor2d.editor.Editor;
import com.delmesoft.editor2d.editor.Layer;
import com.delmesoft.editor2d.graphics.RenderContext;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer.ShapeType;
import com.delmesoft.editor2d.graphics.polygon.Polygon;
import com.delmesoft.editor2d.graphics.polygon.Vertex;
import com.delmesoft.editor2d.ui.LevelEditor;
import com.delmesoft.editor2d.utils.datastructure.Array;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class EditVertexTool implements Tool {
	
	private static float SELECTION_AREA_COLOR = new Color(0.2F, 1F, 0.2F, 0.3F).toFloatBits();
	
	private Array<Vertex> selectedVertices = new Array<>();
	
	private int mode = -1;
	
	Vector2 startPosition = new Vector2();
	Vector2 lastPosition  = new Vector2();
	
	boolean needSave;

	private JPopupMenu popUp;

	private ActionMenu splitAction;

	private ActionMenu deleteAction;
	
	public EditVertexTool() {
		initPopUp();
	}

	@Override
	public void render(Editor editor, ShapeRenderer shapeRenderer, RenderContext renderContext) {
				
		if(mode == 3) {
			
			shapeRenderer.setShapeType(ShapeType.Filled);
			shapeRenderer.setColor(SELECTION_AREA_COLOR);

			renderContext.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			renderSelection(editor, shapeRenderer, renderContext);

			renderContext.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			
			
			shapeRenderer.setShapeType(ShapeType.Line);
			shapeRenderer.setColor(SELECTION_AREA_COLOR);

			renderSelection(editor, shapeRenderer, renderContext);
			
		}
		
	}

	private void renderSelection(Editor editor, ShapeRenderer shapeRenderer, RenderContext renderContext) {
		
		shapeRenderer.begin(renderContext);

		renderContext.setCullFace(GL20.GL_NONE);

		shapeRenderer.rect(startPosition.x, startPosition.y,
						   editor.worldX  , startPosition.y,
						   editor.worldX  , editor.worldY,
						   startPosition.x, editor.worldY);

		shapeRenderer.end();
		
	}

	@Override
	public void touchDragged(Editor editor) {

		if(mode == -1) return;
		
		float worldX = editor.worldX;
		float worldY = editor.worldY;
		
		if(mode == 0) { // Drag mode

			if(needSave) {

				final LevelEditor levelEditor = LevelEditor.getInstance();

				levelEditor.undoRedo.nextChange();

				levelEditor.undoRedo.mem(levelEditor.layerPanel);
				levelEditor.updateUndoRedoButtons();

			}
						
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
						
			if(selectedVertices.size == 1) {
										
				Vertex vertex = selectedVertices.get(0);
				
				vertex.x = worldX;
				vertex.y = worldY;
				
			} else {
				
				lastPosition.sub(worldX, worldY);
				
				for(Vertex vertex : selectedVertices) {
					vertex.x -= lastPosition.x;
					vertex.y -= lastPosition.y;
				}
				
			}

		} else if(mode == 1){
			mode = 3; // Enable selection
		}

		lastPosition.set(worldX, worldY);
		needSave = false;

	}

	@Override
	public void touchDown(Editor editor) {
		
		if(editor.mouseButton3 && selectedVertices.size > 0) {
			
			JPanel renderPanel = LevelEditor.getInstance().renderPanel;
			
			popUp.setLocation((int) (renderPanel.getLocationOnScreen().x + editor.screenX ), (int) (renderPanel.getLocationOnScreen().y + editor.screenY));
			
			splitAction.setEnabled(selectedVertices.size > 1);
			deleteAction.setEnabled(true);
			deleteAction.putValue(Action.NAME, "Delete " + selectedVertices.size + " vertices");
						
			popUp.setVisible(true);
			return;
			
		}
		
		startPosition.set(editor.worldX, editor.worldY);
		lastPosition.set(startPosition);
			
		LevelEditor levelEditor = LevelEditor.getInstance();
		DefaultListModel<Layer> model = levelEditor.layerPanel.model;

		int size = model.getSize();
		
		if (size > 0) {
			
			if(mode == 0 && hitSelection(editor)) {
				return;
			}
			
			if(!editor.ctrlKey) {
				clear();
			}
			
			Vertex vertex;
			
			Layer layer;
							
			for (int i = size - 1; i >= 0; i--) {

				layer = model.get(i);
				
				Polygon polygon;

				for (int j = 0; j < layer.polygons.size; j++) {

					polygon = layer.polygons.get(j);

					vertex = polygon.getVertexAt(editor.worldX, editor.worldY);

					if (vertex != null) {

						if (!vertex.selected) {
							selectedVertices.add(vertex);
							vertex.selected = true;
						}

						mode = 0; // Transform mode

						needSave = true;

						return;

					}

				}
				
			}
			
			mode = 1; // Selection mode
			
		} else {
						
			clear();
			return;
			
		}
		
	}
	
	private boolean hitSelection(Editor editor) {
		for(Vertex vertex : selectedVertices) {
			if(vertex.hit(editor.worldX, editor.worldY)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void touchUp(Editor editor) {
		
		if(mode == -1) return;
		
		if(mode == 3) { // Check selection mode

			if(!editor.ctrlKey) {
				clear();
			}
			
			boolean selection = selectedVertices.size > 0;

			Rectangle rectangle = new Rectangle(Math.min(startPosition.x,  lastPosition.x), Math.min(startPosition.y,  lastPosition.y),
												Math.abs(startPosition.x - lastPosition.x), Math.abs(startPosition.y - lastPosition.y));

			LevelEditor levelEditor = LevelEditor.getInstance();
			DefaultListModel<Layer> model = levelEditor.layerPanel.model;
			
			Polygon polygon;
			Layer layer;
			
			int size = model.getSize();

			for(int i = 0; i < size; i++) {
				layer = model.get(i);
				
				for(int j = 0, n = layer.polygons.size; j < n; j++) {

					polygon = layer.polygons.get(j);

					Vertex vertex;

					for(int vi = 0; vi < polygon.getVertexCount(); vi++) {

						vertex = polygon.getVertex(vi);

						if(!vertex.selected && rectangle.contains(vertex.x, vertex.y)) {							

							vertex.selected = true;
							selectedVertices.add(vertex);
							
							selection = true;

						}

					}

				}

			}
			
			if(selection) {
				mode = 0;
				needSave = true;
			} else {
				needSave = false;
				mode = -1;
			}

		}
	}

	@Override
	public void clear() {
		
		for(Vertex vertex : selectedVertices) {
			vertex.selected = false;
		}
		
		selectedVertices.clear();
		
		mode = -1;
		needSave = false;
		
		popUp.setVisible(false);
		
	}
	
	private void initPopUp() {
		
		popUp = new JPopupMenu();
				
		popUp.add(splitAction = new ActionMenu("Split segments"){

			private static final long serialVersionUID = 1L;
						
			@Override
			public void actionPerformed(ActionEvent e) {
				
				final LevelEditor levelEditor = LevelEditor.getInstance();
				
				boolean saved = false;
				
				final Array<Vertex> selectedVertices = EditVertexTool.this.selectedVertices;
				
				Vertex v, va, vb;
				Polygon parent;
				int indexA, indexB;
				
				for(int i = 0, n = selectedVertices.size; i < n; i++) {
					
					va = selectedVertices.get(i);
										
					parent = va.parent;
					indexA = parent.indexOf(va);
					indexB = (indexA + 1) % parent.getVertexCount();
					vb = parent.getVertex(indexB);
					if(vb.selected) {
						
						if(!saved) {
							levelEditor.undoRedo.nextChange();
							levelEditor.undoRedo.mem(levelEditor.layerPanel);
							levelEditor.updateUndoRedoButtons();
							saved = true;
						}
						
						v = parent.insertVertex(indexB, (va.x + vb.x) * 0.5F, (va.y + vb.y) * 0.5F);
						v.selected = true;
						selectedVertices.add(v);
						
					}
					
				}				
				
				popUp.setVisible(false);
			}
			
		});
		
		popUp.add(deleteAction = new ActionMenu(""){

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(selectedVertices.size > 0) {
					
					final LevelEditor levelEditor = LevelEditor.getInstance();

					levelEditor.undoRedo.nextChange();

					levelEditor.undoRedo.mem(levelEditor.layerPanel);
					levelEditor.updateUndoRedoButtons();
					
					for(Vertex vertex : selectedVertices) {
						
						if(vertex.parent.isClosed()) {
							
							vertex.parent.removeVertex(vertex);
							
							if(!vertex.parent.isClosed()) {
								vertex.parent.parent.polygons.removeValue(vertex.parent);
							}
							
						}
					}
										
					selectedVertices.clear();
					
				}
				
				popUp.setVisible(false);
				
			}
			
		});	

	}
	
	private abstract class ActionMenu extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ActionMenu(String text) {
			putValue(Action.NAME, text);
		}
		
	}
	
}
