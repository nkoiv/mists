/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

/**
     * A simple sorted list for Nodes
     * original idea based on the http://www.cokeandcode.com/main/tutorials/path-finding/ (Kevin Glass)
     * rewritten to skip the use of java collections and to use QuickSort
     * @author nkoiv
     */
    public class SortedNodeList {
        /** The list of elements */
        //private ArrayList<Node> list = new ArrayList();
        private Node[] data; //the nodes stored in the SortedNodeList 
        private int capacity; //Max capacity of the list
        static final int def_cap = 50; //default capacity for a new list
        private int num; //Current size of the list
        
        /**
         * Constructor with specified capacity creates a list with that (max) size
         * If the specified size is smaller than 1, its set to 1;
         * @param capacity The size to start the list with
         */
        public SortedNodeList(int capacity) {
            if (capacity < 1) {
                this.capacity = 1;
            } else {
                this.capacity = capacity;
            }
            this.num = 0; //start from 0
            this.data = new Node[capacity]; //initialize the array
        }
        
        /**
         * Constructor with no parameters makes a SortedNodeList
         * with the default size of (def_cap = 50)
         */
        public SortedNodeList() {
            this(def_cap);
        }
        
        /**
         * Adds a node to the end of the list (position num)
         * and moves the position on the list by one
         * If the array would go over its capacity, the array is extended by def_cap(50)
         * @param n The Node to add to the list
         */
        public void add(Node n) {
            if (num >= capacity) { //We're over our capacity!
                //Make a new array with the capacity+def_cap;
                Node[] newNodeArray = new Node[capacity+def_cap];
                //copy the old array over
                //TODO: consider using System.Arraycopy (is it okay on tiralab)
                for (int i = 0; i < capacity; i++) {
                    newNodeArray[i] = data[i];
                }
                //Replace the old array with the new one
                this.capacity = capacity+def_cap;
                data = newNodeArray;
            }
            this.data[num] = n;
            this.num++;
            //System.out.println("Added node, size is now "+num);
            //this.quickSort(this.data, 0, num-1);
        }
        /**
         * Retrieve a node from the list from the specified position
         * Note that the list is sorted by the node value.
         * @param index Index number of the node to retrieve
         * @return The desired node
         * @throws Exception Trying to retrieve node that's not on the list
         */
        public Node get(int index) throws Exception {
            if (index < 0 || index >= num) {
                throw new Exception("Node index out of list bounds");
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
        private void quickSort(Node[] array, int low, int high) {
            //System.out.println("Starting quicksort");
            if (low < high) {
                int i = low, j =high;
                //split the array in half and pick the middlemost node
                //System.out.println("Pivot at "+((i+j)/2));
                Node pivot = array[(i+j)/2];
                
                do { //Do so that everything is executed at least once
                    //Close in on the position
                    while(array[i].compareTo(pivot) <0) i++;
                    while(pivot.compareTo(array[j]) <0) j--;
                    
                    if ( i<=j) {
                        Node temp = array[i];
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
        public Node first() {
                return data[0];
        }
        
        /*
        * Return the first node on the list that matches given X and Y
        */
        public Node get(int x, int y) {
            for (int i = 0; i< num;i++) {
                if (data[i].getX() == x && data[i].getY() == y) {
                    return data[i];
                }
            }
            return null;
        }
        
        /**
         * Return the index of the selected node
         * if the node can't be found, return -1
         * @param n The node to look for
         * @return index of the node, -1 if it's not found
         */
        private int getIndex(Node n) {
            for (int i = 0; i < num -1; i++) {
                if (data[i].equals(n)) return i;
            }
            return -1;
        }
        
        /**
         * Empty the list
         */
        public void clear() {
                data = new Node[capacity];
                this.num = 0;
        }


        /**
         * Remove a Node from the list
         * Calls the remove(int index) -method after looking up the node with
         * the private getIndex -method
         * @param n The node to remove
         */
        public void remove(Node n) {
            int nodeIndex = this.getIndex(n);
            if (nodeIndex>=0) this.remove(nodeIndex);
        }
        
        /**
         * Remove the selected Node from the array by recreating it
         * @param index The index of the Node to remove
         */
        public void remove(int index) {
            //System.out.println("Starting removal of "+index+". Num at "+num);
            if (index<=num) {
                //Make a new array with the same capacity
                Node[] newNodeArray = new Node[capacity];
                //copy the old array over with the exception of the selected index
                int j = 0;
                for (int i = 0; i < num; i++) {
                    if (i!=index) {//Dont copy over the node we're removing
                        newNodeArray[j] = data[i];
                        //System.out.println("Copied over node at "+i+" ("+newNodeArray[j].toString()+")");
                        j++;
                    }
                }
                //Replace the old array with the new one
                data = newNodeArray;
                //Reduce num by one, since we've shrinked the array by one
                this.num--;
                //System.out.println("Removed node "+index+", size now "+(num-1));
            }
        }

        /**
         * Get the number of elements in the list
         * 
         * @return The number of element in the list
         */
        public int size() {
                return (num-1);
        }
        /**
         * Get the capacity of the list
         * @return The current capacity of the list
         */
        public int capacity() {
            return capacity;
        }

        /**
         * Check if a node is in the list
         * 
         * @param n The node to search for
         * @return True if the element is in the list
         */
        public boolean contains(Node n) {
            int nodeIndex = this.getIndex(n);
            if (nodeIndex >= 0) {
                return true;
            } else {
                return false;
            }
        }
        
        /*
        * Check if the list contains a node with the given coordinates
        */
        public boolean contains(int x, int y) {
            //System.out.println("Checking if list contains "+x+","+y);
            if (num==0) return false;
            for (int i = 0; i < num-1; i++) {
                //System.out.println("Currently at data["+i+"], num is"+num);
                if (data[i].getX()==x && data[i].getY() == y) return true;
            }
            return false;   
        }
        
        @Override
        public String toString() {
            String description = "SortedNodeList: Num:"+num+", Capacity:"+capacity+" Nodes: ";
            for (int i = 0; i < (this.num-1);i++) {
                description = description + " "+this.data[i].toString()+" |";
            }
            return description;
        }
    }
	

