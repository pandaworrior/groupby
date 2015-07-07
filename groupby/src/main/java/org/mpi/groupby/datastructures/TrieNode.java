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

import org.mpi.groupby.util.DiskIO;
import org.mpi.groupby.util.Role;

/**
 * The Class TrieNode.
 */
public class TrieNode {
    
    /** The my char. */
    private char myChar;
    
    /** The children. */
    private TrieNode[] children;
    
    /** The is leaf. */
    private boolean isLeaf;
    
    /** The my role. */
    private Role myRole;
    
    /** The key str. */
    private String keyStr;
    
    /** The value list storing all values for that key. */
    private List<String> valueList;
    
    /** The num of values. */
    private int numOfValues;
    
    /** The next trie node, needed for constructing iterator efficiently. */
    private TrieNode nextTrieNode;
    
    /** The disk data file name. */
    private String dataFileName;
    
    /** The offset in the data file. */
    private int offsetInDFile;
    
    /**
     * Instantiates a new trie node.
     *
     * @param c the c
     * @param r the r
     */
    public TrieNode(char c, Role r){
    	this.setMyChar(c);
    	this.setMyRole(r);
    	this.setChildren(new TrieNode[26]);
    }

	/**
	 * Gets the my char.
	 *
	 * @return the my char
	 */
	public char getMyChar() {
		return myChar;
	}

	/**
	 * Sets the my char.
	 *
	 * @param myChar the new my char
	 */
	public void setMyChar(char myChar) {
		this.myChar = myChar;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public TrieNode[] getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(TrieNode[] children) {
		this.children = children;
	}
	
	/**
	 * Gets the child.
	 *
	 * @param c the c
	 * @return the child
	 */
	public TrieNode getChild(char c){
		return this.children[c - 'a'];
	}
	
	/**
	 * Sets the child.
	 *
	 * @param c the c
	 * @param n the n
	 */
	public void setChild(char c, TrieNode n){
		this.children[c - 'a'] = n;
	}

	/**
	 * Checks if is leaf.
	 *
	 * @return true, if is leaf
	 */
	public boolean isLeaf() {
		return isLeaf;
	}

	/**
	 * Sets the leaf.
	 *
	 * @param isLeaf the new leaf
	 */
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	/**
	 * Gets the my role.
	 *
	 * @return the my role
	 */
	public Role getMyRole() {
		return myRole;
	}

	/**
	 * Sets the my role.
	 *
	 * @param myRole the new my role
	 */
	public void setMyRole(Role myRole) {
		this.myRole = myRole;
		if(this.myRole != Role.LEAF){
			this.setLeaf(false);
		}else{
			this.setLeaf(true);
		}
	}

	/**
	 * Gets the value list.
	 *
	 * @return the value list
	 */
	public List<String> getValueList() {
		return valueList;
	}

	/**
	 * Sets the value list.
	 *
	 * @param valueList the new value list
	 */
	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}
	
	/**
	 * Adds the value str. If the value is on disk, then we must
	 * reload it back.
	 *
	 * @param value the value
	 */
	public void addValueStr(String value){
		if(this.getValueList() == null){
			this.valueList = new ArrayList<String>();
		}
		if(this.isOnDisk()){
			DiskIO.reloadFromDisk(this);
		}
		
		this.getValueList().add(value);
		this.numOfValues++;
	}

	/**
	 * Gets the next trie node.
	 *
	 * @return the next trie node
	 */
	public TrieNode getNextTrieNode() {
		return nextTrieNode;
	}

	/**
	 * Sets the next trie node.
	 *
	 * @param nextTrieNode the new next trie node
	 */
	public void setNextTrieNode(TrieNode nextTrieNode) {
		this.nextTrieNode = nextTrieNode;
	}

	/**
	 * Gets the data file name.
	 *
	 * @return the data file name
	 */
	public String getDataFileName() {
		return dataFileName;
	}

	/**
	 * Sets the data file name.
	 *
	 * @param dataFileName the new data file name
	 */
	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	/**
	 * Gets the offset in d file.
	 *
	 * @return the offset in d file
	 */
	public int getOffsetInDFile() {
		return offsetInDFile;
	}

	/**
	 * Sets the offset in d file.
	 *
	 * @param offsetInDFile the new offset in d file
	 */
	public void setOffsetInDFile(int offsetInDFile) {
		this.offsetInDFile = offsetInDFile;
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
	 * Clean up value list.
	 */
	public void cleanUpValueList(){
		this.valueList.clear();
	}
	
	/**
	 * Checks if is on disk.
	 *
	 * @return true, if is on disk
	 */
	public boolean isOnDisk(){
		return (this.numOfValues > this.getValueList().size());
	}

	/**
	 * Gets the key str.
	 *
	 * @return the key str
	 */
	public String getKeyStr() {
		return keyStr;
	}

	/**
	 * Sets the key str.
	 *
	 * @param keyStr the new key str
	 */
	public void setKeyStr(String keyStr) {
		this.keyStr = keyStr;
	}
    
}
