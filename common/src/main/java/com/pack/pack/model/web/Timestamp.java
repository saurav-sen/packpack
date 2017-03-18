package com.pack.pack.model.web;
/**
 * 
 * @author Saurav
 *
 */
public class Timestamp {

	private long value;
	
	private String unit;
	
	public Timestamp() {
	}
	
	public Timestamp(long value, String unit) {
		this.value = value;
		this.unit = unit;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}