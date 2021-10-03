package com.delmesoft.editor2d.utils;

public class ArrayUtils {
	
	private static Object lock = new Object();
	
	private static class LinkedPoint {
		int x, y;
		LinkedPoint child;
		public LinkedPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private static LinkedPoint head, tail;
	private static int size;

	public static void floodfill(int[] data, int cols, int rows, int x, int y, int value) {
		
		int index = x * rows + y;
		int ref = data[index];

		if (ref != value) {

			synchronized (lock) {

				data[index] = value;
				add(new LinkedPoint(x, y));

				LinkedPoint point;

				do {

					point = poll();

					x = point.x;
					y = point.y;

					if (x + 1 < cols) {
						index = (x + 1) * rows + y;
						if (data[index] == ref) {
							data[index] = value;
							add(new LinkedPoint(x + 1, y));
						}
					}
					if (x - 1 > -1) {
						index = (x - 1) * rows + y;
						if (data[index] == ref) {
							data[index] = value;
							add(new LinkedPoint(x - 1, y));
						}
					}
					if (y + 1 < rows) {
						index = x * rows + (y + 1);
						if (data[index] == ref) {
							data[index] = value;
							add(new LinkedPoint(x, y + 1));
						}
					}
					if (y - 1 > -1) {
						index = x * rows + (y - 1);
						if (data[index] == ref) {
							data[index] = value;
							add(new LinkedPoint(x, y - 1));
						}
					}

				} while (size > 0);

			}

		}

	}

	private static void add(LinkedPoint linkedPoint) {
		if (size == 0) {
			head = linkedPoint;
			tail = head;
		} else {
			tail.child = linkedPoint;
			tail = linkedPoint;
		}
		size++;
	}

	private static LinkedPoint poll() {
		LinkedPoint tmp = head;
		head = head.child;
		size--;
		return tmp;
	}

}