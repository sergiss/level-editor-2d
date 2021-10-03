package com.delmesoft.editor2d.math;

public class MathHelper {
	
	public static final float HALF_PI = (float) (Math.PI / 2.0);
	
	public static final float byteToMega = 0.0000009536743164f;

	public static int fastFloor(float x) {
		int xi = (int) x;
		return x < xi ? xi - 1 : xi;
	}
	
	public static float interpolateLinear(float scale, float startValue, float endValue) {
		
		if (startValue == endValue || scale <= 0f) {
			return startValue;
		}

		if (scale >= 1f) {
			return endValue;
		}

		return (1f - scale) * startValue + (scale * endValue);
	}
	
	public static int moduloInterval(int value, int start, int end) {
		
		int diff = end - start;
		
		value = (value - start) % diff;
		
		if(value < 0) {
			value += diff;
		}
		
		return value + start;
		
	}
}
