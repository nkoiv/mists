/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.util;

/**
    * A simple sorted list for Comparable elements
    * rewritten to skip the use of java collections and to use QuickSort
    * @author nkoiv
    * @param <E> Only Comparable objects can be placed on the list, else Sort wouldnt work
    */
    public class SortedList<E extends Comparable> {
        /** The list of elements */
        protected E[] data; //the elements stored in the SortedList 
        protected int capacity; //Max capacity of the list
        static final int def_cap = 50; //default capacity for a new list
        protected int num; //ID of the next element to add to the list
        
        /**
         * Constructor with specified capacity creates a list with that (max) size
         * If the specified size is smaller than 1, its set to 1;
         * @param capacity The size to start the list with
         */
        public SortedList(int capacity) {
            if (capacity < 1) {
                this.capacity = 1;
            } else {
                this.capacity = capacity;
            }
            this.num = 0; //start from 0
            data = (E[])new Comparable[capacity]; //initialize the array
        }
        
        /**
         * Constructor with no parameters makes a SortedList
         * with the default size of (def_cap = 50)
         */
        public SortedList() {
            this(def_cap);
        }
        
        public void addWithoutSorting(E e) {
            if (num >= capacity) { //We're over our capacity!
                //Make a new array with the capacity+def_cap;
                E[] newElementArray = (E[]) new Comparable[capacity+def_cap];
                //copy the old array over
                //TODO: consider using System.Arraycopy (is it okay on tiralab)
                for (int i = 0; i < capacity; i++) {
                    newElementArray[i] = data[i];
                }
                //Replace the old array with the new one
                this.capacity = capacity+def_cap;
                data = newElementArray;
            }
            this.data[num] = e;
            this.num++;
            //System.out.println("Added element, size is now "+num);
            
        }
        
        /**
         * Adds a element to the end of the list (position num)
         * and moves the position on the list by one
         * If the array would go over its capacity, the array is extended by def_cap(50)
         * @param n The element to add to the list
         */
        public void add(E e) {
            this.addWithoutSorting(e);
            this.quickSort(this.data, 0, num-1);
        }
        /**
         * Retrieve a element from the list from the specified position
         * Note that the list is sorted by the element value.
         * @param index Index number of the element to retrieve
         * @return The desired element
         * @throws Exception Trying to retrieve element that's not on the list
         */
        public E get(int index) throws Exception {
            if (index < 0 || index >= num) {
                throw new Exception("Element index out of list bounds");
            }
            return data[index];
        }
        
        /**
         * Forced sorting for the list
         * The List should sort itself at every addition
         * and order shouldnt change at removals, making this
         * forced sort (probably) redundant
         */
        
        public void quickSort() {
            quickSort(data, 0, (num-1));
        }
        
        /**
         * Quicksort the data-array
         * (https://en.wikipedia.org/wiki/Quicksort)
         */
        private void quickSort(E[] array, int low, int high) {
            //System.out.println("Starting quicksort");
            if (low < high) {
                int i = low, j =high;
                //split the array in half and pick the middlemost element
                //System.out.println("Pivot at "+((i+j)/2));
                E pivot = array[(i+j)/2];
                
                do { //Do so that everything is executed at least once
                    //Close in on the position
                    while(array[i].compareTo(pivot) <0) i++;
                    while(pivot.compareTo(array[j]) <0) j--;
                    
                    if ( i<=j) {
                        E temp = array[i];
                        array[i] = array[j];
                        array[j] = temp;
                        i++;
                        j--;
                    }
                } while (i <= j);
                quickSort(array, low, j);
                quickSort(array, i, high);
            
            }
        }
        
        /**
         * Retrieve the first element from the list
         *  
         * @return The first element from the list
         */
        public E first() {
                return data[0];
        }
        
        /**
         * Return the index of the selected element
         * if the element can't be found, return -1
         * @param n The element to look for
         * @return index of the element, -1 if it's not found
         */
        private int getIndex(E e) {
            for (int i = 0; i < num -1; i++) {
                if (data[i].equals(e)) return i;
            }
            return -1;
        }
        
        /**
         * Empty the list
         */
        public void clear() {
                data = (E[])new Comparable[capacity];
                this.num = 0;
        }


        /**
         * Remove an Element from the list
         * Calls the remove(int index) -method after looking up the element with
         * the private getIndex -method
         * @param n The element to remove
         */
        public void remove(E e) {
            int eIndex = this.getIndex(e);
            if (eIndex>=0) this.remove(eIndex);
        }
        
        /**
         * Remove the selected Element from the array by recreating it
         * @param index The index of the Element to remove
         */
        public void remove(int index) {
            //System.out.println("Starting removal of "+index+". Num at "+num);
            if (index<num) {
                //Make a new array with the same capacity
                E[] newElementArray = (E[])new Comparable[capacity];
                //copy the old array over with the exception of the selected index
                int j = 0;
                for (int i = 0; i < num; i++) {
                    if (i!=index) {//Dont copy over the element we're removing
                        newElementArray[j] = data[i];
                        //System.out.println("Copied over element at "+i+" ("+newElementArray[j].toString()+")");
                        j++;
                    }
                }
                //Replace the old array with the new one
                data = newElementArray;
                //Reduce num by one, since we've shrinked the array by one
                this.num--;
                //System.out.println("Removed element "+index+", size now "+(num-1));
            }
        }

        /**
         * Get the number of elements in the list
         * @return The number of element in the list
         */
        public int size() {
                return (num);
        }
        /**
         * Get the capacity of the list
         * @return The current capacity of the list
         */
        public int capacity() {
            return capacity;
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
        

        @Override
        public String toString() {
            String description = "SL: size:"+num+", Capacity:"+capacity+" E's: ";
            for (int i = 0; i < (this.num-1);i++) {
                description = description + "["+this.data[i].toString()+"],";
            }
            return description;
        }
    }
	

