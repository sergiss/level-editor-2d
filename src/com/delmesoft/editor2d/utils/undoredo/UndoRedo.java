package com.delmesoft.editor2d.utils.undoredo;

import com.delmesoft.editor2d.utils.Pool;
import com.delmesoft.editor2d.utils.datastructure.Array;

public class UndoRedo<T extends Changeable<T>> {
	
	protected Node<T> head;
	protected Node<T> current;
	
	protected final Pool<Node<T>> pool;
		
	protected int index;
	
	protected final int maxChanges;
	
	protected ChangeListener<T> changeListener;
	
	public UndoRedo() {
		this(200);
	}
	
	public UndoRedo(int maxChanges) {
		
		this.maxChanges = maxChanges;
		
		pool = new Pool<Node<T>>() {

			@Override
			public UndoRedo<T>.Node<T> newObject() {
				return new Node<T>();
			}
			
		};
				
		head = pool.obtain();
		head.prev = head;

		current = head;
		
	}
	
	public void nextChange() {
		
		if(index > maxChanges) {
						
			final Node<T> node = head;
			head = node.next;
			head.prev = node.prev;
			
			// Recycle nodes
			node.clear();
			pool.free(node);
			
		} else {
			index++;
		}
		
		// Optimize array
		current.elements.trim(); 
		
		Node<T> node = current.next;
		
		// Recycle nodes
		while(node != null) { 
			
			Node<T> tmp = node;
			node = node.next;
			
			tmp.clear();
			pool.free(tmp);
						
		}
		
		node = pool.obtain();
				
		current.next = node;
		node.prev = current;
		head.prev = node;
		
		current = node;
		
	}
	
	public void mem(T e) {
		current.elements.add(new Element<T>(e));
	}
	
	public void setChangeListener(ChangeListener<T> changeListener) {
		this.changeListener = changeListener;
	}
	
	public void removeChangeListener() {
		this.changeListener = null;
	}
	
	public boolean canUndo() {
		return index > 0;
	}
	
	public void undo() {

		if (canUndo()) {

			final Array<Element<T>> elements = current.elements;

			final ChangeListener<T> changeListener = this.changeListener;

			if (changeListener == null) {

				for (int i = 0, n = elements.size; i < n; i++) {

					elements.get(i).swapValues();

				}

			} else {

				for (int i = 0, n = elements.size; i < n; i++) {

					final Element<T> node = elements.get(i);
					node.swapValues();

					changeListener.nextChange(node.b);

				}

			}

			current = current.prev;
			index--;
		}
		
	}
	
	public boolean canRedo() {
		return current.next != null;
	}
	
	public void redo() {
		
		if(canRedo()) {
			
			final Array<Element<T>> elements = current.next.elements;

			final ChangeListener<T> changeListener = this.changeListener;
			if (changeListener == null) {

				for (int i = 0, n = elements.size; i < n; i++) {

					elements.get(i).swapValues();

				}

			} else {

				for (int i = 0, n = elements.size; i < n; i++) {

					final Element<T> node = elements.get(i);
					node.swapValues();

					changeListener.nextChange(node.a);

				}

			}

			current = current.next;
			index++;
			
		}
	}
	
	public void clear() {

		pool.clear();
		
		for(Node<T> node = head; node != null; node = node.next) {
			node.clear();
		}
		
		index = 0;
		
		//changeListener = null;
		
		head = pool.obtain();
		head.prev = head;

		current = head;

	}
	
	@SuppressWarnings("hiding")
	protected class Node<T extends Changeable<T>> {
		
		Node<T> prev;
		Node<T> next;
		
		Array<Element<T>> elements = new Array<Element<T>>();
		
		public void clear() {
			
			prev = next = null;
			elements.clear();
			
		}
						
	}
	
	@SuppressWarnings("hiding")
	protected class Element<T extends Changeable<T>> {
		
		T a; // instance
		T b; // copy
		
		Element(T e) {
			
			a = e;
			b = e.copy();
			
		}
		
		void swapValues() {
			
			final T tmp = a.copy();
			a.set(b);
			b.set(tmp);
			
		}
		
	}

}
