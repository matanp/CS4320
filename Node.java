import java.util.ArrayList;

public class Node<K extends Comparable<K>, T> {
	protected boolean isLeafNode;
	protected ArrayList<K> keys;

	public boolean isOverflowed() {
		return keys.size() > 2 * BPlusTree.D;
	}

	public boolean willBeOverflowed() {
		return keys.size() + 1 > 2 * BPlusTree.D;
	}

	public boolean isUnderflowed() {
		return keys.size() < BPlusTree.D;
	}

	public ArrayList<K> getKeys() {
		return keys;
	}

}
