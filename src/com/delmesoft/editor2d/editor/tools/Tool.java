package com.delmesoft.editor2d.editor.tools;

import com.delmesoft.editor2d.editor.Editor;
import com.delmesoft.editor2d.graphics.RenderContext;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer;

public interface Tool {
	
	public static Tool BRUSH_TOOL   = new BrushTool();
	public static Tool BUCKET_TOOL  = new BucketTool();
	public static Tool ERASER_TOOL  = new EraserTool();
	public static Tool DROOPER_TOOL = new DropperTool();
	
	public static Tool ADD_VERTEX_TOOL  = new AddVertexTool();
	public static Tool ADD_CIRCLE_TOOL  = new AddCircleTool();
	public static Tool EDIT_VERTEX_TOOL = new EditVertexTool();
		
	void render(Editor editor, ShapeRenderer shapeRenderer, RenderContext renderContext);
		
	void clear();

	void touchDragged(Editor editor);

	void touchDown(Editor editor);

	void touchUp(Editor editor);

}
