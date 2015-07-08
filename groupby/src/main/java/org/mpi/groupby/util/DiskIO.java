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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.mpi.groupby.datastructures.TrieNode;

// TODO: Auto-generated Javadoc
/**
 * The Class DiskIO is used to flush memory objects into a file, and read from a file
 * to recover the in-memory object.
 */
public class DiskIO {
	
	/**All tags are used to search in the configuration xml file.*/
	
	/** The Constant tag INPUT_STR. */
	private final static String INPUT_STR = "input";
	
	/** The len of key. */
	private static int LEN_OF_KEY;
	
	/** The Constant tag LEN_OF_KEY_STR. */
	private final static String LEN_OF_KEY_STR = "lenOfKeyStr";
	
	/** The len of value. */
	private static int LEN_OF_VALUE;
	
	/** The Constant tag LEN_OF_VALUE_STR. */
	private final static String LEN_OF_VALUE_STR = "lenOfValueStr";
	
	/** The Constant tag MEM_STR. */
	private final static String MEM_STR = "mem";
	
	/** The memory threshold to trigger the disk flush. */
	private static double MAX_PER_MEMORY_USAGE;
	
	/** The Constant tag MAX_PER_MEMORY_USAGE_STR. */
	private final static String MAX_PER_MEMORY_USAGE_STR = "maxPerMemUsage";
	
	/** The max memory assigned when the program starts. */
	private static long MAX_MEMORY_ASSIGNED = Runtime.getRuntime().maxMemory();
	
	/** The batch size trigger mem check. */
	public static int BATCH_SIZE_TRIGGER_MEM_CHECK;
	
	/** The Constant tag BATCH_SIZE_TRIGGER_MEM_CHECK_STR. */
	private final static String BATCH_SIZE_TRIGGER_MEM_CHECK_STR = "batchSizeTriggerMemCheck";
	
	/** The Constant tag OUTPUT_STR. */
	private final static String OUTPUT_STR = "output";
	
	/** The value file dir. */
	private static String VALUE_FILE_DIR;
	
	/** The Constant tag VALUE_FILE_DIR_STR. */
	private final static String VALUE_FILE_DIR_STR = "valueFileDirStr";
	
	/** The value file prefix. */
	private static String VALUE_FILE_PREFIX;
	
	/** The Constant tag VALUE_FILE_PREFIX_STR. */
	private final static String VALUE_FILE_PREFIX_STR = "valueFilePrefix";
	
	/** The value file count. */
	private static int VALUE_FILE_COUNT = 0;
	
	/** The max size per file. */
	private static int MAX_SIZE_PER_FILE;
	
	/** The Constant tag MAX_SIZE_PER_FILE_STR. */
	private final static String MAX_SIZE_PER_FILE_STR = "maxSizePerFile";
	
	/** The max num value per file. */
	private static int MAX_NUM_VALUE_PER_FILE;
	
	/** The Constant MAX_NUM_VALUE_PER_FILE_STR. */
	private static final String MAX_NUM_VALUE_PER_FILE_STR = "maxNumOfValuesPerFile";
	
	/** The current file object whose space is not used up. */
	private static FileUtil currentFile;
	
	/** The num of values reloaded from disk. */
	private static int numOfValuesReloadedFromDisk = 0;
	
	/**
	 * Sets the up by reading file.
	 *
	 * @param configFile the new up by reading file
	 */
	public static void setUpByReadingFile(String configFile){
		try{
		    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		    InputStream in  = new FileInputStream(configFile);
		    XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		   
		    while (eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()){
				    StartElement startElement = event.asStartElement();
				    if (startElement.getName().getLocalPart().equals(INPUT_STR)){
				    	Iterator<Attribute> attributes = startElement.getAttributes();
				    	while(attributes.hasNext()){
				    		Attribute attribute = attributes.next();
				    		if (attribute.getName().toString().equals(LEN_OF_KEY_STR)){
				    			LEN_OF_KEY = Integer.parseInt(attribute.getValue());
				    		}else if (attribute.getName().toString().equals(LEN_OF_VALUE_STR)){
				    			LEN_OF_VALUE = Integer.parseInt(attribute.getValue());
				    		}else{
				    			throw new RuntimeException("invalid attribute");
				    		}
				    	}
				    }
				    
				    if (startElement.getName().getLocalPart().equals(MEM_STR)){
				    	Iterator<Attribute> attributes = startElement.getAttributes();
				    	while(attributes.hasNext()){
				    		Attribute attribute = attributes.next();
				    		if (attribute.getName().toString().equals(MAX_PER_MEMORY_USAGE_STR)){
				    			MAX_PER_MEMORY_USAGE = Float.parseFloat(attribute.getValue());
				    		}else if (attribute.getName().toString().equals(BATCH_SIZE_TRIGGER_MEM_CHECK_STR)){
				    			BATCH_SIZE_TRIGGER_MEM_CHECK = Integer.parseInt(attribute.getValue());
				    		}else{
				    			throw new RuntimeException("invalid attribute");
				    		}
				    	}
				    }
				    
				    if (startElement.getName().getLocalPart().equals(OUTPUT_STR)){
				    	Iterator<Attribute> attributes = startElement.getAttributes();
				    	while(attributes.hasNext()){
				    		Attribute attribute = attributes.next();
				    		if (attribute.getName().toString().equals(VALUE_FILE_DIR_STR)){
				    			VALUE_FILE_DIR = attribute.getValue();
				    		}else if (attribute.getName().toString().equals(VALUE_FILE_PREFIX_STR)){
				    			VALUE_FILE_PREFIX = attribute.getValue();
				    		}else if (attribute.getName().toString().equals(MAX_SIZE_PER_FILE_STR)){
				    			MAX_SIZE_PER_FILE = Integer.parseInt(attribute.getValue());//In MB
				    		}else{
				    			throw new RuntimeException("invalid attribute");
				    		}
				    	}
				    }
				}
		    }
		}catch(Exception e){
		    e.printStackTrace();
		    System.exit(RuntimeError.FILEPARSINGFAILURE);
		}
		
		//compute how many values at most for each file to store
		MAX_NUM_VALUE_PER_FILE = (MAX_SIZE_PER_FILE * 1024 * 1024)/LEN_OF_VALUE;
		
		if(Debug.debug){
			printOut();
		}
	}
	
	/**
	 * Creates the new file.
	 *
	 * @return the file
	 */
	public static File createNewFile(){
		String pathName = VALUE_FILE_DIR + "/" + VALUE_FILE_PREFIX + VALUE_FILE_COUNT++;
		File f = new File(pathName);
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(RuntimeError.FILECREATIONFAILURE);
		}
		return f;
	}
	
	/**
	 * Gets the position to write.
	 *
	 * @param createNew the create new
	 * @return the position to write
	 */
	public static FileUtil getPositionToWrite(boolean createNew){
		if(currentFile == null|| createNew){
			File newFile = createNewFile();
			currentFile = new FileUtil(newFile, 0, MAX_NUM_VALUE_PER_FILE);
		}
		return currentFile;
	}
	
	/**
	 * Prints the out.
	 */
	public static void printOut(){
		StringBuilder strB = new StringBuilder();
		strB.append("--------------> Config File Output <-------------------\n");
		strB.append(LEN_OF_KEY_STR + " " + LEN_OF_KEY + "\n");
		strB.append(LEN_OF_VALUE_STR + " " + LEN_OF_VALUE + "\n");
		strB.append(MAX_PER_MEMORY_USAGE_STR + " " + MAX_PER_MEMORY_USAGE + "\n");
		strB.append(BATCH_SIZE_TRIGGER_MEM_CHECK_STR + " " + BATCH_SIZE_TRIGGER_MEM_CHECK + "\n");
		strB.append(VALUE_FILE_DIR_STR + " " + VALUE_FILE_DIR + "\n");
		strB.append(VALUE_FILE_PREFIX_STR + " " + VALUE_FILE_PREFIX + "\n");
		strB.append(MAX_SIZE_PER_FILE_STR + " " + MAX_SIZE_PER_FILE + " MB\n");
		strB.append(MAX_NUM_VALUE_PER_FILE_STR + " " + MAX_NUM_VALUE_PER_FILE + "\n");
		strB.append("--------------> Config File Output <-------------------");
		System.out.println(strB.toString());
	}
	
	/**
	 * Checks if is time to flush.
	 *
	 * @return true, if is time to flush
	 */
	public static boolean isTimeToFlush(){
		long freeMem = Runtime.getRuntime().freeMemory();
		long totalMem = Runtime.getRuntime().totalMemory();
		long usedMem = totalMem - freeMem;
		double memUsage = (usedMem*1.0)/MAX_MEMORY_ASSIGNED;
		return (memUsage >= MAX_PER_MEMORY_USAGE);
	}
	
	/**
	 * Gets the value byte size.
	 *
	 * @param tN the t n
	 * @return the value byte size
	 */
	public static int getValueByteSize(TrieNode tN){
		return tN.getValueList().size() * LEN_OF_VALUE;
	}
	
	/**
	 * Gets the byte str.
	 *
	 * @param tN the t n
	 * @return the byte str
	 */
	public static byte[] getByteStr(TrieNode tN){
		byte[] byteStr = new byte[getValueByteSize(tN)];
		int index = 0;
		for(String s : tN.getValueList()){
			try {
				System.arraycopy( s.getBytes("UTF-8"), 0, byteStr, index * LEN_OF_VALUE, LEN_OF_VALUE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.exit(RuntimeError.STRINGTOBYTEFAILURE);
			}
			index++;
		}
		return byteStr;
	}
	
	/**
	 * Flush to disk. Since our value string is lowercase, if we use UTF-8,
	 * then a character will only occupy a single byte. Therefore, we will know
	 * the size of the byte array of a string.
	 * After writing to disk, we empty the value list of each key leaf node.
	 *
	 * @param listOfNodesBeFlushed the list of nodes be flushed
	 */
	public static void flushToDisk(List<TrieNode> listOfNodesBeFlushed){
		if(listOfNodesBeFlushed == null || listOfNodesBeFlushed.isEmpty()){
			throw new RuntimeException("No nodes to be flushed, please check");
		}
		
		FileUtil fileDescriptor = getPositionToWrite(false);
		for(TrieNode curr : listOfNodesBeFlushed){
			//if the current file doesn't have enough space, we create another one
			if(!fileDescriptor.isEnoughSpace(curr.getNumOfValues())){
				fileDescriptor.close();//close the previous file descriptor
				fileDescriptor = getPositionToWrite(true);
			}
			curr.setDataFileName(fileDescriptor.getfDescriptor().getName());
			curr.setOffsetInDFile(fileDescriptor.getOffset());
			fileDescriptor.writeToDisk(getByteStr(curr), curr.getNumOfValues());
			curr.cleanUpValueList();
			curr.setToBeFlushed();
		}
	}
	
	/**
	 * Reload from disk.
	 *
	 * @param tN the t n
	 */
	public static void reloadFromDisk(TrieNode tN){
		//first check if we need to free memory since reload will consume memory
		tN.getMyTrieInstance().incrementNumOfValues(tN.getNumOfValues());
		tN.getMyTrieInstance().flushToDisk();
		try {
			RandomAccessFile raf = new RandomAccessFile(tN.getDataFileName(), "r");
			byte[] byteStr = new byte[tN.getNumOfValues() * LEN_OF_VALUE];
			int resultLen = raf.read(byteStr, tN.getOffsetInDFile(), byteStr.length);
			
			if(resultLen != byteStr.length){
				raf.close();
				throw new RuntimeException("Reading wrong format");
			}
			
			List<String> valueList = new ArrayList<String>();
			int count = 0;
			while(count < tN.getNumOfValues()){
				String valueStr = new String(byteStr, count * LEN_OF_VALUE, LEN_OF_VALUE);
				valueList.add(valueStr);
				count++;
			}
			
			tN.setValueList(valueList);
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(RuntimeError.FILERANDOMREADERROR);
		}
		
	}
	
}
