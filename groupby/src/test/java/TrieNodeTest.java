

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mpi.groupby.datastructures.TrieNode;
import org.mpi.groupby.util.Role;

public class TrieNodeTest {
	@Test
	@Category(org.mpi.groupby.datastructures.TrieNode.class)
	public void testSetKeyValue(){
		TrieNode tN = new TrieNode('a', Role.LEAF);
		
		boolean thrown = false;
		try{
			tN.setKeyStr("a");
			tN.addValueStr("b");
		}catch(Exception e){
			thrown = true;
		}
		Assert.assertEquals(thrown, false);
	}
	
	@Test
	@Category(org.mpi.groupby.datastructures.TrieNode.class)
	public void testSetKeyValueInternal(){
		TrieNode tN = new TrieNode('a', Role.INTERNAL);
		
		boolean thrown = false;
		try{
			tN.setKeyStr("a");
			tN.addValueStr("b");
		}catch(Exception e){
			thrown = true;
		}
		Assert.assertEquals(thrown, true);
	}
}
