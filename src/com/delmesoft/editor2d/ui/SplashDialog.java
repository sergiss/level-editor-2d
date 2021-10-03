package com.delmesoft.editor2d.ui;

import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

public class SplashDialog extends JDialog implements MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private BufferedImage bufferedImage;

	public SplashDialog(Frame frame) {
		super(frame);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new CardLayout(0, 0));

		try {
			bufferedImage = ImageIO.read(LevelEditor.class.getResource("/textures/title.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		setSize(bufferedImage.getWidth(), bufferedImage.getHeight());
		setLocationRelativeTo(null);
		setUndecorated(true);

		addMouseListener(this);
		addKeyListener(this);

		JPanel panel = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.drawImage(bufferedImage, null, 0, 0);
				g2d.dispose();

			}
		};

		TimerTask timerTask = new TimerTask() {
			public void run() {
				dispose();
			}
		};

		Timer timer = new Timer();

		timer.scheduleAtFixedRate(timerTask, 3000, 3000);

		getContentPane().add(panel);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);

		JLabel lblAB = new JLabel("2021 Sergio S.");
		sl_panel.putConstraint(SpringLayout.SOUTH, lblAB, -10,
				SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, lblAB, -10,
				SpringLayout.EAST, panel);
		panel.add(lblAB);

		setModal(true);
		setVisible(true);

	}

	@Override
	public void keyPressed(KeyEvent e) {
		dispose();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		dispose();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
