package com.delmesoft.editor2d.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.swing.DefaultListModel;

import com.delmesoft.editor2d.editor.Layer;
import com.delmesoft.editor2d.graphics.polygon.Polygon;
import com.delmesoft.editor2d.graphics.polygon.Vertex;
import com.delmesoft.editor2d.ui.LevelEditor;
import com.delmesoft.editor2d.ui.Settings;
import com.delmesoft.editor2d.utils.datastructure.Array;

public class LevelIO {


	public static void save(File file, LevelEditor levelEditor) throws Throwable {
		
		try(FileOutputStream out = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new DeflaterOutputStream(out, true)))) {


			final Settings settings = Settings.DEFAULT_SETTINGS;

			Path tmp = Paths.get(file.getAbsolutePath());

			Path absPath = Paths.get(settings.texturePath);

			String textureFile = tmp.getParent().relativize(absPath).toString();

			if(textureFile.contains(" ")){ textureFile = textureFile.replace(" ", "%"); }

			// Texture file, tile width, tile height, magin, spacing, mapWidth, mapHeight, layerCount
			writer.write(textureFile + " " + settings.tileWidth + " " + settings.tileHeight + " " + settings.margin + " " + settings.spacing + " " + settings.mapWidth + " " + settings.mapHeight + " " + levelEditor.layerPanel.model.size() + "\n");

			Enumeration<Layer> layers = levelEditor.layerPanel.model.elements();

			while(layers.hasMoreElements()) {

				Layer layer = layers.nextElement();

				String name = layer.name.contains(" ") ? layer.name.replace(" ", "%") : layer.name;
				// Name, visible				
				writer.write(name + " " + (layer.visible ? 1 : 0) + "\n");			

				int n = layer.getDataLength() - 1;
				// Tile types
				for (int i = 0; i < n; i++) {
					writer.write(layer.getTileType(i) + ",");
				}

				writer.write(layer.getTileType(n) + "\n");

				final Array<Polygon> polygons = layer.polygons;

				Polygon polygon;
				for (int i = 0; i < polygons.size; i++) {

					polygon = polygons.get(i);

					n = polygon.getVertexCount() - 1;
					// polygon, vertexCount
					writer.write("polygon " + (n + 1) + "\n");

					Vertex v;

					for (int j = 0; j < n; j++) {

						v = polygon.getVertex(j);
						// Vertex
						writer.write(v.x + "," + v.y + ",");

					}

					v = polygon.getVertex(n);

					writer.write(v.x + "," + v.y + "\n");

				}
				// Layer end
				writer.write(">\n");

			}

		}

	}


	public static void load(File file, LevelEditor levelEditor) throws Throwable {

		long start = System.nanoTime();

		try (FileInputStream in = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(new InflaterInputStream(in)))){


			Path basePath = FileSystems.getDefault().getPath(file.getAbsolutePath());

			String[] stringArray = br.readLine().split("\\s+");

			String texturePath = stringArray[0];

			if (texturePath.contains("%")) {
				texturePath = texturePath.replace("%", " ");
			}
			
			texturePath = texturePath.replaceAll("\\\\", File.separator);

			Path resolvedPath = basePath.getParent().resolve(texturePath); // use getParent() if basePath is a file (not a directory) 
			
			final Settings settings = Settings.DEFAULT_SETTINGS;

			settings.texturePath = resolvedPath.normalize().toString();

			settings.tileWidth  = Integer.valueOf(stringArray[1]);
			settings.tileHeight = Integer.valueOf(stringArray[2]);
			settings.margin     = Integer.valueOf(stringArray[3]);
			settings.spacing    = Integer.valueOf(stringArray[4]);
			settings.mapWidth   = Integer.valueOf(stringArray[5]);
			settings.mapHeight  = Integer.valueOf(stringArray[6]);

			// layerCount = Integer.valueOf(stringArray[5]);

			levelEditor.initialize(settings);

			String strLine;

			DefaultListModel<Layer> model = levelEditor.layerPanel.model;

			while ((strLine = br.readLine()) != null) {

				stringArray = strLine.split("\\s+");

				Layer layer = new Layer(stringArray[0].replace('%', ' '), Integer.valueOf(stringArray[1]) > 0, settings.mapWidth, settings.mapHeight);
				model.addElement(layer);

				stringArray = (strLine = br.readLine()).split(",");

				for(int i = 0, n = stringArray.length; i < n; i++) {
					layer.setTileType(i, Integer.valueOf(stringArray[i]));
				}

				for(strLine = br.readLine(); !strLine.equals(">"); strLine = br.readLine()) {
					// Polygon
					stringArray = strLine.split("\\s+");					
					int n = Integer.valueOf(stringArray[1]) << 1;
					// Vertices
					strLine = br.readLine();
					stringArray = strLine.split(",");
					float[] vertices = new float[n];				
					for(int i = 0; i < n; i++) {
						vertices[i] = Float.valueOf(stringArray[i]);
					}

					layer.polygons.add(new Polygon(vertices, layer));
				}

			}

			if(model.size() > 0) {
				levelEditor.layerPanel.list.setSelectedIndex(0);
				levelEditor.setToolsEnabled(true);
			}

		}

		System.out.printf("Level loaded in %f seconds\n", (System.nanoTime() - start) / 1000000000f);

	}

}
