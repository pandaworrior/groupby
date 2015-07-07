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

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mpi.groupby.util.DiskIO;

// TODO: Auto-generated Javadoc
/**
 * The Class TrieIterator.
 */
public class TrieIterator implements Iterator<Map.Entry<String, List<String>>>{
	
	/** The trie store. */
	private Trie trieStore;
    
    /** The index of key. */
    private int  indexOfKey;
    
    /** The start node. */
    private TrieNode startNode;
    
    /** The curr node. */
    private TrieNode currNode;
    
    /**
     * Instantiates a new trie iterator.
     *
     * @param t the t
     */
    public TrieIterator(Trie t){
    	this.setTrieStore(t);
    	this.setIndexOfKey(0);
    	this.setStartNode(t.getStartOfIterator());
    	this.setCurrNode(t.getStartOfIterator());
    }

	/**
	 * Checks for next.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasNext() {
		return (indexOfKey < trieStore.getNumOfKeys());
	}

	/**
	 * Next. If the value of this node is flushed to disk, we need
	 * to recover
	 *
	 * @return the trie
	 */
	@Override
	public Entry<String, List<String>> next() {
		if(hasNext()){
			TrieNode returnNode = this.currNode;
			//check whether we need to reload it
			if(returnNode.isOnDisk()){
				DiskIO.reloadFromDisk(returnNode);
			}
			this.currNode = this.currNode.getNextTrieNode();
			indexOfKey++;
			return new AbstractMap.SimpleEntry<String, List<String>>(returnNode.getKeyStr(), returnNode.getValueList());
		}
		return null;
	}

	/**
	 * Removes the.
	 */
	@Override
	public void remove() {
		throw new RuntimeException("Not implemented");
		
	}

	/**
	 * Gets the trie store.
	 *
	 * @return the trie store
	 */
	public Trie getTrieStore() {
		return trieStore;
	}

	/**
	 * Sets the trie store.
	 *
	 * @param trieStore the new trie store
	 */
	public void setTrieStore(Trie trieStore) {
		this.trieStore = trieStore;
	}

	/**
	 * Gets the index of key.
	 *
	 * @return the index of key
	 */
	public int getIndexOfKey() {
		return indexOfKey;
	}

	/**
	 * Sets the index of key.
	 *
	 * @param indexOfKey the new index of key
	 */
	public void setIndexOfKey(int indexOfKey) {
		this.indexOfKey = indexOfKey;
	}

	public TrieNode getStartNode() {
		return startNode;
	}

	public void setStartNode(TrieNode startNode) {
		this.startNode = startNode;
	}

	public TrieNode getCurrNode() {
		return currNode;
	}

	public void setCurrNode(TrieNode currNode) {
		this.currNode = currNode;
	}

}
