package org.mpi.groupby.datastructures;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mpi.groupby.util.DiskIO;

public class TrieTest{

	@Test
	@Category(org.mpi.groupby.datastructures.Trie.class)
	public void testInsertionForKey() {
		Trie tR = new Trie();
		
		String key = "aaaaa";
		String value = "bbbbb";
		
		tR.insert(key, value);
		
		Assert.assertEquals(tR.containsKey(key), true);
	}
	
	@Test
	@Category(org.mpi.groupby.datastructures.Trie.class)
	public void testInsertionForNumOfKey() {
		Trie tR = new Trie();
		
		String key = "aaaaa";
		String value = "bbbbb";
		
		tR.insert(key, value);
		
		Assert.assertEquals(tR.getNumOfKeys(), 1);
	}
	
	@Test
	@Category(org.mpi.groupby.datastructures.Trie.class)
	public void testInsertionForNumOfValue() {
		Trie tR = new Trie();
		
		String key = "aaaaa";
		String value = "bbbbb";
		
		tR.insert(key, value);
		
		Assert.assertEquals(tR.getNumOfValues(), 1);
	}
	
	@Test
	@Category(org.mpi.groupby.datastructures.Trie.class)
	public void testInsertionForDuplicateKeys(){
		Trie tR = new Trie();
		
		String key = "aaaaa";
		String value1 = "bbbbb";
		String value2 = "ccccc";
		
		tR.insert(key, value1);
		tR.insert(key, value2);
		Assert.assertEquals(tR.getNumOfKeys(), 1);
		Assert.assertEquals(tR.getNumOfValues(), 2);
	}
	
	@Test
	@Category(org.mpi.groupby.datastructures.Trie.class)
	public void testIterator(){
		Trie tR = new Trie();
		
		for(int i = 0; i < 100; i++){
			String keyStr = RandomStringUtils.randomAlphabetic(10).toLowerCase();
			String valueStr = RandomStringUtils.randomAlphabetic(20).toLowerCase();
			
			tR.insert(keyStr, valueStr);
		}
		
		long numOfKeys = tR.getNumOfKeys();
		long index = 0;
		TrieIterator it = tR.iterator();
		while(it.hasNext()){
			index++;
			it.next();
		}
		Assert.assertEquals(numOfKeys, index);
	}
}
