package com.delmesoft.editor2d.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class RenderContext {
	
	public TextureBinder textureBinder;
	
	private boolean blending;
	private int blendSFactor;
	private int blendDFactor;
	private int depthFunc;
	private float depthRangeNear;
	private float depthRangeFar;
	private boolean depthMask;
	private int cullFace;
	
	public RenderContext() {
		textureBinder = new TextureBinder();
	}
	
	public void begin() {
		
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		depthFunc = 0;
		Gdx.gl.glDepthMask(true);
		depthMask = true;
		Gdx.gl.glDisable(GL20.GL_BLEND);
		blending = false;
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		cullFace = 0;
		blendSFactor = 0;
		blendDFactor = 0;
		
		textureBinder.begin();
		
	}	

	public void setDepthMask (final boolean depthMask) {
		
		if (this.depthMask != depthMask) {
			
			this.depthMask = depthMask;
			Gdx.gl.glDepthMask(depthMask);			
		}		
	}

	public void setDepthTest (final int depthFunction) {
		setDepthTest(depthFunction, 0f, 1f);
	}

	public void setDepthTest (final int depthFunction, final float depthRangeNear, final float depthRangeFar) {
					
		if(depthFunction != 0) { // enabled
			
			if(depthFunc == 0 || this.depthRangeNear != depthRangeNear || this.depthRangeFar != depthRangeFar) {
				
				this.depthRangeNear = depthRangeNear;
				this.depthRangeFar  = depthRangeFar;
				
				Gdx.gl.glDepthRangef(depthRangeNear, depthRangeFar);
				
			}
							
			if (depthFunc != depthFunction) {
				depthFunc = depthFunction;
				
				Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
				Gdx.gl.glDepthFunc(depthFunction);
				
			}
			
		} else if (depthFunc != depthFunction) {
			
			depthFunc = depthFunction;
			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

		}
		
	}

	public void setBlending (final boolean enabled, final int sFactor, final int dFactor) {
		
		if(enabled) {
			
			if(enabled != blending) {
				blending = enabled;
				Gdx.gl.glEnable(GL20.GL_BLEND);
			}
			
			if (blendSFactor != sFactor || blendDFactor != dFactor) {
				
				Gdx.gl.glBlendFunc(sFactor, dFactor);
				
				blendSFactor = sFactor;
				blendDFactor = dFactor;
				
			}
			
		} else {
			
			if(enabled != blending) {
				blending = enabled;
				Gdx.gl.glDisable(GL20.GL_BLEND);
			}
			
		}
				
	}

	public void setCullFace (final int face) {
		
		if (face != cullFace) {
			
			cullFace = face;
			
			if (face == GL20.GL_FRONT || face == GL20.GL_BACK || face == GL20.GL_FRONT_AND_BACK) {
				
				Gdx.gl.glEnable(GL20.GL_CULL_FACE);
				Gdx.gl.glCullFace(face);
				
			} else {
				
				Gdx.gl.glDisable(GL20.GL_CULL_FACE);
				
			}
			
		}
		
	}
	
	public void end() {
		
		if (depthFunc != 0) Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		if (!depthMask)     Gdx.gl.glDepthMask(true);
		if (blending)       Gdx.gl.glDisable(GL20.GL_BLEND);
		if (cullFace > 0)   Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		
		textureBinder.end();
		
	}
	
}
