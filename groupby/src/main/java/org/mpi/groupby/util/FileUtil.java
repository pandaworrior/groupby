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

package org.mpi.groupby.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * The Class FileUtil.
 */
public class FileUtil {
	
	/** The descriptor. */
	private File fDescriptor;
	
	/** The offset. */
	private int offset;
	
	/** The num of free value slots. */
	private int numOfFreeValueSlots;
	
	/** The bos. */
	private BufferedOutputStream bos;
	
	/**
	 * Instantiates a new file util.
	 *
	 * @param f the f
	 * @param _offset the _offset
	 * @param _nFree the _n free
	 */
	public FileUtil(File f, int _offset, int _nFree){
		this.setfDescriptor(f);
		this.setOffset(_offset);
		this.setNumOfFreeValueSlots(_nFree);
		try {
			this.setBos(new BufferedOutputStream(new FileOutputStream(f)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(RuntimeError.FILEOUTPUTOPENERROR);
		}
	}

	/**
	 * Gets the f descriptor.
	 *
	 * @return the f descriptor
	 */
	public File getfDescriptor() {
		return fDescriptor;
	}

	/**
	 * Sets the f descriptor.
	 *
	 * @param fDescriptor the new f descriptor
	 */
	public void setfDescriptor(File fDescriptor) {
		this.fDescriptor = fDescriptor;
	}

	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets the offset.
	 *
	 * @param offset the new offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * Gets the num of free value slots.
	 *
	 * @return the num of free value slots
	 */
	public int getNumOfFreeValueSlots() {
		return numOfFreeValueSlots;
	}

	/**
	 * Sets the num of free value slots.
	 *
	 * @param numOfFreeValueSlots the new num of free value slots
	 */
	public void setNumOfFreeValueSlots(int numOfFreeValueSlots) {
		this.numOfFreeValueSlots = numOfFreeValueSlots;
	}
	
	/**
	 * Checks if is enough space.
	 *
	 * @param num the num
	 * @return true, if is enough space
	 */
	public boolean isEnoughSpace(int num){
		if(numOfFreeValueSlots < num){
			return false;
		}
		return true;
	}

	/**
	 * Gets the bos.
	 *
	 * @return the bos
	 */
	public BufferedOutputStream getBos() {
		return bos;
	}

	/**
	 * Sets the bos.
	 *
	 * @param bos the new bos
	 */
	public void setBos(BufferedOutputStream bos) {
		this.bos = bos;
	}
	
	/**
	 * Write to disk.
	 *
	 * @param b the b
	 * @param num the num
	 */
	public void writeToDisk(byte[] b, int num){
		try {
			bos.write(b);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(RuntimeError.FILEOUTPUTWRERROR);
		}
		this.offset += b.length;
		this.numOfFreeValueSlots -= num;
	}
	
	/**
	 * Close.
	 */
	public void close(){
		try {
			this.bos.flush();
			this.bos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(RuntimeError.FILEOUTPUTCLOSEERROR);
		}
	}
}
