package com.example.huffman;

public class Header {

	private String signature = "huf";
	private String extension;
	private byte extensionSize;
	private int headerSize;
	private int totalSize;

	
	
	public Header() {
		super();
	}
	public Header(String extension, byte extensionSize, int headerSize , int totalSize) {
		this.extension = extension;
		this.extensionSize = extensionSize;
		this.headerSize = headerSize;
		this.totalSize = totalSize;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public byte getExtensionSize() {
		return extensionSize;
	}
	public void setExtensionSize(byte extensionSize) {
		this.extensionSize = extensionSize;
	}
	public int getHeaderSize() {
		return headerSize;
	}
	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}
	
	public String getsignature() {
		return signature;
	}
	public void setsignature(String signature) {
		this.signature = signature;
	}
	
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	@Override
	public String toString() {
		return "Header [signature=" + signature + ", extension=" + extension + ", extensionSize=" + extensionSize + ", headerSize="
				+ headerSize + ", totalSize=" + totalSize + "]";
	}
	
	
}
