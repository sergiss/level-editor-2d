package com.delmesoft.editor2d.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.delmesoft.editor2d.editor.Editor;
import com.delmesoft.editor2d.editor.tools.Tool;
import com.delmesoft.editor2d.utils.LevelIO;
import com.delmesoft.editor2d.utils.TmxLoader;
import com.delmesoft.editor2d.utils.undoredo.UndoRedo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

public class LevelEditor {
	
	private static LevelEditor instance;

	public static LevelEditor getInstance() {
		if (instance == null) {
			instance = new LevelEditor();
		}
		return instance;
	}
	
	public static boolean splash = true;
	
	public final UndoRedo<LayerPanel> undoRedo;
	
	public final Editor editor;	
	
	public final LayerPanel layerPanel;
	
	public final TilesetPanel tilesetPanel;
	
	private final JFrame frame;	

	private final JButton btnSave, btnUndo, btnRedo;

	private final JToggleButton btnBrush, btnBucket, btnEraser, btnDrooper, btnAddVertex, btnAddCircle, btnEditVertex;

	private final JCheckBox chckbxShowGrid;

	private JLabel infoLabel;

	public final JPanel renderPanel;
	private JCheckBox chckbxShowShapes;
	private JButton btnInfo;
	private Component horizontalGlue;
	
	public static void main(String[] args) {

		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		EventQueue.invokeLater(new Runnable() {

			public void run() {

				try {
					
					String level = null;
					
					if(args != null && args.length > 0) {
												
						String flag;
						for(int i = 0; i < args.length; i++) {							
							flag = args[i].trim();							
							switch(flag) {							
							case "-l" :
								level = args[++i].trim();								
								break;
							case "-s" :
								splash = Boolean.valueOf(args[++i].trim());
								break;						
							}							
						}						
						
					}
					
					LevelEditor.getInstance().frame.setVisible(true);
					
					if(level != null) {
						LevelEditor.getInstance().loadFile(new File(level));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

	}

	private LevelEditor() {

		frame = new JFrame();
		frame.setTitle("Level 2d Editor");
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(LevelEditor.class.getResource("/icons/appIcon.png")));
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {								
				Gdx.app.exit();
				System.exit(0);
			}
			
		});
		
		undoRedo  = new UndoRedo<LayerPanel>(50);

		GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode dm = graphicsDevice.getDisplayMode();
	
		frame.setSize(dm.getWidth() - 150, dm.getHeight() - 100);	
		
		frame.setLocationRelativeTo(null);
		
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		
		JToolBar toolBar = new JToolBar();
		
		toolBar.setFloatable(false);
		
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		infoLabel = new JLabel();
		infoLabel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		infoLabel.setText(" ");
		
		frame.getContentPane().add(infoLabel, BorderLayout.SOUTH);
		
		final NewFileDialog newFileDialog = new NewFileDialog();
		
		JButton btnNew = new JButton("New");
		btnNew.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/newFile.png")));
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int status = newFileDialog.showDialog(frame);
				
				if(status == NewFileDialog.APPROVE_OPTION) {
					
					Gdx.app.postRunnable(new Runnable() {
						
						@Override
						public void run() {
							
							clear();
							
							frame.setTitle("Level 2d Editor");
														
							final Settings settings = Settings.DEFAULT_SETTINGS;
							
							settings.texturePath = newFileDialog.getTexturePath();					
							settings.tileWidth   = newFileDialog.getTileWidth();
							settings.tileHeight  = newFileDialog.getTileHeight();	
							settings.margin      = newFileDialog.getMargin();
							settings.spacing     = newFileDialog.getSpacing();	
							settings.mapWidth    = newFileDialog.getMapWidth();
							settings.mapHeight   = newFileDialog.getMapHeight();
														
							initialize(settings);
																					
						}
					});					
				}
				
			}
		});
		toolBar.add(btnNew);
		
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		fileChooser.setFileFilter( new FileNameExtensionFilter("Level Files (*.s2l)", "s2l"));
				
		JButton btnLoad = new JButton("Load");
		btnLoad.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/loadFile.png")));
		btnLoad.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int returnVal = fileChooser.showOpenDialog(LevelEditor.this.frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					loadFile(file);
				}

			}

		});
		toolBar.add(btnLoad);				
		
		btnSave = new JButton("Save");
		btnSave.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/saveFile.png")));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int returnVal = fileChooser.showSaveDialog(LevelEditor.this.frame);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							try {
								File file = fileChooser.getSelectedFile();
								if(!file.toString().toLowerCase().endsWith(".s2l")) {
									file = new File(file + ".s2l");
								}							

								LevelIO.save(file, LevelEditor.this);
								frame.setTitle("Level 2d Editor - " + file.getAbsolutePath());
							} catch(Throwable e) {
								new Thread() {
									public void run() {
										JOptionPane.showMessageDialog(frame,
												String.format("Error saving level file %s.", fileChooser.getSelectedFile().getAbsolutePath()),
												"Error",
												JOptionPane.ERROR_MESSAGE);
									}
								}.start();
							}
						}
					});
					
				}
				
			}
		});		
		toolBar.add(btnSave);
		
		toolBar.addSeparator();
		
		btnUndo = new JButton("");
		btnUndo.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/undo.png")));
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				undoRedo.undo();				
				updateUndoRedoButtons();				
				tilesetPanel.repaint();				
			}
		});
		toolBar.add(btnUndo);
		
		btnRedo = new JButton("");
		btnRedo.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/redo.png")));
		btnRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				undoRedo.redo();				
				updateUndoRedoButtons();				
				tilesetPanel.repaint();				
			}
		});
		toolBar.add(btnRedo);
		
		toolBar.addSeparator();
		
		btnBrush = new JToggleButton("");
		btnBrush.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				editor.setTool(Tool.BRUSH_TOOL);			
			}
		});
		btnBrush.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/brush.png")));
		toolBar.add(btnBrush);
		
		btnBrush.setSelected(true);
		
		btnBucket = new JToggleButton("");
		btnBucket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				editor.setTool(Tool.BUCKET_TOOL);
			}
		});
		btnBucket.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/bucket.png")));
		toolBar.add(btnBucket);
		
		btnEraser = new JToggleButton("");
		btnEraser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				editor.setTool(Tool.ERASER_TOOL);			
			}
		});
		btnEraser.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/eraser.png")));
		toolBar.add(btnEraser);
		
		btnDrooper = new JToggleButton("");
		btnDrooper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				editor.setTool(Tool.DROOPER_TOOL);				
			}
		});
		
		btnDrooper.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/dropper.png")));
		toolBar.add(btnDrooper);
		
		toolBar.addSeparator();
		
		btnAddVertex = new JToggleButton("");
		btnAddVertex.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {				
				editor.setTool(Tool.ADD_VERTEX_TOOL);			
			}
			
		});
		
		btnAddVertex.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/add_vertice.png")));
		toolBar.add(btnAddVertex);
				
		btnAddCircle = new JToggleButton("");
		btnAddCircle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				editor.setTool(Tool.ADD_CIRCLE_TOOL);
			}
		});
		
		btnAddCircle.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/add_circle.png")));
		toolBar.add(btnAddCircle);
		
		btnEditVertex = new JToggleButton("");
		btnEditVertex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				editor.setTool(Tool.EDIT_VERTEX_TOOL);			
			}
		});
				
		btnEditVertex.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/editVertices.png")));
		toolBar.add(btnEditVertex);
		
		toolBar.addSeparator();
		
		ButtonGroup buttonGroup = new ButtonGroup();
		
		buttonGroup.add(btnBrush);
		buttonGroup.add(btnBucket);
		buttonGroup.add(btnEraser);
		buttonGroup.add(btnDrooper);
		buttonGroup.add(btnAddVertex);
		buttonGroup.add(btnAddCircle);
		buttonGroup.add(btnEditVertex);
		
		chckbxShowGrid = new JCheckBox("Show Grid");
		chckbxShowGrid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				editor.showGrid = chckbxShowGrid.isSelected();				
			}
		});
		
		chckbxShowGrid.setSelected(true);
		toolBar.add(chckbxShowGrid);
		
		chckbxShowShapes = new JCheckBox("Show Shapes");
		chckbxShowShapes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				editor.showShapes = chckbxShowShapes.isSelected();				
			}
		});
		chckbxShowShapes.setSelected(true);
		toolBar.add(chckbxShowShapes);
		
		horizontalGlue = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue);
		
		btnInfo = new JButton("");
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new InfoDialog(frame).setVisible(true);
			}
		});
		
		btnInfo.setIcon(new ImageIcon(LevelEditor.class.getResource("/icons/info.png")));
		toolBar.add(btnInfo);
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		renderPanel = new JPanel();
		splitPane.setRightComponent(renderPanel);	
		
		editor = Editor.getInstance();
		
		final LwjglAWTCanvas glCanvas = new LwjglAWTCanvas(editor);
		final Canvas canvas = glCanvas.getCanvas();
		
		canvas.requestFocusInWindow();
		renderPanel.setLayout(new CardLayout(0, 0));
		renderPanel.add(canvas);
		
		JPanel panel = new JPanel();
		
		layerPanel = new LayerPanel();
		layerPanel.setBorder(new TitledBorder(null, "Layers", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		splitPane.setLeftComponent(panel);
		
		JPanel tmPanel = new JPanel();
		tmPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Tileset", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(tmPanel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(7)
							.addComponent(layerPanel, GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(7)
					.addComponent(layerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tmPanel, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		tmPanel.setLayout(new CardLayout(0, 0));
		
		tilesetPanel = new TilesetPanel();
		
		JScrollPane scrollPane = new JScrollPane(tilesetPanel);
		tmPanel.add(scrollPane, "name_39574355806592");
		panel.setLayout(gl_panel);
		
		frame.setFocusable(true);
		
		clear();
		if(splash) {
			new SplashDialog(frame);
		}
	}
	
	private void loadFile(File file) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				try {
					clear();
					if (file.getName().endsWith(".tmx")) {
						TmxLoader.load(file, LevelEditor.this);
					} else {
						LevelIO.load(file, LevelEditor.this);
					}
					frame.setTitle("Level 2d Editor - " + file.getAbsolutePath());
				} catch (Throwable e) {
					new Thread() {
						public void run() {
							JOptionPane.showMessageDialog(frame,
									String.format("Error loading level file %s.", file.getAbsolutePath()),
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}.start();
				}
			}
		});
	}
	
	public void setToolsEnabled(boolean enabled) {
		
		btnBrush.setEnabled(enabled);
		btnBucket.setEnabled(enabled);
		btnEraser.setEnabled(enabled);
		btnDrooper.setEnabled(enabled);
				
		btnAddVertex.setEnabled(enabled);
		btnAddCircle.setEnabled(enabled);
		btnEditVertex.setEnabled(enabled);
		
		if(!enabled) {
			editor.tool.clear();
		}
		
	}
	
	public void clear() {
				
		btnSave.setEnabled(false);
		
		setToolsEnabled(false);
				
		chckbxShowGrid.setEnabled(false);
		chckbxShowShapes.setEnabled(false);
		
		tilesetPanel.clear();
		layerPanel.clear();
		
		editor.clear();
		
		undoRedo.clear();
		
		updateUndoRedoButtons();
		
	}
	
	// Execute only in opengl context...
	public void initialize(Settings settings) {

		try {

			editor.initialize(settings);
			tilesetPanel.initialize(settings);					

			layerPanel.initialize();
			
			btnSave.setEnabled(true);

			tilesetPanel.revalidate();
			layerPanel.revalidate();

			chckbxShowGrid.setEnabled(true);
			chckbxShowShapes.setEnabled(true);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void setInfoText(String info) {
		infoLabel.setText(info);
	}

	public void updateUndoRedoButtons() {
		btnUndo.setEnabled(undoRedo.canUndo());
		btnRedo.setEnabled(undoRedo.canRedo());
	}	
}
