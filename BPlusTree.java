import java.util.AbstractMap;
import java.util.Map.Entry;

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
		if(root.isLeafNode) {
			LeafNode root1 = (LeafNode) root;
			if(root1.keys.contains(key)) {
				int i = root1.getKeys().indexOf(key);
				return (T) root1.getValues().get(i);
			} else {
				return null; //key not found
			}	
		} else {
			IndexNode root1 = (IndexNode) root;
			Node newroot;
			if(key.compareTo((K) root1.keys.get(0)) < 0) {
				newroot = (Node) root1.children.get(0);
			} else {
				newroot = (Node) root1.children.get(1);
			}
			BPlusTree<K,T> b = new BPlusTree<K,T>(newroot);
			T answer = (T) b.search(key);
			return answer;
		}
	}

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
		if(root.isLeafNode) {
			if(!root.willBeOverflowed()) {
				//Add key, value pair to root
			} else if()


		} else {
			IndexNode root1 = (IndexNode) root;
			int i;
			if(key.compareTo((K) root1.keys.get(0))<0){
				i=0;
			} else {
				i=1;
			}
			Node newroot = (Node) root1.children.get(i);
			BPlusTree<K,T> b = new BPlusTree<K,T>(newroot);
			BPlusTree<K,T> result = b.insert(key, value);
			root1.children.get(i) = result;
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