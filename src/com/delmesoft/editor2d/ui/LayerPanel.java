package com.delmesoft.editor2d.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Enumeration;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.delmesoft.editor2d.editor.Layer;
import com.delmesoft.editor2d.utils.undoredo.Changeable;

public class LayerPanel extends JPanel implements Changeable<LayerPanel>{

	private static final long serialVersionUID = 1L;

	public DefaultListModel<Layer> model;
	public JList<Layer> list;

	private JButton addButton;
	private JButton removeButton;

	private JCheckBox chckbxVisible;

	private JFormattedTextField formattedTextField;

	private JButton btnUpLayer;
	private JButton btnDownlayer;

	/**
	 * @wbp.parser.constructor
	 */
	public LayerPanel() {

		model = new DefaultListModel<Layer>();
		list = new JList<Layer>(model);

		list.setCellRenderer(new MyListCellRenderer());

		list.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {

				if(LevelEditor.getInstance().layerPanel == LayerPanel.this) {
					LevelEditor.getInstance().setToolsEnabled(model.size() > 0);
				}

			}

		});		

		JScrollPane pane = new JScrollPane(list);
		addButton = new JButton("Add");
		addButton.setIcon(new ImageIcon(LayerPanel.class.getResource("/icons/add_image.png")));
		removeButton = new JButton("Remove");
		removeButton.setIcon(new ImageIcon(LayerPanel.class.getResource("/icons/remove.png")));	

		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				final LevelEditor levelEditor = LevelEditor.getInstance();

				levelEditor.undoRedo.nextChange();

				levelEditor.undoRedo.mem(LayerPanel.this);
				levelEditor.updateUndoRedoButtons();

				addLayer();

			}

		});

		removeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int index = list.getSelectedIndex();

				if (index >= 0 && model.getSize() > 0) {

					final LevelEditor levelEditor = LevelEditor.getInstance();

					levelEditor.undoRedo.nextChange();

					levelEditor.undoRedo.mem(LayerPanel.this);
					levelEditor.updateUndoRedoButtons();						

					model.removeElementAt(index);

					if(model.getSize() > 0) {

						list.setSelectedIndex(Math.max(index - 1, 0));

					} else {

						removeButton.setEnabled(false);

						chckbxVisible.setEnabled(false);				
						chckbxVisible.setSelected(false);

						formattedTextField.setEnabled(false);
						formattedTextField.setText("");

					}

				}				

			}

		});

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Layer Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		chckbxVisible = new JCheckBox("");
		chckbxVisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				final LevelEditor levelEditor = LevelEditor.getInstance();

				levelEditor.undoRedo.nextChange();

				levelEditor.undoRedo.mem(LayerPanel.this);
				levelEditor.updateUndoRedoButtons();	

				list.getSelectedValue().visible = chckbxVisible.isSelected();				
			}

		});

		formattedTextField = new JFormattedTextField();

		formattedTextField.addFocusListener(new FocusAdapter() {
			
			private int lastIndex   = -1;
			private String lastName = "Default name";
			
			@Override
			public void focusLost(FocusEvent e) {
				
				if(lastIndex > -1) {
					
					if(lastName.equals(formattedTextField.getText()) || isValidName(model.getElementAt(lastIndex).name, lastIndex) == false) {
						LevelEditor.getInstance().undoRedo.undo();
					} else {
						LevelEditor.getInstance().updateUndoRedoButtons();
					}			
					
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				final LevelEditor levelEditor = LevelEditor.getInstance();
				levelEditor.undoRedo.nextChange();
				levelEditor.undoRedo.mem(LayerPanel.this);
				
				lastIndex = list.getSelectedIndex();
				lastName = model.getElementAt(lastIndex).name;
			}

		});

		formattedTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				changed();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changed();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				changed();
			}

			public void changed() {
				if(formattedTextField.hasFocus()) {
					if(list.getSelectedIndex() > -1) {
						list.getSelectedValue().name = formattedTextField.getText();					
						list.repaint();					
					}
				}
			}

		});

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
						.addGap(6)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
								.addComponent(pane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
								.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
										.addComponent(addButton, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(removeButton, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)))
						.addGap(4))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(6)
						.addComponent(pane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(3)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(removeButton)
								.addComponent(addButton))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
				);

		btnUpLayer = new JButton("");
		btnUpLayer.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				final LevelEditor levelEditor = LevelEditor.getInstance();

				levelEditor.undoRedo.nextChange();

				levelEditor.undoRedo.mem(LayerPanel.this);
				levelEditor.updateUndoRedoButtons();	

				int index = list.getSelectedIndex();

				Layer layer = model.remove(index - 1);

				model.add(index, layer);

			}

		});

		btnUpLayer.setIcon(new ImageIcon(LayerPanel.class.getResource("/icons/arrow-up.png")));

		btnDownlayer = new JButton("");
		btnDownlayer.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				final LevelEditor levelEditor = LevelEditor.getInstance();

				levelEditor.undoRedo.nextChange();

				levelEditor.undoRedo.mem(LayerPanel.this);
				levelEditor.updateUndoRedoButtons();	

				int index = list.getSelectedIndex();

				Layer layer = model.remove(index);

				model.add(index + 1, layer);

				list.setSelectedIndex(index + 1);

			}

		});
		btnDownlayer.setIcon(new ImageIcon(LayerPanel.class.getResource("/icons/arrow-down.png")));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addGap(7)
						.addComponent(chckbxVisible)
						.addGap(4)
						.addComponent(formattedTextField, GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnUpLayer, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
						.addGap(2)
						.addComponent(btnDownlayer, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
						.addGap(2))
				);
		gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
						.addGap(7)
						.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(formattedTextField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
								.addComponent(btnUpLayer, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
								.addComponent(btnDownlayer, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 21, Short.MAX_VALUE)
								.addComponent(chckbxVisible, Alignment.LEADING))
						.addContainerGap())
				);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);

	}

	public LayerPanel(LayerPanel layerPanel) {

		this();

		set(layerPanel);

	}

	public void addLayer() {

		final DefaultListModel<Layer> model = this.model;

		String name = generateNewName("Layer ");

		model.addElement(new Layer(name, true, Settings.DEFAULT_SETTINGS.mapWidth, Settings.DEFAULT_SETTINGS.mapHeight));

		list.setSelectedIndex(model.size() - 1);

	}

	private String generateNewName(String baseName) {

		int counter = 1;
		String name;
		
		do {
			name = baseName + counter++;
		} while (!isValidName(name, -1));

		return name;
	}

	private boolean isValidName(String name, int ignoreIndex) {

		if(name == null || name.trim().equals("")) return false;

		for (int i = 0, n = model.size(); i < n; i++) {

			if (i != ignoreIndex && name.equals(model.get(i).name)) {
				return false;
			}

		}

		return true;

	}

	public void initialize() {
		addButton.setEnabled(true);
	}

	public void clear() {

		model.removeAllElements();

		addButton.setEnabled(false);
		removeButton.setEnabled(false);

		chckbxVisible.setEnabled(false);
		chckbxVisible.setSelected(false);

		btnUpLayer.setEnabled(false);
		btnDownlayer.setEnabled(false);

		formattedTextField.setEnabled(false);
		formattedTextField.setText("");

	}

	@Override
	public LayerPanel copy() {
		return new LayerPanel(this);
	}

	@Override
	public LayerPanel set(LayerPanel layerPanel) {

		model.clear();

		Enumeration<Layer> layers = layerPanel.model.elements();
		while(layers.hasMoreElements()) {
			model.addElement(layers.nextElement().copy());
		}

		list.setSelectedIndex(layerPanel.list.getSelectedIndex());

		addButton.setEnabled(layerPanel.addButton.isEnabled());
		removeButton.setEnabled(layerPanel.removeButton.isEnabled());

		chckbxVisible.setEnabled(layerPanel.chckbxVisible.isEnabled());				
		chckbxVisible.setSelected(layerPanel.chckbxVisible.isSelected());

		formattedTextField.setEnabled(layerPanel.formattedTextField.isEnabled());
		formattedTextField.setText(layerPanel.formattedTextField.getText());	

		return this;
	}

	public class MyListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

			Layer layerInfo = (Layer) value;

			if(isSelected) {

				removeButton.setEnabled(true);

				chckbxVisible.setEnabled(true);				
				chckbxVisible.setSelected(layerInfo.visible);

				formattedTextField.setEnabled(true);

				if(!formattedTextField.getText().equals(layerInfo.name)){
					formattedTextField.setText(layerInfo.name);
				}

				int size = model.size();

				boolean enable = size > 1;

				btnUpLayer.setEnabled(enable && index != 0);				
				btnDownlayer.setEnabled(enable && (index < size - 1));

			}

			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			label.setText(layerInfo.name);

			return label;

		}

	}

}
