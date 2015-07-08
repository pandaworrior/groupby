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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * The Class TestDataGenerator is used to generate
 * a test file, in which each row represents a key and value pair separated
 * by a space.
 */
public class TestDataGenerator {
	
	/** The Constant fileName. */
	private final static String fileName = "dataFile.txt";
	
	/** The Constant batchSize. */
	private final static int batchSize = 100;
	
	/**
	 * Sets the up.
	 *
	 * @param configFile the new up
	 */
	public static void setUp(String configFile){
		DiskIO.setUpByReadingFile(configFile);
	}
	
	/**
	 * Generate string.
	 *
	 * @param length the length
	 * @return the string
	 */
	public static String generateString(int length){
		return RandomStringUtils.randomAlphabetic(length).toLowerCase();
	}
	
	/**
	 * Compute num of key value pairs.
	 *
	 * @param size the size
	 * @return the int
	 */
	private static int computeNumOfKeyValuePairs(int size){
		int num = 0;
		num = (size * 1024 * 1024)/(DiskIO.getLEN_OF_KEY() + DiskIO.getLEN_OF_VALUE() + 1);
		return num;
	}
	
	/**
	 * Generate test file.
	 *
	 * @param configFile the config file
	 * @param size the size
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void generateTestFile(String configFile, int size) throws IOException{
		setUp(configFile);
		
		BufferedWriter writer = new BufferedWriter( new FileWriter(fileName));

		StringBuilder strB = new StringBuilder();
		int numOfKeyValuePairs = computeNumOfKeyValuePairs(size);
		int index = 0;
		while(numOfKeyValuePairs > 0){
			String keyStr = generateString(DiskIO.getLEN_OF_KEY());
			String valueStr = generateString(DiskIO.getLEN_OF_VALUE());
			strB.append(keyStr);
			strB.append(' ');
			strB.append(valueStr);
			strB.append('\n');
			numOfKeyValuePairs--;
			index++;
			if(index % batchSize == 0){
				index = 0;
				writer.write(strB.toString());
				strB.setLength(0);
			}
		}
		
		writer.close();
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		if(args.length != 2){
			System.err.println("TestDataGenerator [config_file] [size_in_mb]");
			System.exit(RuntimeError.PARAMETERERROR);
		}
		
		String configFile = args[0];
		int size = Integer.parseInt(args[1]);
		try {
			generateTestFile(configFile, size);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(RuntimeError.FILECREATIONFAILURE);
		}
	}
}
