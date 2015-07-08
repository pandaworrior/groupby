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

import java.util.ArrayList;
import java.util.List;

import org.mpi.groupby.util.Debug;
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
	
	/** The num of values since last flushing. */
	private int numOfValues;
	
	/** The num of inserts. */
	private long numOfInserts;

    /**
     * Instantiates a new trie.
     */
    public Trie() {
        root = new TrieNode('0', Role.ROOT, null);
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
                curr.setChild(c, new TrieNode(c, Role.INTERNAL, null));
            }
            curr = curr.getChild(c);
        }
        
        //find the position to add the key and value pair
        char lastChar = key.charAt(key.length() - 1);
        TrieNode leafNode = curr.getChild(lastChar);
        if(leafNode == null){
        	leafNode = new TrieNode(lastChar, Role.LEAF, this);
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
        numOfInserts++;
        this.statusOutput();
        this.flushToDisk();
    }
    
    /**
     * Status output.
     */
    private void statusOutput(){
    	if(numOfInserts % 10000 == 0){
    		Debug.printf("Current already inserted %d\n", this.numOfInserts);
    	}
    }
    
    /**
     * Flush to disk if the number of values we insert reach
     * the batch size to trigger the flush.
     */
    public void flushToDisk(){
    	if(numOfValues == DiskIO.BATCH_SIZE_TRIGGER_MEM_CHECK){
        	if(DiskIO.isTimeToFlush()){
        		//flush into disk
        		if(this.lastNodeFlushed == null){
        			this.lastNodeFlushed = this.startOfIterator;
        		}
        		
        		DiskIO.flushToDisk(computeKeysToFlush(this.lastNodeFlushed, numOfValues));
        	}
        	numOfValues = 0;
        }
    }
    

    /**
     * Compute the list of keys to flush. 
     * Iterate the key leaf nodes from start, sum up the value list size
     * of all scanned leaf nodes, terminate when the sum equal to or larger than
     * the numOfValues value strings we want to flush. If reaching the leaf node
     * whose next is null, then we continue to scan from the beginning.
     *
     * @param start the start
     * @param numOfValues the num of values
     * @return the list
     */
    private List<TrieNode> computeKeysToFlush(TrieNode start, int numOfValues){
    	List<TrieNode> leafNodesTobeFlushed = new ArrayList<TrieNode>();
    	TrieNode curr = start;
    	int numOfKeyVisited = 0;
    	//avoid infinite loop if we don't stop when we iterate all keys
    	while(numOfValues >= 0 && numOfKeyVisited < this.getNumOfKeys()){
    		if(curr == null){
    			curr = this.getStartOfIterator();
    		}
    		if(curr.notFlushed()){
    			numOfValues -= curr.getNumOfValues();
    			curr.waitForFlushing();
    			leafNodesTobeFlushed.add(curr);
    		}
    		curr = curr.getNextTrieNode();
    	}
    	
    	if(leafNodesTobeFlushed.isEmpty()){
    		throw new RuntimeException("No nodes to be flushed");
    	}
    	
    	//set the last on the list as the start point for next round of flushing
    	this.setLastNodeFlushed(leafNodesTobeFlushed.get(leafNodesTobeFlushed.size() - 1));
    	return leafNodesTobeFlushed;
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
	
	/**
	 * Iterator.
	 *
	 * @return the trie iterator
	 */
	public TrieIterator iterator(){
		return new TrieIterator(this);
	}
	
	/**
	 * Increment num of values.
	 *
	 * @param numOfV the num of v
	 */
	public void incrementNumOfValues(int numOfV){
		this.numOfValues += numOfV;
	}
}
