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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.function.Consumer;

import org.mpi.groupby.util.RuntimeError;

/**
 * The Class KeyValueIterator.
 */
public class KeyValueIterator implements Iterator<Map.Entry<String, String>>{
	
	/** The input stream. */
	private FileInputStream inputStream;
	
	/** The scanner. */
	private Scanner scanner;
	
	/**
	 * Instantiates a new key value iterator.
	 *
	 * @param fileName the file name
	 */
	public KeyValueIterator(String fileName){
		try {
			inputStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(RuntimeError.FILENOTFOUNDERROR);
		}
		
		scanner = new Scanner(inputStream, "UTF-8");
		this.detectScannerError();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		boolean result = scanner.hasNextLine();
		this.detectScannerError();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Entry<String, String> next() {
		if(hasNext()){
			String line = scanner.nextLine();
			this.detectScannerError();
			String[] result = line.split("\\s");
			if(result.length != 2){
				throw new RuntimeException("Reading wrong format");
			}
			Map.Entry<String, String> e = new AbstractMap.SimpleEntry<String, String>(result[0], result[1]);
			return e;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new RuntimeException("Not implemented");
		
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#forEachRemaining(java.util.function.Consumer)
	 */
	@Override
	public void forEachRemaining(Consumer<? super Entry<String, String>> action) {
		throw new RuntimeException("Not implemented");
		
	}
	
	/**
	 * Detect scanner error.
	 */
	private void detectScannerError(){
		if(scanner.ioException() != null){
			scanner.ioException().printStackTrace();
			System.exit(RuntimeError.FILESCANNERERROR);
		}
	}

}
