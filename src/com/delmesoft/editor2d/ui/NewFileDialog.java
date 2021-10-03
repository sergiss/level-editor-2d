package com.delmesoft.editor2d.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class NewFileDialog extends JDialog {

	public static final int CANCEL_OPTION  = 0;	
	public static final int APPROVE_OPTION = 1;	

	private static final long serialVersionUID = 1L;

	private final JPanel infoPanel = new JPanel();

	int retunVal;

	private JButton okButton;

	private JTextField texturePathTextField;

	private JSpinner spinnerTileWidth, 
	spinnerTileHeight;

	private JSpinner spinnerMargin,
	spinnerSpacing;

	private JSpinner spinnerMapWidth, 
	spinnerMapHeight;	

	@SuppressWarnings("deprecation")
	public NewFileDialog() {
		setResizable(false);

		setIconImage(Toolkit.getDefaultToolkit().getImage(NewFileDialog.class.getResource("/icons/appIcon.png")));
		setTitle("New Map");

		setBounds(100, 100, 330, 300);

		getContentPane().setLayout(new BorderLayout());
		infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(infoPanel, BorderLayout.CENTER);

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		fileChooser.setFileFilter( new FileNameExtensionFilter("Image Files (*.jpg *.png)", "jpg", "png"));
		GridBagLayout gbl_infoPanel = new GridBagLayout();
		gbl_infoPanel.columnWidths = new int[]{166, 168, 0};
		gbl_infoPanel.rowHeights = new int[]{150, 98, 0};
		gbl_infoPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_infoPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		infoPanel.setLayout(gbl_infoPanel);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Tileset", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridwidth = 2;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		infoPanel.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0};

		panel.setLayout(gbl_panel);

		JButton btnNewButton = new JButton("...");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int returnVal = fileChooser.showOpenDialog(NewFileDialog.this);

				if(returnVal == JFileChooser.APPROVE_OPTION) {

					File file = fileChooser.getSelectedFile();

					texturePathTextField.setText(file.getAbsolutePath());

					okButton.setEnabled(true);

				}

			}

		});

		JLabel lblSource = new JLabel("Source:");
		GridBagConstraints gbc_lblSource = new GridBagConstraints();
		gbc_lblSource.anchor = GridBagConstraints.WEST;
		gbc_lblSource.insets = new Insets(0, 0, 5, 5);
		gbc_lblSource.gridx = 0;
		gbc_lblSource.gridy = 0;
		panel.add(lblSource, gbc_lblSource);

		texturePathTextField = new JTextField();
		texturePathTextField.setEditable(false);
		texturePathTextField.setColumns(10);
		GridBagConstraints gbc_texturePathTextField = new GridBagConstraints();
		gbc_texturePathTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_texturePathTextField.insets = new Insets(0, 0, 5, 5);
		gbc_texturePathTextField.gridwidth = 3;
		gbc_texturePathTextField.gridx = 1;
		gbc_texturePathTextField.gridy = 0;
		panel.add(texturePathTextField, gbc_texturePathTextField);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 4;
		gbc_btnNewButton.gridy = 0;
		panel.add(btnNewButton, gbc_btnNewButton);

		JLabel lblTileWidth = new JLabel("Width:");
		GridBagConstraints gbc_lblTileWidth = new GridBagConstraints();
		gbc_lblTileWidth.anchor = GridBagConstraints.WEST;
		gbc_lblTileWidth.insets = new Insets(0, 0, 5, 5);
		gbc_lblTileWidth.gridx = 0;
		gbc_lblTileWidth.gridy = 1;
		panel.add(lblTileWidth, gbc_lblTileWidth);

		spinnerTileWidth = new JSpinner();
		spinnerTileWidth.setModel(new SpinnerNumberModel(new Integer(16), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_spinnerTileWidth = new GridBagConstraints();
		gbc_spinnerTileWidth.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerTileWidth.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerTileWidth.gridx = 1;
		gbc_spinnerTileWidth.gridy = 1;
		panel.add(spinnerTileWidth, gbc_spinnerTileWidth);

		JLabel lblMargin = new JLabel("Margin:");
		GridBagConstraints gbc_lblMargin = new GridBagConstraints();
		gbc_lblMargin.anchor = GridBagConstraints.WEST;
		gbc_lblMargin.insets = new Insets(0, 0, 5, 5);
		gbc_lblMargin.gridx = 2;
		gbc_lblMargin.gridy = 1;
		panel.add(lblMargin, gbc_lblMargin);

		spinnerMargin = new JSpinner();
		spinnerMargin.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spinnerMargin = new GridBagConstraints();
		gbc_spinnerMargin.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerMargin.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerMargin.gridwidth = 2;
		gbc_spinnerMargin.gridx = 3;
		gbc_spinnerMargin.gridy = 1;
		panel.add(spinnerMargin, gbc_spinnerMargin);

		JLabel lblTileHeight = new JLabel("Height:");
		GridBagConstraints gbc_lblTileHeight = new GridBagConstraints();
		gbc_lblTileHeight.anchor = GridBagConstraints.WEST;
		gbc_lblTileHeight.insets = new Insets(0, 0, 0, 5);
		gbc_lblTileHeight.gridx = 0;
		gbc_lblTileHeight.gridy = 2;
		panel.add(lblTileHeight, gbc_lblTileHeight);

		spinnerTileHeight = new JSpinner();
		spinnerTileHeight.setModel(new SpinnerNumberModel(new Integer(16), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_spinnerTileHeight = new GridBagConstraints();
		gbc_spinnerTileHeight.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerTileHeight.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerTileHeight.gridx = 1;
		gbc_spinnerTileHeight.gridy = 2;
		panel.add(spinnerTileHeight, gbc_spinnerTileHeight);

		JLabel lblSpacing = new JLabel("Spacing:");
		GridBagConstraints gbc_lblSpacing = new GridBagConstraints();
		gbc_lblSpacing.anchor = GridBagConstraints.WEST;
		gbc_lblSpacing.insets = new Insets(0, 0, 0, 5);
		gbc_lblSpacing.gridx = 2;
		gbc_lblSpacing.gridy = 2;
		panel.add(lblSpacing, gbc_lblSpacing);

		spinnerSpacing = new JSpinner();
		spinnerSpacing.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_spinnerSpacing = new GridBagConstraints();
		gbc_spinnerSpacing.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerSpacing.gridwidth = 2;
		gbc_spinnerSpacing.gridx = 3;
		gbc_spinnerSpacing.gridy = 2;
		panel.add(spinnerSpacing, gbc_spinnerSpacing);

		JPanel panel2 = new JPanel();
		panel2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Map size", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel2 = new GridBagConstraints();
		gbc_panel2.fill = GridBagConstraints.BOTH;
		gbc_panel2.insets = new Insets(0, 0, 0, 5);
		gbc_panel2.gridx = 0;
		gbc_panel2.gridy = 1;
		infoPanel.add(panel2, gbc_panel2);
		GridBagLayout gbl_panel2 = new GridBagLayout();
		gbl_panel2.columnWeights = new double[]{0.0, 1.0};
		panel2.setLayout(gbl_panel2);

		JLabel lblWidth = new JLabel("Width:");
		GridBagConstraints gbc_lblWidth = new GridBagConstraints();
		gbc_lblWidth.anchor = GridBagConstraints.WEST;
		gbc_lblWidth.insets = new Insets(0, 0, 5, 5);
		gbc_lblWidth.gridx = 0;
		gbc_lblWidth.gridy = 0;
		panel2.add(lblWidth, gbc_lblWidth);

		spinnerMapWidth = new JSpinner();
		spinnerMapWidth.setModel(new SpinnerNumberModel(new Integer(100), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_spinnerMapWidth = new GridBagConstraints();
		gbc_spinnerMapWidth.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerMapWidth.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerMapWidth.gridx = 1;
		gbc_spinnerMapWidth.gridy = 0;
		panel2.add(spinnerMapWidth, gbc_spinnerMapWidth);

		JLabel lblHeight = new JLabel("Height:");
		GridBagConstraints gbc_lblHeight = new GridBagConstraints();
		gbc_lblHeight.anchor = GridBagConstraints.WEST;
		gbc_lblHeight.insets = new Insets(0, 0, 0, 5);
		gbc_lblHeight.gridx = 0;
		gbc_lblHeight.gridy = 1;
		panel2.add(lblHeight, gbc_lblHeight);

		spinnerMapHeight = new JSpinner();
		spinnerMapHeight.setModel(new SpinnerNumberModel(100, 1, null, 1));
		GridBagConstraints gbc_spinnerMapHeight = new GridBagConstraints();
		gbc_spinnerMapHeight.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerMapHeight.gridx = 1;
		gbc_spinnerMapHeight.gridy = 1;
		panel2.add(spinnerMapHeight, gbc_spinnerMapHeight);

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new GridLayout(2, 1, 0, 0));

		JLabel lblIcon = new JLabel("");
		lblIcon.setVerticalAlignment(SwingConstants.BOTTOM);
		lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
		lblIcon.setIcon(new ImageIcon(NewFileDialog.class.getResource("/icons/appIcon.png")));
		panel_1.add(lblIcon);

		JLabel lblDelmesoft = new JLabel("Sergio S. 2021");
		lblDelmesoft.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblDelmesoft);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 1;
		infoPanel.add(panel_1, gbc_panel_1);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				GridBagLayout gbl_buttonPane = new GridBagLayout();
				gbl_buttonPane.columnWeights = new double[]{1.0, 0.0};
				buttonPane.setLayout(gbl_buttonPane);
			}
			{
				okButton = new JButton("OK");
				okButton.setEnabled(false);
				okButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						retunVal = APPROVE_OPTION;
						dispose();
					}

				});
				okButton.setActionCommand("OK");
				GridBagConstraints gbc_okButton = new GridBagConstraints();
				gbc_okButton.fill = GridBagConstraints.VERTICAL;
				gbc_okButton.anchor = GridBagConstraints.EAST;
				gbc_okButton.insets = new Insets(0, 0, 0, 5);
				gbc_okButton.gridx = 0;
				gbc_okButton.gridy = 0;
				buttonPane.add(okButton, gbc_okButton);
				getRootPane().setDefaultButton(okButton);
			}
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					retunVal = CANCEL_OPTION;
					dispose();
				}

			});
			cancelButton.setActionCommand("Cancel");
			GridBagConstraints gbc_cancelButton = new GridBagConstraints();
			gbc_cancelButton.fill = GridBagConstraints.VERTICAL;
			gbc_cancelButton.anchor = GridBagConstraints.WEST;
			gbc_cancelButton.gridx = 1;
			gbc_cancelButton.gridy = 0;
			buttonPane.add(cancelButton, gbc_cancelButton);
		}

	}

	public String getTexturePath() {
		return texturePathTextField.getText();
	}

	public int getTileWidth() {
		return (int) spinnerTileWidth.getValue();
	}

	public int getTileHeight() {
		return (int) spinnerTileHeight.getValue();
	}

	public int getMargin() {
		return (int) spinnerMargin.getValue();
	}

	public int getSpacing() {
		return (int) spinnerSpacing.getValue();
	}

	public int getMapWidth() {
		return (int) spinnerMapWidth.getValue();
	}

	public int getMapHeight() {
		return (int) spinnerMapHeight.getValue();
	}

	public int showDialog(Component component) {

		retunVal = CANCEL_OPTION;

		setLocationRelativeTo(component);
		setModal(true);
		setVisible(true);

		return retunVal;
	}

}
