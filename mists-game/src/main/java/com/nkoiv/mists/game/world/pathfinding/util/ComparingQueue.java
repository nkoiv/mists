/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding.util;

/**
 * Using the concept of PriorityQueue, but simplified
 * ComparingQueue looks for the correct spot of the given
 *(Comparable) element when it's added to the queue.
 * There's no need to additional sorting of the list.
 * @author daedra
 */
//Comparing queueu

public class ComparingQueue<E extends Comparable> {
	protected E[] data;
	protected static final int def_cap = 10;
	
	protected int size = 0;
	
	public ComparingQueue() {
		this(def_cap);
		
	}
	
	public ComparingQueue(int startCap) {
		data = (E[])new Comparable[def_cap];
		
	}
	/**
	* add checks the array size and uses findSpot
	* to place the Element after it's ensured the
	* array can fit it.
	* @param e The element to add to the queue
	*/
	public boolean add(E e) {
		int i = this.size;
		if (i >= data.length) {
			enlargeArray();
		}
		if (size == 0) data[0] = e;
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
			E p = data[parentPos];
			  if (key.compareTo((E) p) >= 0)
				break;
				data[position] = p;
				position = parentPos;
		}
		//If this wasnt better than anyone else, it goes to last
		data[position] = e;
	}
	
	/*
	* Enlarge the size of the array by the default cap
	*/
	private void enlargeArray() {
		int newSize = data.length + def_cap;
		E[] newElementArray = (E[]) new Comparable[newSize];
		for (int i = 0; i < size; i++) {
                    newElementArray[i] = data[i];
		}
		data = newElementArray;
	}
	
	public int size() {
		return size;
	}
        
        /**
         * Return the index of the selected element
         * if the element can't be found, return -1
         * @param n The element to look for
         * @return index of the element, -1 if it's not found
         */
        private int getIndex(E e) {
            for (int i = 0; i < size -1; i++) {
                if (data[i].equals(e)) return i;
            }
            return -1;
        }
        
        public void remove(E e) {
            int eIndex = this.getIndex(e);
            if (eIndex>=0) this.remove(eIndex);
        }

	public void remove(int position) {
            int s = --size;
            if (s == position) data[position] = null; //last spot is safe to null
            else {
                //look at what we're removing - shift everything after it down one step.
                //reduce the size of the queue by one (to s)
                for (int i = (position); i < s; i++) {
                    data[i] = data[i+1];
                }
                data[s] = null; //not doing this would result last record being duplicate
            }
            size = s;
	}
        
        /**
         * Empty the list
         */
        public void clear() {
            int oldLength = data.length;
            data = (E[])new Comparable[oldLength];
            this.size = 0;
        }
        
        /**
         * Retrieve the selected element from the queue
         * @param n order-ID of the element to get
         * @return desired element
         */
        public E get(int n) {
            if (n>=0 && n <size) {
                return data[n];
            }
            return null;
        }
        
                /**
         * Check if an Element is in the list
         * 
         * @param n The element to search for
         * @return True if the element is in the list
         */
        public boolean contains(E e) {
            int eIndex = this.getIndex(e);
            if (eIndex >= 0) {
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * Retrieve the first element from the queue
         *  
         * @return The first element from the queue
         */
        public E first() {
            if (this.size>=1) {
                return data[0];
            } else {
                return null;
            }
        }
    
    @Override
    public String toString() {
        String result = "CQ size "+size+": ";
        for (int i = 0; i < this.size; i++) {
            result = result + "["+data[i].toString()+"],";
        }
        return result;
    }
	
}

