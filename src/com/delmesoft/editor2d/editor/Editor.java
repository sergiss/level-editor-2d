package com.delmesoft.editor2d.editor;

import javax.swing.DefaultListModel;

import com.delmesoft.editor2d.editor.tools.Tool;
import com.delmesoft.editor2d.graphics.RenderContext;
import com.delmesoft.editor2d.graphics.g2d.Font;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer;
import com.delmesoft.editor2d.graphics.g2d.SpriteRenderer;
import com.delmesoft.editor2d.graphics.g2d.TextureCoordinate;
import com.delmesoft.editor2d.graphics.g2d.TiledTexture;
import com.delmesoft.editor2d.graphics.g2d.ShapeRenderer.ShapeType;
import com.delmesoft.editor2d.graphics.g2d.shader.ShaderProvider;
import com.delmesoft.editor2d.graphics.g2d.shader.ShaderProvider.ShaderType;
import com.delmesoft.editor2d.graphics.polygon.Polygon;
import com.delmesoft.editor2d.math.MathHelper;
import com.delmesoft.editor2d.ui.LevelEditor;
import com.delmesoft.editor2d.ui.Settings;
import com.delmesoft.editor2d.utils.datastructure.Array;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class Editor extends InputAdapter implements ApplicationListener {
		
	public static final int SIZE   = 16;
	public static final float GAP  = 2F;

	private static Editor instance;

	public static Editor getInstance() {

		if (instance == null) {
			instance = new Editor();
		}

		return instance;
	}	

	// EDITABLE VARS ************************
	
	private final Vector3 mousePosition;	
	public boolean ctrlKey, undoRedoKeys, mouseButton1, mouseButton2, mouseButton3;
			
	private TiledTexture tiledTexture;
	
	public float worldX, worldY;
	public int screenX, screenY;
	
	public int mapWidth, mapHeight;		
	public int currentTile = -1;
	
	public Tool tool = Tool.BRUSH_TOOL;
	
	// RENDER ******************************
	
	private RenderContext renderContext;
	private SpriteRenderer spriteRenderer;
	private ShapeRenderer shapeRenderer;
	
	private OrthographicCamera camera;
	
	public boolean showGrid = true;
	public boolean showShapes = true;
	
	private boolean debug = false;
		
	// DEBUG ******************************	
	
	private Texture textureFont;
	private Font font;
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
		
	private Editor() {		
		mousePosition = new Vector3();		
	}

	@Override
	public void create() {
		
		Gdx.input.setInputProcessor(this);
		
		renderContext = new RenderContext();
		
		textureFont = new Texture(Gdx.files.internal("textures/font.png"), true);
		
		font = new Font(textureFont, 6, 8);
		font.scale = 1.75F;
				
		spriteRenderer = new SpriteRenderer();
		spriteRenderer.setShader(ShaderProvider.getShader(ShaderType.DEFAULT));
		
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setShader(ShaderProvider.getShader(ShaderType.SHAPE_SHADER));
		
		camera = new OrthographicCamera();		
						
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				
		initialize(Settings.DEFAULT_SETTINGS);
		
	}
	
	public void initialize(Settings settings) {
						
		mapWidth  = settings.mapWidth;
		mapHeight = settings.mapHeight;

		camera.position.set(mapWidth  * SIZE * 0.5F, 
				            mapHeight * SIZE * 0.5F, 0);
		
		if(mapWidth > mapHeight) {
			camera.zoom = (mapHeight * SIZE) / camera.viewportHeight;			
		} else {
			camera.zoom = (mapWidth * SIZE) / camera.viewportWidth;
		}
		
		if(settings.texturePath != null) {
			
			if(tiledTexture != null) tiledTexture.getTexture().dispose();
			
			tiledTexture = new TiledTexture(new Texture(settings.texturePath), 
					                        settings.tileWidth, settings.tileHeight, 
					                        settings.margin, settings.spacing);
			
		}
		
	}

	@Override
	public void render() {
		
		float speed = 100 * Gdx.graphics.getDeltaTime();
		
		if(up) {
			camera.position.y += speed;
		}
		
		if(down) {
			camera.position.y -= speed;
		}
		
		if(left) {
			camera.position.x -= speed;
		}
		
		if(right) {
			camera.position.x += speed;
		}
						
		camera.update(true);
		
		// RENDER *************************************************
		
		final Graphics graphics = Gdx.graphics;

		graphics.getGL20().glClearColor(0.65F, 0.65F, 0.65F, 1F);
		graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderContext.begin();
		
		final float size = SIZE;
		
		final float width  = size * mapWidth;
		final float height = size * mapHeight;
		
		float scl = camera.viewportWidth * 0.5F * camera.zoom;
		
		final float minX = Math.max(0F   , camera.position.x - scl);
		final float maxX = Math.min(width, camera.position.x + scl);
		
		scl = camera.viewportHeight * 0.5F * camera.zoom;
		
		float minY = Math.max(0F    , camera.position.y - scl);
		float maxY = Math.min(height, camera.position.y + scl);
		
		{ // Tile canvas background

			shapeRenderer.getProjectionMatrix().set(camera.combined);

			shapeRenderer.setColor(Color.GRAY);

			shapeRenderer.setShapeType(ShapeType.Filled);

			shapeRenderer.begin(renderContext);

			shapeRenderer.rect(minX, minY, maxX, minY, maxX, maxY, minX, maxY);

			shapeRenderer.end();

		}
		
		if(tiledTexture != null) { // Render Tiles
			
			spriteRenderer.getProjectionMatrix().set(camera.combined);

			spriteRenderer.begin(renderContext);

			int index;
			
			final int x1 = (int) (minX / size);
			final int x2 = Math.min((int) (maxX / size) + 1, mapWidth);

			final int y1 = (int) (minY / size);
			final int y2 = Math.min((int) (maxY / size) + 1, mapHeight);
			
			final DefaultListModel<Layer> model = LevelEditor.getInstance().layerPanel.model;

			Layer layer;
			TextureCoordinate tc;
						
			int tileType;
			
			spriteRenderer.setTexture(tiledTexture.getTexture());
						
			for (int i = 0, n = model.getSize(); i < n; i++) {
				
				layer = model.get(i);
				
				float tx1, tx2, ty;

				if (layer.visible) { // Current layer is visible

					for (int x = x1; x < x2; x++) {
						
						index = x * mapHeight;
						
						tx1 = x * size;
						tx2 = tx1 + size;
												
						for (int y = y1; y < y2; y++) { 
							
							tileType = layer.getTileType(index + y);
							
							if (tileType > -1) { // Tile is empty
								
								tc = tiledTexture.getTextureCoordinates()[tileType];							
																	
								ty = y * size;
								
								spriteRenderer.addSprite(tx1, ty, tx2, ty + size, tc.u1, tc.v2, tc.u2, tc.v1);
								
							}					

						}
					}
					
				}

			}
									
			spriteRenderer.end();
			
			// Draw shapes
			if(showShapes) {
				shapeRenderer.begin(renderContext);

				for (int i = 0, n = model.getSize(); i < n; i++) {

					layer = model.get(i);

					if (layer.visible) { // Current layer is visible

						Array<Polygon> polygons = layer.polygons;

						for(int j = 0; j < polygons.size; j++) {
							polygons.get(j).render(shapeRenderer, renderContext);
						}					

					}

				}

				shapeRenderer.end();
			}
		}
		
		tool.render(this, shapeRenderer, renderContext);
				
		if(showGrid) { // Draw grid
			
			shapeRenderer.setColor(Color.BLACK);
			
			shapeRenderer.setShapeType(ShapeType.Line);			
			
			shapeRenderer.begin(renderContext);			
						
			int steps = 0;

			float gap = GAP * camera.zoom;

			float x, y;

			for (x = minX; x <= maxX; x += size - (x % size)) {
				for (y = minY; y < maxY; y += gap) {
					if (steps++ % 2 == 0) {
						shapeRenderer.line(x, y, x, y + gap);
					}
				}
			}

			for (y = minY; y <= maxY; y += size - (y % size)) {
				for (x = minX; x < maxX; x += gap) {
					if (steps++ % 2 == 1) {
						shapeRenderer.line(x, y, x + gap, y);
					}
				}
			}
			
			shapeRenderer.end();	
			
		}
		
		if(debug) {
			
			spriteRenderer.getProjectionMatrix().setToOrtho2D(0, 0, graphics.getWidth(), graphics.getHeight());
			spriteRenderer.begin(renderContext);
			
			font.render("FPS: " + graphics.getFramesPerSecond(), 5, graphics.getHeight() - 20, spriteRenderer);
			font.render(String.format("Heap Size: %.2f MB", MathHelper.byteToMega * Runtime.getRuntime().totalMemory()), 5, graphics.getHeight() - 35, spriteRenderer);
			font.render(String.format("Heap Use: %.2f MB" , MathHelper.byteToMega * (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())), 5, graphics.getHeight() - 50, spriteRenderer);
					
			spriteRenderer.end();
			
		}
		
		renderContext.end();

	}

	@Override
	public void resize(int width, int height) {
		
		camera.viewportWidth  = width;
		camera.viewportHeight = height;
		
	}
		
	@Override
	public boolean keyDown (int keycode) {
		
		switch(keycode) {
		
		case Keys.CONTROL_LEFT:
			ctrlKey = true;	
			break;
		case Keys.ESCAPE:
			if(tool != null) {
				tool.clear();
			}
			break;	
		case Keys.Z:
			if(ctrlKey && undoRedoKeys == false && LevelEditor.getInstance().undoRedo.canUndo()) {
				undoRedoKeys = true;
				LevelEditor.getInstance().undoRedo.undo();
				LevelEditor.getInstance().updateUndoRedoButtons();
			}
			break;
		case Keys.Y:
			if(ctrlKey && undoRedoKeys == false && LevelEditor.getInstance().undoRedo.canRedo()) {
				undoRedoKeys = true;
				LevelEditor.getInstance().undoRedo.redo();
				LevelEditor.getInstance().updateUndoRedoButtons();
				
			}
			break;
		case Keys.LEFT:
			left = true;
			break;
		case Keys.RIGHT:
			right = true;
			break;
		case Keys.UP:
			up = true;
			break;
		case Keys.DOWN:
			down = true;
			break;
			
		}
		
		return false;
	}
	
	@Override
	public boolean keyUp (int keycode) {
		
		switch(keycode) {
		case Keys.CONTROL_LEFT:
			ctrlKey = false;
		case Keys.Z:
		case Keys.Y:
			undoRedoKeys = false;
			break;
		case Keys.D:
			debug = !debug;
			break;
		case Keys.LEFT:
			left = false;
			break;
		case Keys.RIGHT:
			right = false;
			break;
		case Keys.UP:
			up = false;
			break;
		case Keys.DOWN:
			down = false;
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean scrolled (float amountX, float amountY) {
		
		if(ctrlKey) {

			if(amountY > 0 && camera.zoom < 2F) {
				camera.zoom = 1.1F * camera.zoom;
				camera.position.set(camera.position.x * 1.1F - worldX * 0.1F, 
									camera.position.y * 1.1F - worldY * 0.1F, 
									0F);
			} else if(amountY < 0 && camera.zoom > 0.01F) {
				camera.zoom = 0.9F * camera.zoom;
				camera.position.set(camera.position.x * 0.9F + worldX * 0.1F, 
									camera.position.y * 0.9F + worldY * 0.1F, 
									0F);
			}

		}
		
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		
		this.screenX = screenX;
		this.screenY = screenY;

		mousePosition.x = screenX;
		mousePosition.y = screenY;

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		float x =      screenX - w * 0.5F + camera.position.x / camera.zoom;
		float y = h - (screenY + h * 0.5F - camera.position.y / camera.zoom);

		worldX = x * camera.zoom;
		worldY = y * camera.zoom;
		
		if (worldX > 0F && worldY > 0F && worldX < mapWidth * SIZE && worldY < mapHeight * SIZE) {
			
			float size = camera.zoom / SIZE;

			int col = (int) (x * size);
			int row = (int) (y * size);
			
			int oldTile = currentTile;
			
			currentTile = col * mapHeight + row;			
			
			if (oldTile != currentTile) {

				String info = col + ", " + row + " [";

				LevelEditor levelEditor = LevelEditor.getInstance();
				DefaultListModel<Layer> model = levelEditor.layerPanel.model;

				if (model.getSize() > 0) {

					int tileType = model.getElementAt(levelEditor.layerPanel.list.getSelectedIndex()).tileData[currentTile];

					if (tileType > 0) {
						info += (tileType - 1);
					} else {
						info += "empty";
					}

				} else {
					info += "empty";
				}

				levelEditor.setInfoText(info + "]");

			}

		}

		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
				
		if(mouseButton1) { // TOOL			
			tool.touchDragged(this);						
		} else if (mouseButton2) {
			camera.position.x -= (screenX - mousePosition.x) * camera.zoom;
			camera.position.y += (screenY - mousePosition.y) * camera.zoom;
		}
		
		mouseMoved(screenX, screenY);

		return false;
	}
	
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(currentTile > -1) {
			if(button == Buttons.LEFT) {			
				mouseButton1 = true;							
				tool.touchDown(this);							
			} else if (button == Buttons.MIDDLE) {
				mouseButton2 = true;
			} else if(button == Buttons.RIGHT) {			
				mouseButton3 = true;
				tool.touchDown(this);			
			}
		}
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(currentTile > -1) {
			if(button == Buttons.LEFT) {
				mouseButton1 = false;
				tool.touchUp(this);
			} if (button == Buttons.MIDDLE) {
				mouseButton2 = false;
			}else if(button == Buttons.RIGHT) {			
				mouseButton3 = false;			
			}
		}
		return false;
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {		
		textureFont.dispose();
		spriteRenderer.dispose();
		shapeRenderer.dispose();
	}

	public void clear() {	
		if(this.tool != null) {
			this.tool.clear();
		}
		currentTile = -1;		
	}

	public void setTool(Tool tool) {
		if(this.tool != null) {
			this.tool.clear();
		}
		this.tool = tool;
	}

}
