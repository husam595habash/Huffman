package Decompression;


import Compression.Compression;
import Structures.BTNode;
import com.example.huffman.Header;
import com.example.huffman.HuffmanFileReader	;

import java.io.File;

public class Decompression {

    public static boolean decompression(File compressedFile) {
        try {
            // Step 1: Create header object to store file metadata
            Header header = new Header();

            // Step 2: Read the compressed file using custom reader
            HuffmanFileReader reader = new HuffmanFileReader(compressedFile);

            // Step 3: Extract Huffman tree structure in binary format from file header
            String TreeHeaderInBinary = reader.readHeader(header);
            if (TreeHeaderInBinary == null) {
                return false;  // If invalid file, exit
            }

            // Step 4: Prepare output file path ( file.txt)
            String directory = compressedFile.getParent();
            File originalFile = new File(directory + "\\" + Compression.getFileName(compressedFile) + "." + header.getExtension());

            // Step 5: Rebuild the Huffman tree from binary header string
            BTNode root = restoreTreeFromHeaderBits(TreeHeaderInBinary, new int[] {0});

            // Step 6: Assign Huffman codes to each leaf node in the tree
            root.generateHuffmanCodes("");

            // Step 7: Decode the compressed file content and reconstruct the original file
            reader.decodeCompressedFileToOriginal(header.getTotalSize(), root, originalFile);

            // Should be true if successful
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    private static BTNode restoreTreeFromHeaderBits(String TreeHeaderInBinary, int[] index) {
        if (index[0] >= TreeHeaderInBinary.length()) return null;

        char type = TreeHeaderInBinary.charAt(index[0]++);

        if (type == '1') {
            String byteBits = TreeHeaderInBinary.substring(index[0], index[0] + 8);
            index[0] += 8;
            byte value = (byte) Integer.parseInt(byteBits, 2);

            BTNode leaf = new BTNode();
            leaf.setValue(value);
            return leaf;

        } else { // type == '0' = internal node
            BTNode node = new BTNode();
            node.setLeft(restoreTreeFromHeaderBits(TreeHeaderInBinary, index));
            node.setRight(restoreTreeFromHeaderBits(TreeHeaderInBinary, index));
            return node;
        }
    }



}
