package com.example.huffman;

import Structures.BTNode;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class HuffmanFileReader	 {
    private File file;
    private FileInputStream fin;
    private FileChannel channel;
    private ByteBuffer buff;

    public HuffmanFileReader(File file) {
        this.file = file;

        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        channel = fin.getChannel();
        buff = ByteBuffer.allocate(8);
    }


    // Reads header from compressed file using ByteBuffer and parses it
    public String readHeader(Header header) {

        StringBuilder headerSig = new StringBuilder();
        String headerTreeInBinary;
        try {
            // Read and verify the "huf" signature (3 bytes)
            buff = ByteBuffer.allocate(3);
            channel.read(buff);
            buff.flip();
            byte[] ar = buff.array();
            buff.clear();

            for (int i = 0; i < ar.length; i++) {
                headerSig.append((char) ar[i]);
            }

            if (!headerSig.toString().equals("huf")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Error in the file ");
                alert.setContentText("This isn't a huf file compressed by this system");
                alert.showAndWait();
                return null;
            }


            // Read extension size (1 byte)
            buff = ByteBuffer.allocate(1);
            channel.read(buff);
            buff.flip();
            byte extensionSize = buff.get();
            buff.clear();

            // Read extension string
            buff = ByteBuffer.allocate(extensionSize);
            channel.read(buff);
            buff.flip();
            ar = buff.array();
            String extension = "";
            for (int i = 0; i < extensionSize; i++) {
                extension += (char) ar[i];
            }
            buff.clear();

			// Read total size of original file (4 bytes)
            buff = ByteBuffer.allocate(4);
            channel.read(buff);
            buff.flip();
            int totalSize = buff.getInt();
            buff.clear();

			// Read size of header tree (4 bytes)
            buff = ByteBuffer.allocate(4);
            channel.read(buff);
            buff.flip();
            int headerTreeSize = buff.getInt();
            buff.clear();

			// Set header fields
			header.setExtensionSize(extensionSize);
            header.setHeaderSize(headerTreeSize);
            header.setTotalSize(totalSize);
            header.setExtension(extension);
            header.setsignature(headerSig.toString());

			// Read the actual binary header tree as string
            buff = ByteBuffer.allocate(headerTreeSize);
            channel.read(buff);
            buff.flip();
            byte headerTreeInBinaryInBytes[] = buff.array();
            headerTreeInBinary = extractBinaryFromHeaderBytes(headerTreeInBinaryInBytes);
            buff.clear();

        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error in the file ");
            alert.setContentText("This isn't a huf file compressed by this system");


            alert.showAndWait();
            return null;
        }

        return headerTreeInBinary;

    }


    // Converts an array of bytes into a continuous binary string
    private String extractBinaryFromHeaderBytes(byte[] byteArray) {
        StringBuilder binaryString = new StringBuilder();

        for (byte b : byteArray) {
            System.out.println("byte = "+b );
            int unsignedValue = (b < 0) ? b + 256 : b;

            // Convert to 8-bit binary string with leading zeros
            System.out.println("unsigned value = "+unsignedValue);
            System.out.println("value befor padding = "+  String.format("%8s", Integer.toBinaryString(unsignedValue)));
            String bits = String.format("%8s", Integer.toBinaryString(unsignedValue)).replace(' ', '0');
            System.out.println("bits = "+bits);

            binaryString.append(bits);
            System.out.println(binaryString);
        }
        System.out.println(binaryString);
        System.out.println("paddedBinaryTreeHeader = 010110000110110001000000");
        return binaryString.toString();
    }



    /** This method rebuilds the original file by reading the compressed bits
     * and traversing the Huffman tree to decode each symbol.*/
    public void decodeCompressedFileToOriginal(int originalFileSize, BTNode rootNode, File outputFile) throws IOException {
        ByteBuffer inputBuffer = ByteBuffer.allocate(2048);
        ByteBuffer outputBuffer = ByteBuffer.allocate(512);

        try (
                FileOutputStream fout = new FileOutputStream(outputFile);
                FileChannel outChannel = fout.getChannel()
        ) {
            int decodedBytes = 0;
            StringBuilder bitQueue = new StringBuilder();

            // Start by filling buffer
            channel.read(inputBuffer);
            inputBuffer.flip();

            while (decodedBytes < originalFileSize) {
                BTNode current = rootNode;

                // Traverse tree until leaf
                while (!current.isLeaf()) {
                    // Load more bits if needed
                    if (bitQueue.length() == 0) {
                        if (!inputBuffer.hasRemaining()) {
                            inputBuffer.clear();
                            if (channel.read(inputBuffer) < 1)
                                break;
                            inputBuffer.flip();
                        }
                        bitQueue.append(toBitString(inputBuffer.get()));
                    }

                    // Traverse
                    char bit = bitQueue.charAt(0);
                    bitQueue.deleteCharAt(0);
                    current = (bit == '0') ? current.getLeft() : current.getRight();
                }

                // Write decoded byte
                outputBuffer.put(current.getValue());
                decodedBytes++;

                // Flush every 512 bytes
                if (outputBuffer.position() == 512) {
                    outputBuffer.flip();
                    outChannel.write(outputBuffer);
                    outputBuffer.clear();
                }
            }

            // Final flush
            if (outputBuffer.position() > 0) {
                outputBuffer.flip();
                outChannel.write(outputBuffer);
            }

            channel.close(); // Close input channel

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }



    private String toBitString(byte b) {
        String binary = Integer.toBinaryString(b < 0 ? b + 256 : b);
        return "00000000".substring(binary.length()) + binary;
    }




}
