package org.mpi.groupby.datastructures;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
}
