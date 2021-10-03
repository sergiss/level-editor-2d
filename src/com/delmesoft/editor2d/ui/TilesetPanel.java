package com.delmesoft.editor2d.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public class TilesetPanel extends JPanel implements Scrollable, MouseListener {
	
	private static final long serialVersionUID = 1L;
	
	private Settings settings;
		
	private BufferedImage[] tiles;
	
	private int cols, rows;
		
	private float zoom;
	
	public int selectedTile;
	
	public TilesetPanel() {

		addMouseListener(this);		
		
		setBackground(Color.WHITE);

	}
	
	public void initialize(Settings settings) throws IOException {

		this.settings = settings;

		int tileWidth  = settings.tileWidth;
		int tileHeight = settings.tileHeight;
		int margin  = settings.margin;
		int spacing = settings.spacing;

		final BufferedImage image = ImageIO.read(new File(settings.texturePath));

		int width  = image.getWidth();
		int height = image.getHeight();

		cols = (width  - margin) / (tileWidth  + spacing);
		rows = (height - margin) / (tileHeight + spacing);

		tiles = new BufferedImage[cols * rows];

		Graphics g;

		BufferedImage img;

		int index;		
		
		for (int j = 0; j < rows; j++) {
			
			index = j * cols;
			
			int y1 = j * (tileHeight + spacing) + margin;

			for (int i = 0; i < cols; i++) {					

				img = new BufferedImage(tileWidth, tileHeight, image.getType());

				g = img.getGraphics();

				int x1 = i * (tileWidth  + spacing) + margin;				

				g.drawImage(image, 0, 0, tileWidth, tileHeight, x1, y1, x1 + tileWidth, y1 + tileHeight, null);

				g.dispose();

				tiles [index + i] = img;

			}

		}

		setZoom(1);

	}
	
	public void setZoom(float zoom) {

		this.zoom = zoom;

		setPreferredSize(new Dimension(cols * fastCeil(settings.tileWidth  * zoom), 
									   rows * fastCeil(settings.tileWidth  * zoom)));

		revalidate();
		repaint();
		
	}
   
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		
		if (tiles != null) {
			
			Rectangle clip = g.getClipBounds();

			int tileWidth  = fastCeil(settings.tileWidth  * zoom);
			int tileHeight = fastCeil(settings.tileHeight * zoom);
			
			int startX = clip.x - (clip.x % tileWidth );
			int startY = clip.y - (clip.y % tileHeight);
			
			int col, row;

			for (int x = startX; x < clip.x + clip.width; x += tileWidth) {
				
				col = x / tileWidth;
				
				for (int y = startY; y < clip.y + clip.height; y += tileHeight) {
					
					row = y / tileHeight;
					
					g.drawImage(tiles[row * cols + col], x, y, tileWidth, tileHeight, null);
					
				}
			}		

			if(selectedTile > -1) {	
								
				Graphics2D g2d = (Graphics2D) g.create();
				
				g2d.setColor(new Color(50, 50, 128, 100));
				
				col = (selectedTile % cols);
				row = (selectedTile / cols);
				
				g2d.fillRect(col * tileWidth, row * tileHeight, tileWidth, tileHeight);
								
				g2d.dispose();
				
			}		

		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {	
		
		if (tiles != null) {
			
			int selectedTile = (e.getPoint().x / fastCeil(settings.tileWidth * zoom)) + (e.getPoint().y / fastCeil(settings.tileHeight * zoom)) * cols;
			
			if(selectedTile != this.selectedTile) {
				
				this.selectedTile = selectedTile;

				repaint();
			}
			
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	public void clear() {

		tiles = null;
		selectedTile = 0;
		
		setPreferredSize(new Dimension());
		
		revalidate();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return (int) Math.ceil((orientation == 0 ? settings.tileWidth : settings.tileHeight) * zoom);
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return orientation == 0 ? (int) visibleRect.getWidth() : (int) visibleRect.getHeight();
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return null;
	}
	
	private int fastCeil(float x) {
		int xi = (int) x;
		return x > xi ? xi + 1 : xi;
	}

}
