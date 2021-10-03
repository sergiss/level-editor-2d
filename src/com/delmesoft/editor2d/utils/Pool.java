package com.delmesoft.editor2d.utils;

public abstract class Pool<T> {
	
	public T[] items; // free elements
	public int size;  // free count
	
	@SuppressWarnings("unchecked")
	public Pool() {
		items = (T[]) new Object[8];
	}

	abstract protected T newObject();

	public T obtain() {

		T e;

		if (size == 0) {
			e = newObject();
		} else {
			e = items[--size];
		}

		return e;
	}

	@SuppressWarnings("unchecked")
	public void free(T e) {

		if (size == items.length) {
			T[] tmp = (T[]) java.lang.reflect.Array.newInstance(items.getClass().getComponentType(), (int) (size * 1.78f));
			System.arraycopy(items, 0, tmp, 0, size);
			items = tmp;
		}
		
		items[size++] = e;
	}

	public void clear() {

		for (int i = 0, n = size; i < n; i++)
			items[i] = null;
		size = 0;

	}

}
