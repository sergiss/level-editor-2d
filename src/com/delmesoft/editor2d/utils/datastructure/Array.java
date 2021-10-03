package com.delmesoft.editor2d.utils.datastructure;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class Array <T> implements Iterable<T> {
	
	public T[] items;
	public int size;

	public Array() {
		this(8);
	}
	
	@SuppressWarnings("unchecked")
	public Array(int initialCapacity) {
		items = (T[]) new Object[initialCapacity];
	}

	@SuppressWarnings("unchecked")
	public Array(Array<T> array) {
		
		size = array.size;
		
		T[] tmp = (T[]) java.lang.reflect.Array.newInstance(array.items.getClass().getComponentType(), size);
		System.arraycopy(array.items, 0, tmp, 0, size);
		
		items = tmp;	
		
	}

	public void add(T e) {

		if (size == items.length) {
			resize(Math.max(8, (int) (size * 1.75f)));
		}

		items[size++] = e;
	}

	public T get(int index) {
		return items[index];
	}
	
	public void set(int index, T e) {
		items[index] = e;		
	}
	
	public T removeIndex(int index) {
		
		T e = items[index];
		size--;
		System.arraycopy(items, index + 1, items, index, size - index);
		items[size] = null;
		
		return e;
	}
	
	public T removeIndexFast(int index) {
		
		T e = items[index];
		size--;
		items[index] = items[size];
		items[size] = null;
		
		return e;
	}
	
	public boolean removeValue(T e) {
		
		final T[] items = this.items;

		int i = size - 1;

		while (i > -1) {
			if (items[i] == e) {
				removeIndex(i);
				return true;
			}
			i--;
		}

		return false;
	}
	
	public boolean contains(T e) {
		final T[] items = this.items;

		int i = size - 1;

		while (i > -1) {
			if (items[i] == e) {
				return true;
			}
			i--;
		}

		return false;
	}
	
	public boolean containsEquals(T e) {
		
		final T[] items = this.items;

		int i = size - 1;

		while (i > -1) {
			if (e.equals(items[i])) {
				return true;
			}
			i--;
		}

		return false;
	}

	public int indexOf(T e) {

		final T[] items = this.items;

		int i = 0;
		int n = size;

		while (i < n) {
			if (items[i] == e) {
				return i;
			}
			i++;
		}

		return -1;
	}
	
	public int indexOfEquals(T e) {

		final T[] items = this.items;

		int i = 0;
		int n = size;

		while (i < n) {
			if (e.equals(items[i])) {
				return i;
			}
			i++;
		}

		return -1;
	}
	
	public int lastIndexOf(T e) {

		final T[] items = this.items;

		int i = size - 1;

		while (i > -1) {
			if (items[i] == e) {
				return i;
			}
			i--;
		}

		return -1;
	}
	
	public int lastIndexOfEquals(T e) {

		final T[] items = this.items;

		int i = size - 1;

		while (i > -1) {
			if (e.equals(items[i])) {
				return i;
			}
			i--;
		}

		return -1;
	}
		
	public T pop() {
		
		size--;
		
		T e = items[size];
		items[size] = null; // GC ;)
		
		return e;
	}
	
	@SuppressWarnings("unchecked")
	private T[] resize(int newCapacity) {
		
		T[] tmp = (T[]) java.lang.reflect.Array.newInstance(items.getClass().getComponentType(), newCapacity);
		System.arraycopy(items, 0, tmp, 0, size);
		
		return items = tmp;
		
	}
	
	@SuppressWarnings("unchecked")
	public void shrink() {

		if(size < items.length) {

			T[] tmp = (T[]) java.lang.reflect.Array.newInstance(items.getClass().getComponentType(), size);
			System.arraycopy(items, 0, tmp, 0, size);

			items = tmp;

		}

	}
	
	public void addAll(Array<? extends T> array) {
		addAll(array, 0, array.size);
	}

	public void addAll(Array<? extends T> array, int start, int count) {
		addAll((T[]) array.items, start, count);
	}
	
	public void addAll(T[] array, int start, int count) {
		
		T[] items = this.items;
		int sizeNeeded = size + count;
		if (sizeNeeded > items.length) items = resize(Math.max(8, (int)(sizeNeeded * 1.75f)));
		System.arraycopy(array, start, items, size, count);
		size += count;
		
	}
	public void insert(int index, T e) {
		
		if (size == items.length) {
			resize(Math.max(8, (int) (size * 1.75f)));
		}
		
		if(index == size) {
			items[size++] = e;
		} else {
			
			System.arraycopy(items, index, items, index + 1, size - index);
			
			size++;
			items[index] = e;
		}
		
	}
	
	public void insertAll(Array<? extends T> array) {
		insertAll(array, 0, array.size);
	}

	public void insertAll(Array<? extends T> array, int start, int count) {
		insertAll((T[]) array.items, start, count);
	}

	@SuppressWarnings("unchecked")
	public void insertAll(T[] array, int start, int count) {
	
		int sizeNeeded = size + count;			
		T[] items = (T[]) new Object[sizeNeeded];
		System.arraycopy(array, start, items, 0, count);
		System.arraycopy(this.items, 0, items, count, size);
		this.items = items;
		size += count;
	}
	
	public void sort() {
		Arrays.sort(items, 0, size);		
	}
	
	public void sort(int fromIndex, int toIndex) {
		Arrays.sort(items, fromIndex, toIndex);		
	}
	
	public void sort(Comparator<? super T> comparator) {
		Arrays.sort(items, 0, size, comparator);		
	}
	
	public void sort(int fromIndex, int toIndex, Comparator<? super T> comparator) {
		Arrays.sort(items, fromIndex, toIndex, comparator);		
	}
	
	public void reverse () {
		T[] items = this.items;
		for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
			int ii = lastIndex - i;
			T temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}
	
	public void trim() {
		if (size < items.length) {
			resize(size);
		}
	}
	
	@SuppressWarnings("unchecked")
	public T[] toArray () {
		return (T[])toArray(items.getClass().getComponentType());
	}

	@SuppressWarnings("unchecked")
	public <V> V[] toArray (Class<?> type) {
		V[] result = (V[]) java.lang.reflect.Array.newInstance(type, size);
		System.arraycopy(items, 0, result, 0, size);
		return result;
	}
		
	public void clear() {
		
		final T[] items = this.items;
		
		while(size > 0) {
			items[--size] = null; // GC ;)
		}
		
	}

	@Override
	public Iterator<T> iterator() {
		
		return new ArrayIterator();
	}
	
	class ArrayIterator implements Iterator<T> {
		
		int index;

		@Override
		public boolean hasNext() {
			return index < size;
		}

		@Override
		public T next() {
			return items[index++];
		}
		
		@Override
		public void remove() {
			removeIndex(--index);
		}
		
	}


}
