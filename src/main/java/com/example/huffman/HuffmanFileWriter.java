package com.example.huffman;

import Structures.CodeTableEntry;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


	public class HuffmanFileWriter {
	private File file;
	private FileOutputStream fout;
	private FileChannel channel;
	private ByteBuffer buff;
	private StringBuilder currentBits;
	private int counterOfBytes = 0;

	public HuffmanFileWriter(File file) {
		this.file = file;

		try {
			fout = new FileOutputStream(file);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		channel = fout.getChannel();
		buff = ByteBuffer.allocate(1024);
		currentBits = new StringBuilder("");
	}

	/**
	 * Writes the full header to the file:
	 * - Signature "huf"
	 * - Extension size and actual extension
	 * - Original file size
	 * - Header tree size
	 * - Binary Huffman tree
	 */
	public void writeHeaderData(Header header, String binaryTreeHeader) {
		// Signature to identify the file format
		buff.put(new byte[] { (byte) 'h', (byte) 'u', (byte) 'f' });

		// File extension length (e.g., 3 for "txt")
		buff.put(header.getExtensionSize());

		// Write extension (e.g., "txt")
		putString(header.getExtension());

		// Original file size (before compression)
		buff.putInt(header.getTotalSize());

		// Header tree size (in bytes)
		buff.putInt(header.getHeaderSize());

		// Encoded tree structure (pre-order traversal)
		putHeaderTree(binaryTreeHeader);
		buff.flip();
		try {
			channel.write(buff);
		} catch (IOException e) {

			e.printStackTrace();
		}

		buff.clear();

	}

		public void encodeFileContent(CodeTableEntry[] codeTable, File inputFile) {
			if (inputFile == null) {
				return;
			}

			try (RandomAccessFile aFile = new RandomAccessFile(inputFile, "r");
				 FileChannel inChannel = aFile.getChannel()) {

				ByteBuffer buffer = ByteBuffer.allocate(64);

				while (inChannel.read(buffer) > 0) {
					buffer.flip();

					while (buffer.hasRemaining()) {
						byte currentByte = buffer.get();
						int index = currentByte < 0 ? currentByte + 256 : currentByte;

						CodeTableEntry entry = codeTable[index];
						if (entry != null) {
							putCompressedFileData(entry.getCode());
						} else {
							System.err.println("Missing code for byte: " + index);
						}
					}

					buffer.clear();
				}

			} catch (FileNotFoundException e) {
				System.err.println("File not found: " + inputFile.getAbsolutePath());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("I/O error while reading file: " + inputFile.getAbsolutePath());
				e.printStackTrace();
			}
		}


		/**
	 * Converts a string to a byte array and puts it into the buffer.
	 * Used for writing the file extension string.
	 */
		public void putString(String str) {
			byte[] bytes = new byte[str.length()];
			int i = 0;
			for (char ch : str.toCharArray()) {
				bytes[i++] = (byte) ch;
			}
			buff.put(bytes);
		}


		/**
	 * Converts the binary representation of the Huffman tree into actual bytes
	 * and places them into the buffer.
	 */
	public void putHeaderTree(String binaryHeaderString) {
		String temp = "";

		for (int i = 0; i < binaryHeaderString.length(); i++) {
			temp += binaryHeaderString.charAt(i);

			// Every 8 bits (1 byte), convert to byte and store
			if ((i + 1) % 8 == 0) {
				int byteValue  = Integer.parseInt(temp, 2);
				buff.put((byte) byteValue );
				temp = "";
			}
		}

	}

	/**
	 * Called at the end of compression to flush any remaining bits that
	 * don't make up a full byte by padding them with zeros.
	 */
	public void cleanTheCurrentBits() {
		if (currentBits.length() > 0 && currentBits.length() < 8) {
			// Pad with '0's until 8 bits
			while (currentBits.length() < 8) {
				currentBits.append('0');
			}

			// Convert to byte and write
			int byteValue = Integer.parseInt(currentBits.toString(), 2);
			buff.put((byte) byteValue);
			counterOfBytes++;

			// Always flush remaining buffer content
			try {
				buff.flip();
				channel.write(buff);
				buff.clear();
				counterOfBytes = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Clear bits after writing
			currentBits.setLength(0);
		}
	}



		/**
	 * Writes bits of the compressed file content to the buffer and eventually to file.
	 * It accumulates bits until it forms a byte, then writes to the buffer.
	 * Every 8 buffered bytes (64 bits), flushes the buffer to disk.
	 * */
public void putCompressedFileData(String code) {
	currentBits.append(code);  // More efficient than using +=

	// Convert every full 8 bits into a byte and write it to the buffer
	while (currentBits.length() >= 8) {
		String byteString = currentBits.substring(0, 8);
		currentBits.delete(0, 8);  // Remove the processed bits
		int byteValue = Integer.parseInt(byteString, 2);
		buff.put((byte) byteValue);
		counterOfBytes++;

		// Flush buffer to file after 8 bytes
		if (counterOfBytes == 8) {
			try {
				buff.flip();
				channel.write(buff);
				buff.clear();
				counterOfBytes = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}}

