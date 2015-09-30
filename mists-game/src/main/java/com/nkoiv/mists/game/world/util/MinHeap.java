/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.util;

import java.util.Arrays;


/**
 * Since pathfinding only really needs to poll/peek
 * the lowest value object in the list (thanks to nodemap),
 * minheap should suffice.
 * @author nikok
 */
public class MinHeap<E extends Comparable> {
    private E[] data;
    private final int def_cap = 50;
    private int maxSize;
    private int currentSize;
    
    public MinHeap() {
        data = (E[])new Comparable[def_cap];
        currentSize = 0;
        maxSize = data.length -1;
    }
    
    /**
    * Add an element to the heap
    * TODO: Figure if it matters that we're wasting space
    * because the first element is stored at data[1];
    * @param e the element to add
    */
    public void add(E e) {
        currentSize++; 
        if (currentSize >= maxSize) grow();
        data[currentSize] = e; //add the element to the bottom
        swim(currentSize); //and swim it up from the current position
    }
    
    public void remove(E e) {
        if (currentSize ==0) return; //nothing to remove if heap is empty
        if (currentSize ==1) clear(); //clear the node when removing last node
        else {
        for (int i = 1; i <= currentSize; i++) {
            //System.out.println("Checking i "+i );
            if (data[i] == e) {
                //System.out.println("Found "+e+" at "+i);
                if (i == currentSize) {
                    //this was the last node - safe to null.
                    //no further action needed
                    data[i] = null;
                    currentSize--;
                    break;
                } 
                //Node was removed from inbetween, so restructuring is needed
                data[i] = data[currentSize]; //Override this node with last node
                //data[currentSize] = null; //Nulling the data is perhaps unneeded, we never check anything beyond currentSize
                currentSize--;
                //System.out.println("Comparing "+i+" to "+(i/2));
                //System.out.println("Comparing "+data[i]+" to "+data[parent(i)]);
                if (i == 1) { //Always need to sink if we removed the very first node
                    sink(i);
                }
                else if (data[i].compareTo(data[parent(i)]) >= 0) {
                    sink(i);
                } else {
                    
                    swim(i);
                }
                break;
            }
        }
        }
    }
    
    /**
     * Heapify the array from i upwards, to the top
     * @param i 
     */
    private void swim(int i) {
        //System.out.println("Swimming up from "+i);
        int bottom = i;
        while (i > 1) {
            if ((data[i].compareTo(data[parent(i)])) >= 0) {
                break; //We're at right height
            } else {
                bottom = parent(i);
                swap(i, bottom); //swap the positions
                swim(bottom);
            }
        }
    }
    
    /**
     * Heapify the array from i downwards, to the bottom
     * @param i 
     */
    private void sink(int i) {
        int current = i;
        if ((leftChild(i) <= currentSize) && (data[leftChild(i)].compareTo(data[current]) < 0)) {
            current = leftChild(i);
        }
        if ((rightChild(i) <= currentSize) && (data[rightChild(i)].compareTo(data[current]) < 0)) {
            current = rightChild(i);
        }
        if (current != i) {
            swap(i, current); //swap the positions
            sink(current);
        }
    }

    /**
     * Grow up by doubling current size
     */
    private void grow () {
        int newSize = (currentSize) *2;
            E[] newElementArray = (E[]) new Comparable[newSize];
            for (int i = 0; i < currentSize; i++) {
                newElementArray[i] = data[i];
            }
            data = newElementArray;
            maxSize = data.length -1;
    }
    
    public E first() {
        return data[1];
    }
    
    public void clear() {
        data = (E[])new Comparable[def_cap];
        maxSize = def_cap;
        currentSize = 0;
    }
    
    public int size() {
        return currentSize;
    }
    
    public boolean isEmpty() {
        return (currentSize == 0);
    }
    
    //Helper methods to make code clearer
    //-----------------------------------
    private int parent(int pos)
    {
        return (pos / 2);
    }
 
    private int leftChild(int pos)
    {
        return (2 * pos);
    }
 
    private int rightChild(int pos)
    {
        return (2 * pos) + 1;
    }
    
    private void swap (int pos1, int pos2) {
        E temp = data[pos1];
        data[pos1] = data[pos2];
        data[pos2] = temp;
    }
    //----------------------------------
    
    
    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}
