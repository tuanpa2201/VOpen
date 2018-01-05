package base.util;

import java.util.LinkedList;

/**
 * 
 * @author VuD
 */
public class LinkedListSyn<T> {
	private final LinkedList<T> lstValue;

	public LinkedListSyn() {
		super();
		this.lstValue = new LinkedList<>();
	}

	public LinkedList<T> getLstValue() {
		return lstValue;
	}

	public synchronized T get(int index) {
		return lstValue.get(index);
	}

	public synchronized boolean add(T t) {
		if (t == null) {
			return false;
		}
		return lstValue.add(t);
	}

	public synchronized T remove(int index) {
		return lstValue.remove(index);
	}

	public synchronized boolean remove(T t) {
		return lstValue.remove(t);
	}

	public synchronized T removeFirst() {
		return lstValue.removeFirst();
	}

	public synchronized T removeLast() {
		return lstValue.removeLast();
	}

	public synchronized boolean contains(T t) {
		return lstValue.contains(t);
	}

	public synchronized int size() {
		return lstValue.size();
	}

	@SuppressWarnings("unchecked")
	public synchronized LinkedList<T> clone() {
		LinkedList<T> lstResult = null;
		try {
			Object object = lstValue.clone();
			lstResult = (LinkedList<T>) object;
		} catch (Exception e) {
			e.printStackTrace();
			lstResult = null;
		}
		return lstResult;
	}

}
