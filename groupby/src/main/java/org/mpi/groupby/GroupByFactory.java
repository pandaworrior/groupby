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
package org.mpi.groupby;

import java.util.List;
import java.util.Map;

import org.mpi.groupby.datastructures.KeyValueIterator;
import org.mpi.groupby.datastructures.Trie;
import org.mpi.groupby.datastructures.TrieIterator;
import org.mpi.groupby.util.Debug;
import org.mpi.groupby.util.DiskIO;
import org.mpi.groupby.util.RuntimeError;

/**
 * A factory for computing GroupBy.
 */
public class GroupByFactory {
	
	/** The Constant NUM_FOR_PRINT_TEST. */
	private static final int NUM_FOR_PRINT_TEST = 100;
	
	
	/**
	 * Group by.
	 *
	 * @param kvIter the kv iter
	 * @return the trie iterator
	 */
	public static TrieIterator GroupBy(KeyValueIterator kvIter){
		if(kvIter == null){
			return null;
		}else{
			Trie tR = new Trie();
			while(kvIter.hasNext()){
				Map.Entry<String, String> entry = (Map.Entry<String, String>)kvIter.next();
				tR.insert(entry.getKey(), entry.getValue());
			}
			return tR.iterator();
		}
	}
	
	/**
	 * Prints the out test.
	 *
	 * @param tIter the t iter
	 */
	public static void printOutTest(TrieIterator tIter){
		if(tIter != null){
			int index = 0;
			while(tIter.hasNext() && index < NUM_FOR_PRINT_TEST){
				Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>)tIter.next();
				System.out.println("Key: " + entry.getKey() + "\n");
				for(String value : entry.getValue()){
					System.out.println("\t Value: " + value + "\n");
				}
				index++;
			}
		}
		
	}
	
	/**
	 * Group by.
	 *
	 * @param fileName the file name
	 * @return the trie iterator
	 */
	public static TrieIterator GroupBy(String fileName){
		KeyValueIterator kvIter = new KeyValueIterator(fileName);
		return GroupBy(kvIter);
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		if(args.length != 2){
			System.err.println("Please start the program by the following two parameters: DATAFILEPATH, CONFIGFILEPATH");
			System.exit(RuntimeError.PARAMETERERROR);
		}
		
		String dataFileName = args[0];
		String configFileName = args[1];
		
		Debug.printf("You run the program with %s, %s\n", dataFileName, configFileName);
		
		DiskIO.setUpByReadingFile(configFileName);
		
		TrieIterator tIter = GroupBy(dataFileName);
		
		printOutTest(tIter);
	}
}
