package Compression;

import Pane.CompressionPane;
import Structures.CodeTableEntry;
import Structures.Heap;
import Structures.BTNode;
import com.example.huffman.Header;
import com.example.huffman.HuffmanFileWriter;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Compression {
    private static CompressionPane compressionPane;
    private static File file;

    public static void compression(File file , CompressionPane compressionPane) {
        Compression.file = file;
        Compression.compressionPane = compressionPane;
        // Step 1: Read the file and count frequency of each byte (0–255)
        int [] frequencies = countByteFrequencies();

        // Step 2: Count how many distinct bytes appeared (non-zero frequencies)
        int countOfNonZero = countNonZeroFrequencies(frequencies);

        // STEP 3: Store non-zero frequency and value of bytes in BTNode array
        int heapIndex = 1;
        BTNode[] BTNodes = new BTNode[countOfNonZero + 1]; // because index 0 unused
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] != 0) {
                if (i >= 128)
                    BTNodes[heapIndex] = new BTNode(frequencies[i], (byte) (i - 256));
                else
                    BTNodes[heapIndex] = new BTNode(frequencies[i], (byte) i);
                BTNodes[heapIndex].setData(true);
                heapIndex++;
            }
        }

        // STEP 4: Build min-heap of characters
        Heap heap = new Heap(BTNodes.length, BTNodes, BTNodes.length - 1);


        // Step 5: Build the Huffman tree from the heap
        for (int i = 1 ; i < heap.getSize()-1 ; ++i){
            BTNode z = new BTNode();
            BTNode x = heap.extractMin();
            BTNode y = heap.extractMin();
            z.setLeft(x);
            z.setRight(y);
            z.setfrequency(x.getfrequency() + y.getfrequency());
            heap.insert(z);
        }

        // STEP 6: Generate Huffman codes and fill CodeTable
        if (countOfNonZero != 0) {
            heap.getArr()[1].generateHuffmanCodes("");

            BTNode rootNode = heap.getArr()[1];
            CodeTableEntry[] huffmanTable = new CodeTableEntry[256];

            rootNode.fillHuffmanTable(huffmanTable);

            int numberOfLeaves = countOfNonZero;

            int numberOfNonLeaves = rootNode.countInternalNodes();
            String headerTree = rootNode.preOrderCodingInHeaderTextArea();

            // STEP 7: Write compressed file with header + data
            Header header = writeHeaderAndCompressedData(numberOfLeaves, rootNode, huffmanTable, numberOfNonLeaves);

            // STEP 8: Show results in CompressionPane
            compressionPane.setHeaderDetails(
                    "Original File Size: " + header.getTotalSize() + " bytes\n" +
                            "Compressed File Extension: .huf\n" +
                            "File Extension: ." + header.getExtension() + "\n" +
                            "Extension Length: " + header.getExtensionSize() + "\n" +
                            "Header Tree: " + headerTree + "\n" +
                            "Header Tree Size: " + header.getHeaderSize() + " bytes"
            );
            compressionPane.setCodeTable(huffmanTable);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error");
            alert.setContentText("The choosen file is empty");
            alert.showAndWait();
        }

    }


    private static int[] countByteFrequencies() {
        if (file == null) {
            return null;
        }

        // 0-127 positive,  128-256 negative
        int[] byteFrequencies = new int[256];

        try (RandomAccessFile inputFile = new RandomAccessFile(file, "r");
             // FileChannel is used to quickly read a large block of bytes into the buffer for efficient processing
             FileChannel channel = inputFile.getChannel()) {
            // Use 4096 bytes (power of 2) for faster and efficient reading.
            // Powers of 2 (512, 1024, 2048, 4096) match memory structure better than random sizes
            ByteBuffer buffer = ByteBuffer.allocate(2048);

            while (channel.read(buffer) > 0) {
                buffer.flip(); // Prepare buffer for reading
                for (int i = 0; i < buffer.limit(); i++) {
                    byte tempByte = buffer.get();
                    if (tempByte < 0) {
                        byteFrequencies[tempByte + 256]++;
                    } else
                        byteFrequencies[tempByte]++;

                }
                buffer.clear();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteFrequencies;
    }


    private static Header writeHeaderAndCompressedData(int numberOfLeaves, BTNode rootNode, CodeTableEntry arr[], int numberOfNonLeaves) {

        // Calculates how many bytes the header tree will occupy in the compressed file.
        int headerTreeSize = getHeadeTreerSize(numberOfNonLeaves, numberOfLeaves);

        int fileSize= (int)file.length();

        // Initializes the Header object to store metadata like file extension, sizes,etc.
        String fileExtension = getFileExtension(file); // getting the extension
        byte fileExtensionSize = (byte) fileExtension.length();
        Header header = new Header(fileExtension, fileExtensionSize, headerTreeSize , fileSize);

        // Prepares the output file path (same directory, same name but with .huf extension).
        String directory = file.getParent();
        File compressedFile = new File(directory + "\\" + getFileName(file) + ".huf");


        String binaryTreeHeader;
        try {
            // Initializes HuffmanFileWriter, a class that helps write binary data to the file efficiently
            HuffmanFileWriter huffmanWriter  = new HuffmanFileWriter(compressedFile);

            // Generates the binary string for the header tree using post-order traversal and pads it to be byte-aligned.
            binaryTreeHeader = rootNode.generateHeaderBinaryTreeInBinary();
            String paddedBinaryTreeHeader = completeHeaderTreeString(binaryTreeHeader);
            System.out.println("paddedBinaryTreeHeader = " +paddedBinaryTreeHeader);
            huffmanWriter.writeHeaderData(header, paddedBinaryTreeHeader);
            huffmanWriter.encodeFileContent(arr ,file);
            huffmanWriter.cleanTheCurrentBits();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return header;
    }

    private static int getHeadeTreerSize(int numberOfNonLeaves, int numberOfLeaves) {
        return (int) (Math.ceil((numberOfNonLeaves + numberOfLeaves) / 8.0)) + numberOfLeaves;
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();

        int index = fileName.lastIndexOf('.');
        String extension ;
        if (index > 0) {
            extension = fileName.substring(index + 1);
            return extension;
        } else {
            return null;
        }
    }

    public static String getFileName(File file) {
        String fileName = file.getName();

        int index = fileName.lastIndexOf('.');
        String name = "";
        if (index > 0) {
            name = fileName.substring(0, index);
            return name;

        } else {
            return null;
        }
    }

    private static String completeHeaderTreeString(String str) {
        while (str.length() % 8 != 0) {
            str += "0";
        }
        return str;
    }

    private static int countNonZeroFrequencies(int[] frequencies) {
        int count = 0;
        for (int f : frequencies) {
            if (f != 0) count++;
        }
        return count;
    }


}
