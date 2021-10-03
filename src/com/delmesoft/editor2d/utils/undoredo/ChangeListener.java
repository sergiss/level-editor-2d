package com.delmesoft.editor2d.utils.undoredo;

public interface ChangeListener<T> {
	
	void nextChange(T e);

}
