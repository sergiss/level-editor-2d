package com.delmesoft.editor2d.graphics.g2d.shader;

import java.util.HashMap;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;


public class ShaderProvider {

	public enum ShaderType {
		DEFAULT, SHAPE_SHADER
	}
	
	private static final HashMap<ShaderType, ShaderProgram> programMap = new HashMap<ShaderProvider.ShaderType, ShaderProgram>();
	
	public static ShaderProgram getShader(ShaderType shaderType) {
		
		ShaderProgram shaderProgram = programMap.get(shaderType);
		
		if(shaderProgram == null) {
			
			switch (shaderType) {

			case DEFAULT:				
				shaderProgram = new ShaderProgram(defaultVertexShader, defaulFragmentShader);
				break;				
			case SHAPE_SHADER:				
				shaderProgram = new ShaderProgram(shapeVertexShader, shapeFragmentShader);
				break;
				
			}
			
			if (!shaderProgram.isCompiled()) {
				throw new IllegalArgumentException("Error compiling shader: " + shaderProgram.getLog());
			}
			
			programMap.put(shaderType, shaderProgram);
			
		}
		
		return shaderProgram;
	}
	
	// SPRITE RENDERER
	
	private static final String defaultVertexShader  = 

			          "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
					+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
					+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
					+ "uniform mat4 u_projTrans;\n"
					+ "varying vec4 v_color;\n"
					+ "varying vec2 v_texCoords;\n"
					+ "\n"
					+ "void main()\n"
					+ "{\n"
					+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
					+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
					+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
					+ "}\n";

	private static final String defaulFragmentShader =

					  "#ifdef GL_ES\n"
					+ "#define LOWP lowp\n"
					+ "#define MED mediump\n"
					+ "#define HIGH highp\n"
					+ "precision mediump float;\n" 
					+ "#else\n"
					+ "#define MED\n"
					+ "#define LOWP\n"
					+ "#define HIGH\n" 
					+ "#endif\n"
					+ "uniform sampler2D u_texture;\n"
					+ "varying MED vec2 v_texCoords;\n"
					+ "varying LOWP vec4 v_color;\n"
					+ "void main() {\n"
					+ "   vec4 texel = v_color * texture2D(u_texture, v_texCoords);\n"
					+ "   if(texel.a < 0.1) {\n"
					+ "		  discard;\n"
					+ "   } else {\n"
					+ "		  gl_FragColor = texel;\n"
					+ "   }\n"		
					+ "} \n";
	
	// SHAPE RENDERER
	
	private static final String shapeVertexShader =

					  "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
					+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE    + ";\n"
					+ "uniform mat4 u_projTrans;\n"
					+ "varying vec4 v_col;\n"
					+ "void main() {\n" 
					+ "   gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
					+ "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" 
					+ "   gl_PointSize = 1.0;\n"
					+ "}";

	private static final String shapeFragmentShader = 

					  "#ifdef GL_ES\n"
					+ "#define LOWP lowp\n"
					+ "#define MED mediump\n"
					+ "#define HIGH highp\n"
					+ "precision mediump float;\n" 
					+ "#else\n"
					+ "#define MED\n"
					+ "#define LOWP\n"
					+ "#define HIGH\n" 
					+ "#endif\n" 
					+ "varying LOWP vec4 v_col;\n"
					+ "void main() {\n"
					+ "   gl_FragColor = v_col;"
					+ "}";	
}
