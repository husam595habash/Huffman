package Structures;

public class BTNode {

	// Left and right children in the Huffman tree
	private BTNode left;
	private BTNode right;

	// Frequency of the character or sum of frequencies for internal nodes
	private int frequency;

	// Byte value for the character (used in leaf nodes only)
	private byte value;

	// Huffman code generated for this node
	private String code;

	// Flag to indicate if this node contains actual data (leaf) or is internal
	private boolean isData;

	public BTNode() {
	}

	public BTNode(int frequency, byte value) {
		this.frequency = frequency;
		this.value = value;

	}

	// Getters and setters
	public BTNode getLeft() {
		return left;
	}

	public void setLeft(BTNode left) {
		this.left = left;
	}

	public BTNode getRight() {
		return right;
	}

	public void setRight(BTNode right) {
		this.right = right;
	}

	public int getfrequency() {
		return frequency;
	}

	public void setfrequency(int value) {
		this.frequency = value;
	}

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


	public boolean isData() {
		return isData;
	}

	public void setData(boolean isData) {
		this.isData = isData;
	}


	// Recursively assigns Huffman codes to all leaf nodes in the tree
	public void generateHuffmanCodes(String code) {
		if (this.left != null) {

			this.left.setCode(code + "0");

			this.left.generateHuffmanCodes(this.left.code);

		}

		if (this.right != null) {

			this.right.setCode(code + "1");

			this.right.generateHuffmanCodes(this.right.code);
		}

	}

	// Fills the Huffman code table with entries for all characters (leaves)
	public void fillHuffmanTable(CodeTableEntry[] huffmanTable) {
		if (this.isData) {
			int unsignedValue = this.value < 0 ? this.value + 256 : this.value;
			huffmanTable[unsignedValue] = new CodeTableEntry(
					this.code,
					(char) unsignedValue,
					this.frequency
			);
		}

		if (this.left != null) {
			this.left.fillHuffmanTable(huffmanTable);
		}

		if (this.right != null) {
			this.right.fillHuffmanTable(huffmanTable);
		}
	}



	public String generateHeaderBinaryTreeInBinary() {
		StringBuilder sb = new StringBuilder();
		buildPreOrderBinaryHeader(sb);
		return sb.toString();
	}

	private void buildPreOrderBinaryHeader(StringBuilder sb) {
		if (this.left == null && this.right == null) {
			sb.append('1');
			System.out.println(sb);
			int unsignedValue = (this.value + 256) % 256;
			System.out.println("unsgined value ="+unsignedValue);
			System.out.println("unsigned Value before badding"+String.format("%8s", Integer.toBinaryString(unsignedValue)));
			String bits = String.format("%8s", Integer.toBinaryString(unsignedValue)).replace(' ', '0');
			System.out.println("bits = " + bits);
			sb.append(bits);
			System.out.println(sb);
		} else {
			sb.append('0');
			System.out.println(sb);
			if (this.left != null) this.left.buildPreOrderBinaryHeader(sb);
			if (this.right != null) this.right.buildPreOrderBinaryHeader(sb);
		}
	}




	/**
	 * Returns a human-readable string of the tree structure using pre-order traversal
	 * Format: '1' + character for leaf, '0' for internal node
	 */
	// Example: 0 1A 1B → root node, then left leaf A, right leaf B
	public String preOrderCodingInHeaderTextArea() {
		StringBuilder sb = new StringBuilder();
		buildPreOrderHeader(sb);
		return sb.toString();
	}

	// Helper method to fill StringBuilder using pre-order traversal
	private void buildPreOrderHeader(StringBuilder sb) {
		if (this.left == null && this.right == null) {
			sb.append('1');                 // Mark as leaf
			sb.append((char) this.value);
		} else {
			sb.append('0');                // Mark as internal node
			if (this.left != null) {
				this.left.buildPreOrderHeader(sb);
			}
			if (this.right != null) {
				this.right.buildPreOrderHeader(sb);
			}
		}
	}



	// Recursively counts internal nodes in the Huffman tree
	public int countInternalNodes() {
		int count;
		if (this.left != null || this.right != null) {
			count = 1;
			count += this.left.countInternalNodes();
			count += this.right.countInternalNodes();
			return count;
		} else {
			return 0;
		}

	}

	// to check if the node is leaf
	public boolean isLeaf() {
		if (this.left == null && this.right == null) {
			return true;
		} else {
			return false;
		}
	}

}
