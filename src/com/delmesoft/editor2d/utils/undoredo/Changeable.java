package com.delmesoft.editor2d.utils.undoredo;

public interface Changeable<T> {
	
	T copy();
	
	T set(T e);
	
}
