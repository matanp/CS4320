import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Collections;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 * TODO: Rename to BPlusTree
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	public BPlusTree(Node<K,T> root1) {
		root = root1;
	}
	public BPlusTree() {
		root = new Node();
	}


	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
		LeafNode<K,T> result = (LeafNode<K,T>) searchHelper(root, key);
		int i = result.keys.indexOf(key);
		if(i>=0){
			return result.values.get(i);
		} else {
			return null;
		}
	}

	private Node searchHelper(Node<K,T> node, K key) {
		if(node.isLeafNode) {
			return node;
		} else {
			IndexNode<K,T> node1 = (IndexNode<K,T>) node;
			for (int i=0; i<node1.keys.size(); i++) {
				if(key.compareTo(node1.keys.get(i))<0) {
					return searchHelper(node1.children.get(i), key);
				}
			} 
			return searchHelper(node1.children.get(node1.keys.size()), key);
		}
	}




	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
		K pair1 = null;
		Node<K,T> pair2 = null; 
		insertHelper(root, key, value, pair1, pair2);
		if(pair1 != null){ //Root needs to be split

		}
	}

	public void insertHelper(Node<K,T> root, K key, T value, K pair1, Node<K,T> pair2) {
		if(!root.isLeafNode) {  //root is an index
			IndexNode<K,T> root1 = (IndexNode<K,T>) root;
			int i=0;
			for(int j=1; j<root1.keys.size(); j++) {
				if(key.compareTo(root1.keys.get(j))<0) {
					i++;
				}
			}
			if(key.compareTo(root1.keys.get(root1.keys.size()))>=0){
				i++;
			}
			Node newnode = root1.children.get(i);
			insertHelper(newnode, key, value, pair1, pair2);
			if(pair1==null){
				return;
			} else {
				root1.keys.add(pair1);
				Collections.sort(root1.getKeys());
				ArrayList<K> firstkeys = new ArrayList<K>(root1.getKeys().subList(0, D+1));
				ArrayList<K> secondkeys = new ArrayList<K>(root1.getKeys().subList(D+2, root1.getKeys().size()));
				ArrayList<Node<K,T>> firstchildren = new ArrayList<Node<K,T>>(root1.children.subList(0, D+2));
				ArrayList<Node<K,T>> secondchildren = new ArrayList<Node<K,T>>(root1.children.subList(D+2, root1.children.size()));
				root1.keys = firstkeys;
				root1.children = firstchildren;
				IndexNode<K,T> second = new IndexNode<K,T>(secondkeys, secondchildren);
				pair1 = second.keys.get(0);
				pair2 = second; 
				return;
			}
		} else { //root is a leaf
			LeafNode<K,T> root1 = (LeafNode<K,T>) root;
			root1.keys.add(key);
			Collections.sort(root1.keys);
			int index = root1.keys.indexOf(key);
			root1.values.add(index, value);
			if(!root1.isOverflowed()){ //There's room to insert the pair
				pair1 = null;
				pair2 = null;
				return;
			} else {
				ArrayList<K> keys1 = new ArrayList<K>(root1.keys.subList(0, D+1));
				ArrayList<K> keys2 = new ArrayList<K>(root1.keys.subList(D+1, root1.keys.size()));
				ArrayList<T> values1 = new ArrayList<T>(root1.values.subList(0, D+1));
				ArrayList<T> values2 = new ArrayList<T>(root1.values.subList(D+1, root1.values.size()));
				root1.keys = keys1;
				root1.values = values1;
				LeafNode<K,T> newLeaf = new LeafNode<K,T>(keys2, values2);
				root1.nextLeaf.previousLeaf = newLeaf;
				newLeaf.nextLeaf = root1.nextLeaf;
				root1.nextLeaf = newLeaf;
				pair1 = newLeaf.keys.get(0);
				pair2 = newLeaf;
				return;
			}
		}
	} 

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf, any other relevant data
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {

		return null;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {

		return null;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		return -1;
	}

	public static void main(String[] args){
	LeafNode<Integer, String> child1 = new LeafNode<Integer, String>(1, "A");
	LeafNode<Integer, String> child2 = new LeafNode<Integer, String>(3, "C");
	IndexNode<Integer, String> root = new IndexNode<Integer, String>(2, child1, child2);
	BPlusTree<Integer, String> bplus = new BPlusTree<Integer, String>(root);
	String result = bplus.search(1);
	System.out.println(result);
	String result2 = bplus.search(2);
	System.out.println(result2);
	String result3 = bplus.search(3);
	System.out.println(result3);
	}

}