package Structures;

public class CodeTableEntry {
	private String  code;
	private char value;
	private int freq;

	public CodeTableEntry() {
	}

	public CodeTableEntry(String code, char value, int freq) {
		this.code = code;
		this.value = value;
		this.freq = freq;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}





}
