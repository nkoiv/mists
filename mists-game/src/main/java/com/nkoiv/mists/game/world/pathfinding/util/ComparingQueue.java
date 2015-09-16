/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding.util;

/**
 *
 * @author daedra
 */
//Comparing queueu

public class ComparingQueue<E extends Comparable> {
	private Comparable[] queue;
	private static final int def_cap = 10;
	
	private int size = 0;
	
	public ComparingQueue() {
		this(def_cap);
		
	}
	
	public ComparingQueue(int startCap) {
		this.queue = new Comparable[def_cap];
		
	}
	/**
	* add checks the array size and uses findSpot
	* to place the Element after it's ensured the
	* array can fit it.
	* @param e The element to add to the queue
	*/
	public boolean add(E e) {
		int i = this.size;
		if (i >= queue.length) {
			enlargeArray();
		}
		size = i +1;
		if (size == 0) queue[0] = e;
		else findSpot(i,e);
		this.size++;
		return true;
	}
	/**
	* findSpot rummages through the array, checking if anyone is
	* better (compares 1 or 0) than the given E, and then places
	* the E at the right spot.
	* @param position Position in queue we're up to
	* @param e the Element to add to the queue
	*/
	public void findSpot(int position, E e) {
		Comparable<? super E> key = (Comparable<? super E>) e;
		while (position > 0 ) {
			int parentPos = (position -1) >>> 1;
			Comparable p = queue[parentPos];
			  if (key.compareTo((E) p) >= 0)
				break;
				queue[position] = p;
				position = parentPos;
		}
		//If this wasnt better than anyone else, it goes to last
		queue[position] = e;
	}
	
	/*
	* Enlarge the size of the array by the default cap
	*/
	private void enlargeArray() {
		int newSize = queue.length + def_cap;
		E[] newElementArray = (E[]) new Comparable[newSize];
		for (int i = 0; i < size; i++) {
                    newElementArray[i] = queue[i];
		}
		this.queue = newElementArray;
	}
	
	private int size() {
		return size;
	}

	public void remove(int position) {
		int s = --size;
		if (s == position) queue[position] = null; //last spot is safe to null
		else {
			//look at what we're removing - shift everything after it down one step.
			//reduce the size of the queue by one (to s)
		}
	}
	
}

