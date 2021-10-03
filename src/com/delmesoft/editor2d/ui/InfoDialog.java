package com.delmesoft.editor2d.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import java.awt.Font;

public class InfoDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public InfoDialog(final Component parent) {
		setAlwaysOnTop(true);
		setTitle("About Level Editor");
		setIconImage(Toolkit.getDefaultToolkit().getImage(InfoDialog.class.getResource("/icons/info.png")));

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(385, 260);

		setLocationRelativeTo(parent);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel label = new JLabel("");
			label.setIcon(new ImageIcon(InfoDialog.class.getResource("/icons/appIcon.png")));
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.insets = new Insets(0, 0, 5, 0);
			gbc_label.gridx = 0;
			gbc_label.gridy = 0;
			contentPanel.add(label, gbc_label);
		}
		{
			JLabel lblLevelEditord = new JLabel("Level Editor 2D");
			lblLevelEditord.setFont(new Font("Dialog", Font.BOLD, 20));
			GridBagConstraints gbc_lblLevelEditord = new GridBagConstraints();
			gbc_lblLevelEditord.insets = new Insets(0, 0, 5, 0);
			gbc_lblLevelEditord.gridx = 0;
			gbc_lblLevelEditord.gridy = 1;
			contentPanel.add(lblLevelEditord, gbc_lblLevelEditord);
		}
		{
			JLabel lblDelmesoft = new JLabel("Advanced level designer");
			GridBagConstraints gbc_lblDelmesoft = new GridBagConstraints();
			gbc_lblDelmesoft.insets = new Insets(0, 0, 5, 0);
			gbc_lblDelmesoft.gridx = 0;
			gbc_lblDelmesoft.gridy = 2;
			contentPanel.add(lblDelmesoft, gbc_lblDelmesoft);
		}
		{
			JLabel label = new JLabel("-------------------------------------------------");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.insets = new Insets(0, 0, 5, 0);
			gbc_label.gridx = 0;
			gbc_label.gridy = 3;
			contentPanel.add(label, gbc_label);
		}
		{
			JLabel lblProgramedBySergio = new JLabel("2021 Delmesoft - Sergio Soriano");
			GridBagConstraints gbc_lblProgramedBySergio = new GridBagConstraints();
			gbc_lblProgramedBySergio.insets = new Insets(0, 0, 5, 0);
			gbc_lblProgramedBySergio.gridx = 0;
			gbc_lblProgramedBySergio.gridy = 4;
			contentPanel.add(lblProgramedBySergio, gbc_lblProgramedBySergio);
		}
		{
			JLabel lblSergissgmailcom = new JLabel("sergi.ss4@gmail.com");
			GridBagConstraints gbc_lblSergissgmailcom = new GridBagConstraints();
			gbc_lblSergissgmailcom.gridx = 0;
			gbc_lblSergissgmailcom.gridy = 5;
			contentPanel.add(lblSergissgmailcom, gbc_lblSergissgmailcom);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.addActionListener(new ActionListener() {
				
					public void actionPerformed(ActionEvent e) {
				
						dispose();
			
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
