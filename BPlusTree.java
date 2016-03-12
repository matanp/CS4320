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
		root = null;
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
		if(root==null){
			root = new LeafNode<K,T>(key, value); return;
		} else {
			K pair1 = null;
			Node<K,T> pair2 = null; 
			Node<K,T> res = insertHelper(root, key, value, pair1, pair2);
			if(res != null && !root.isLeafNode){ //Root needs to be split
				IndexNode<K,T> root1 = (IndexNode<K,T>) root;
				root1.keys.add(res.keys.get(0));
				Collections.sort(root1.keys);
				int i = root1.keys.indexOf(res.keys.get(0));
				root1.children.add(i, res);
				System.out.println(root1.keys.toString());
				ArrayList<K> firstkeys = new ArrayList<K>(root1.keys.subList(0, D));
				ArrayList<K> secondkeys = new ArrayList<K>(root1.getKeys().subList(D+1, root.getKeys().size()));
				ArrayList<Node<K,T>> firstchildren = new ArrayList<Node<K,T>>(root1.children.subList(0, D+1));
				ArrayList<Node<K,T>> secondchildren = new ArrayList<Node<K,T>>(root1.children.subList(D+1, root1.children.size()));
				IndexNode<K,T> first = new IndexNode<K,T>(firstkeys, firstchildren);
				IndexNode<K,T> second = new IndexNode<K,T>(secondkeys, secondchildren);
				ArrayList<Node<K,T>> newchildren = new ArrayList<Node<K,T>>();
				newchildren.add(first); 
				newchildren.add(second);
				IndexNode<K,T> newroot = new IndexNode<K,T>(root1.keys.get(D), newchildren.get(0), newchildren.get(1));
				root = newroot;
			} else if(res != null && root.isLeafNode) {
				IndexNode<K,T> newroot = new IndexNode<K,T>(res.keys.get(0), root, res);
				root = newroot;
			}
		}
	}

	public Node<K,T> insertHelper(Node<K,T> root, K key, T value, K pair1, Node<K,T> pair2) {
		
		if(!root.isLeafNode) {  //root is an index
			IndexNode<K,T> root1 = (IndexNode<K,T>) root;
			int i=0;
			for(int j=1; j<root1.keys.size(); j++) {
				if(key.compareTo(root1.keys.get(j))>=0) {
					i++;
				}
			}
			if(root1.keys.size()>0 && key.compareTo(root1.keys.get(root1.keys.size()-1))>=0){
				i++;
			}
			Node<K,T> newnode = root1.children.get(i);
			Node<K,T> res = insertHelper(newnode, key, value, pair1, pair2);
			if(res==null){
				return null;
			} else {
				root1.keys.add(res.keys.get(0));
				Collections.sort(root1.getKeys());
				int k = root1.keys.indexOf(res.keys.get(0));
				root1.children.add(k+1, res);
				if(root1.isOverflowed()) {
					ArrayList<K> firstkeys = new ArrayList<K>(root1.getKeys().subList(0, D));
					ArrayList<K> secondkeys = new ArrayList<K>(root1.getKeys().subList(D, root1.getKeys().size()));
					ArrayList<Node<K,T>> firstchildren = new ArrayList<Node<K,T>>(root1.children.subList(0, D+1));
					ArrayList<Node<K,T>> secondchildren = new ArrayList<Node<K,T>>(root1.children.subList(D+1, root1.children.size()));
					root1.keys = firstkeys;
					root1.children = firstchildren;
					IndexNode<K,T> second = new IndexNode<K,T>(secondkeys, secondchildren); 
					return second;
				} else {
					return null;
				}
			}
		} else { //root is a leaf
			LeafNode<K,T> root1 = (LeafNode<K,T>) root;
			root1.keys.add(key);
			Collections.sort(root1.keys);
			int index = root1.keys.indexOf(key);
			root1.values.add(index, value);
			if(!root1.isOverflowed()){ //There's room to insert the pair
				return null;
			} else {
				ArrayList<K> keys1 = new ArrayList<K>(root1.keys.subList(0, D));
				ArrayList<K> keys2 = new ArrayList<K>(root1.keys.subList(D, root1.keys.size()));
				ArrayList<T> values1 = new ArrayList<T>(root1.values.subList(0, D));
				ArrayList<T> values2 = new ArrayList<T>(root1.values.subList(D, root1.values.size()));
				root1.keys = keys1;
				root1.values = values1;
				LeafNode<K,T> newLeaf = new LeafNode<K,T>(keys2, values2);
				LeafNode<K,T> third = root1.nextLeaf;
				if(root1.nextLeaf != null) {
					third.previousLeaf = newLeaf;
				}
				newLeaf.nextLeaf = third;
				root1.nextLeaf = newLeaf;
				newLeaf.previousLeaf = root1;
				return newLeaf;
			}
		}
	} 
	
	public IndexNode<K,T> createIndexNode(Node<K,T> n) {
		return new IndexNode<K, T>(n.getKeys().get(0), new Node<K, T>(), new Node<K, T>());
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
		deleteHelper(null, root, key);
	}

	public Node<K,T> deleteHelper(IndexNode<K,T> parent, Node<K,T> node, K key) {
		if(!node.isLeafNode) {
			IndexNode<K,T> node1 = (IndexNode<K,T>) node;
			int i = 0;
			for(int j=1; j<node1.keys.size(); j++) {
				if(key.compareTo(node1.keys.get(j))>=0) {
					i++;
				}
			}
			if(key.compareTo(node1.keys.get(node1.keys.size()-1))>=0){
				i++;
			}
			Node<K,T> res = deleteHelper(node1, node1.children.get(i), key);
			if(res == null) {
				return null;
			} else {
				node1.children.set(node1.children.indexOf(res)-1, node1.children.get(node1.children.indexOf(res)-1));
				node1.keys.remove(node1.keys.indexOf(res.keys.get(0)));
				node1.children.remove(node1.children.indexOf(res));
				if(!node1.isUnderflowed()){
					return null;
				} else {
					IndexNode<K,T> parent1 = (IndexNode<K,T>) parent;
					IndexNode<K,T> sibling;
					IndexNode<K,T> left;
					IndexNode<K,T> right;
					if(parent == null){
						return null;
					}
					else if(parent1.children.indexOf(node1) != 0){
						sibling = (IndexNode<K,T>) parent1.children.get(parent1.children.indexOf(node1)-1);
						left = sibling;
						right = node1;
					} else {
						sibling = (IndexNode<K,T>) parent1.children.get(parent1.children.indexOf(node1)+1);
						left = node1;
						right = sibling;
					}
					ArrayList<K> totalkeys = new ArrayList<K>();
					ArrayList<Node<K,T>> totalchildren = new ArrayList<Node<K,T>>();
					for(int j=0; j<left.keys.size(); j++){
						totalkeys.add(left.keys.get(j));
						totalchildren.add(left.children.get(j));
					}
					totalchildren.add(left.children.get(left.children.size()));
					for(int k=0; k<right.keys.size(); k++){
						totalkeys.add(right.keys.get(k));
						totalchildren.add(right.children.get(k));
					}
					totalchildren.add(right.children.get(right.children.size()));

					if(sibling.keys.size()+node1.keys.size() >= 2*D) { //redistribute
						ArrayList<K> leftkeys = new ArrayList<K>(totalkeys.subList(0, totalkeys.size()/2));
						ArrayList<K> rightkeys = new ArrayList<K>(totalkeys.subList(totalkeys.size()/2, totalkeys.size()));
						ArrayList<Node<K,T>> leftchildren = new ArrayList<Node<K,T>>(totalchildren.subList(0, totalchildren.size()/2));
						ArrayList<Node<K,T>> rightchildren = new ArrayList<Node<K,T>>(totalchildren.subList(totalkeys.size()/2, totalchildren.size()));
						left.keys = leftkeys; left.children = leftchildren;
						right.keys = rightkeys; right.children = rightchildren;
						parent.keys.set(parent.children.indexOf(right), right.keys.get(0));
						parent.keys.set(parent.children.indexOf(left), left.keys.get(0));
						return null;
					} else { //merge
						int m = left.keys.size();
						left.keys = totalkeys;
						left.keys.add(m, parent.keys.get(parent.children.indexOf(left)));
						parent.children.set(parent.keys.indexOf(right), null);
						left.children = totalchildren;
						right = null;
						return right;
					}
				}
			}

		} else {
			LeafNode<K,T> node1 = (LeafNode<K,T>) node;
			int i = node1.keys.indexOf(key);
			System.out.println("THE INDEX IS: " + i);
			node1.keys.remove(i);
			node1.values.remove(i);
			if(!node1.isUnderflowed()){
				return null;
			} else {
				LeafNode<K,T> sibling;
				LeafNode<K,T> left;
				LeafNode<K,T> right;
				IndexNode<K,T> parent1 = (IndexNode<K,T>) parent;
				if(node1.previousLeaf != null){
					sibling = node1.previousLeaf;
					left = sibling;
					right = node1;
				} else if (node1.nextLeaf != null) {
					sibling = node1.nextLeaf;
					left = node1;
					right = sibling;
				} else { 
					return null;
				}
				ArrayList<K> totalkeys = new ArrayList<K>();
				ArrayList<T> totalvalues = new ArrayList<T>();
				for(int j=0; j<left.keys.size(); j++){
					totalkeys.add(left.keys.get(j));
					totalvalues.add(left.values.get(j));
				}
				for(int k=0; k<right.keys.size(); k++){
					totalkeys.add(right.keys.get(k));
					totalvalues.add(right.values.get(k));
				}
				if(sibling.keys.size()+node1.keys.size() >= 2*D){
					ArrayList<K> keys1 = new ArrayList<K>(totalkeys.subList(0,totalkeys.size()/2));
					ArrayList<K> keys2 = new ArrayList<K>(totalkeys.subList(totalkeys.size()/2, totalkeys.size()));
					ArrayList<T> values1 = new ArrayList<T>(totalvalues.subList(0, totalvalues.size()/2));
					ArrayList<T> values2 = new ArrayList<T>(totalvalues.subList(totalvalues.size()/2, totalvalues.size()));
					left.keys = keys1;
					left.values = values1;
					right.keys = keys2;
					right.values = values2;
					int l = parent.children.indexOf(right);
					System.out.println(l);
					parent.keys.remove(l);
					parent.keys.add(l, right.keys.get(0));
					return null;
				} else {
					left.keys = totalkeys; left.values = totalvalues;
					left.nextLeaf = right.nextLeaf;
					right.nextLeaf.previousLeaf = left;
					node = null;
					return right;
				}
			}
		}
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