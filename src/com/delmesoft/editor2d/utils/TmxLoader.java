package com.delmesoft.editor2d.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import javax.swing.DefaultListModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.delmesoft.editor2d.editor.Layer;
import com.delmesoft.editor2d.ui.LevelEditor;
import com.delmesoft.editor2d.ui.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TmxLoader {

	public static void load(File file, LevelEditor levelEditor) throws Throwable {

		long start = System.nanoTime();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		Path basePath = FileSystems.getDefault().getPath(file.getAbsolutePath());

		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);

		final Settings settings = Settings.DEFAULT_SETTINGS;

		Node item = document.getFirstChild();

		settings.tileWidth  = stringToInt(getAttributeValue(item, "tilewidth"));
		settings.tileHeight = stringToInt(getAttributeValue(item, "tileheight"));

		settings.mapWidth   = stringToInt(getAttributeValue(item, "width"));
		settings.mapHeight  = stringToInt(getAttributeValue(item, "height"));

		NodeList tilesets = document.getElementsByTagName("tileset");	

		Node tileset = tilesets.item(0);
		settings.texturePath = basePath.getParent().resolve(getAttributeValue(getChildNodeMap(tileset).get("image"), "source")).normalize().toString();

		settings.margin  = stringToInt(getAttributeValue(tileset, "margin"));
		settings.spacing = stringToInt(getAttributeValue(tileset, "spacing"));

		levelEditor.initialize(settings);

		DefaultListModel<Layer> model = levelEditor.layerPanel.model;
		Layer layer;
		NodeList layers = document.getElementsByTagName("layer");
		Node nodeLayer;
		int n = layers.getLength();
		for(int i = 0; i < n; i++) {
			nodeLayer = layers.item(i);

			layer = new Layer(getAttributeValue(nodeLayer, "name"), true, settings.mapWidth, settings.mapHeight);

			Node data = getChildNodeMap(nodeLayer).get("data");

			String encoding = getAttributeValue(data, "encoding");
			String compression = getAttributeValue(data, "compression");


			byte[] enc = data.getTextContent().trim().getBytes();

			if(encoding != null) {

				if(encoding.equals("base64")) {

					byte[] dec = Base64.getDecoder().decode(enc);

					final ByteArrayInputStream bais = new ByteArrayInputStream(dec);
					InputStream is;

					if ("gzip".equalsIgnoreCase(compression)) {
						is = new GZIPInputStream(bais);
					} else if ("zlib".equalsIgnoreCase(compression)) {
						is = new InflaterInputStream(bais);
					} else {
						is = bais;
					}

					int d;
					for(int j = 0, n2 = settings.mapWidth * settings.mapHeight; j < n2; j++) {

						d  = is.read()       | 
								is.read() <<  8 | 
								is.read() << 16 | 
								is.read() << 32; 

						int x = j % settings.mapWidth;
						int y = (settings.mapHeight -1) - j / settings.mapWidth;
						if(d < layer.tileData.length)
							layer.tileData[y + x * settings.mapHeight] = d;
					}

					//System.out.println(value.length());

				}

			}

			model.addElement(layer);

		}

		if(model.size() > 0) {
			levelEditor.layerPanel.list.setSelectedIndex(0);
			levelEditor.setToolsEnabled(true);
		}


		System.out.printf("Level loaded in %f seconds\n", (System.nanoTime() - start) / 1000000000f);

	}

	private static int stringToInt(String value) {
		return value == null ? 0 : Integer.parseInt(value);
	}

	private static Map<String, Node> getChildNodeMap(Node node) {
		HashMap<String, Node> map = new HashMap<>();
		NodeList list = node.getChildNodes();
		int n = list.getLength();
		for(int i = 0; i < n; i++) {
			node = list.item(i);
			map.put(node.getNodeName(), node);
		}
		return map;
	}

	private static String getAttributeValue(Node node, String attribname) {
		final NamedNodeMap attributes = node.getAttributes();
		String value = null;
		if (attributes != null) {
			Node attribute = attributes.getNamedItem(attribname);
			if (attribute != null) {
				value = attribute.getNodeValue();
			}
		}
		return value;
	}

	public static String decompress(final byte[] data) throws IOException {
		String outStr = "";

		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			outStr += line;
		}

		return outStr;
	}

}