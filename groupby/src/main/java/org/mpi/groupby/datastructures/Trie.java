/*******************************************************************************
 * Copyright (c) 2015 Dependable Cloud Group and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dependable Cloud Group - initial API and implementation
 *
 * Creator:
 *     Cheng Li
 *
 * Contact:
 *     chengli@mpi-sws.org    
 *******************************************************************************/
package org.mpi.groupby.datastructures;

import org.mpi.groupby.util.DiskIO;
import org.mpi.groupby.util.Role;

/**
 * The Class Trie to store key and value, served as a cache for all keys and part of values.
 */
public class Trie {
	
	/** The root. */
	private TrieNode root;
	
	/** The following three parameters are used to implement the iterator. */
	private TrieNode startOfIterator;
	
	/** The previous key node. */
	private TrieNode previousKeyNode;
	
	/** The last node flushed. */
	private TrieNode lastNodeFlushed;
	
	/** The num of keys. */
	private long numOfKeys;
	
	/** The num of values. */
	private int numOfValues;

    /**
     * Instantiates a new trie.
     */
    public Trie() {
        root = new TrieNode('0', Role.ROOT);
        this.setNumOfKeys(0);
        this.setNumOfValues(0);
    }

    // Inserts a key, pair into the trie.
    /**
     * Insert a key and value pair. If key is not set, then
     * create a leaf node. If the number of leaf node we processed
     * over a limit (DiskIO.BATCH_SIZE_TRIGGER_MEM_CHECK), we read
     * the current memory usage, if the free memory is very little,
     * then we decide to flush some data to disk. In particular,
     * we flush the value lists for a range of keys to disk, and clear
     * all these value lists in memory. Like this, the trie instance more
     * likes a cache for keys.
     *
     * @param key the key
     * @param value the value
     */
    public void insert(String key, String value) {
        TrieNode curr = root;
        for(int i = 0; i < key.length() - 1; i++){
            char c = key.charAt(i);
            if(curr.getChild(c) == null){
                curr.setChild(c, new TrieNode(c, Role.INTERNAL));
            }
            curr = curr.getChild(c);
        }
        
        //find the position to add the key and value pair
        char lastChar = key.charAt(key.length() - 1);
        TrieNode leafNode = curr.getChild(lastChar);
        if(leafNode == null){
        	leafNode = new TrieNode(lastChar, Role.LEAF);
        	leafNode.setKeyStr(key);
        	curr.setChild(lastChar, leafNode);
        	
            //mark the first key value pair as the start of the iterator
            if(this.getStartOfIterator() == null){
            	this.setStartOfIterator(leafNode);
            }
            
        	if(this.previousKeyNode != null){
        		this.previousKeyNode.setNextTrieNode(leafNode);
        	}
        	this.previousKeyNode = leafNode;
        	numOfKeys++;
        }
        leafNode.addValueStr(value);
        numOfValues++;
        
        if(numOfValues == DiskIO.BATCH_SIZE_TRIGGER_MEM_CHECK){
        	if(DiskIO.isTimeToFlush()){
        		//flush into disk
        		if(this.lastNodeFlushed == null){
        			this.lastNodeFlushed = this.startOfIterator;
        		}
        		
        		DiskIO.flushToDisk(this.lastNodeFlushed, computeNumKeyToFlush(this.lastNodeFlushed, numOfValues));
        	}
        	numOfValues = 0;
        }
    }
    
    /**
     * Compute num key to flush.
     *
     * @param start the start
     * @param numOfValues the num of values
     * @return the int
     */
    private int computeNumKeyToFlush(TrieNode start, int numOfValues){
    	int numOfKey = 0;
    	TrieNode curr = start;
    	while(numOfValues > 0){
    		numOfValues -= curr.getNumOfValues();
    		numOfKey++;
    	}
    	return numOfKey;
    }

    // Returns if the key is in the trie.
    /**
     * Contains key.
     *
     * @param word the word
     * @return true, if successful
     */
    public boolean containsKey(String word) {
        TrieNode curr = root;
        for(int i = 0; i < word.length(); i++){
            char c = word.charAt(i);
            if(curr.getChild(c) == null){
                return false;
            }
            curr = curr.getChild(c);
        }
        
        if(curr.isLeaf()){
            return true;
        }
        return false;
    }

	/**
	 * Gets the start of iterator.
	 *
	 * @return the start of iterator
	 */
	public TrieNode getStartOfIterator() {
		return startOfIterator;
	}

	/**
	 * Sets the start of iterator.
	 *
	 * @param startOfIterator the new start of iterator
	 */
	public void setStartOfIterator(TrieNode startOfIterator) {
		this.startOfIterator = startOfIterator;
	}

	/**
	 * Gets the num of keys.
	 *
	 * @return the num of keys
	 */
	public long getNumOfKeys() {
		return numOfKeys;
	}

	/**
	 * Sets the num of keys.
	 *
	 * @param numOfKeys the new num of keys
	 */
	public void setNumOfKeys(long numOfKeys) {
		this.numOfKeys = numOfKeys;
	}

	/**
	 * Gets the num of values.
	 *
	 * @return the num of values
	 */
	public int getNumOfValues() {
		return numOfValues;
	}

	/**
	 * Sets the num of values.
	 *
	 * @param numOfValues the new num of values
	 */
	public void setNumOfValues(int numOfValues) {
		this.numOfValues = numOfValues;
	}

	/**
	 * Gets the last node flushed.
	 *
	 * @return the last node flushed
	 */
	public TrieNode getLastNodeFlushed() {
		return lastNodeFlushed;
	}

	/**
	 * Sets the last node flushed.
	 *
	 * @param lastNodeFlushed the new last node flushed
	 */
	public void setLastNodeFlushed(TrieNode lastNodeFlushed) {
		this.lastNodeFlushed = lastNodeFlushed;
	}
	
	public TrieIterator iterator(){
		return new TrieIterator(this);
	}
}
