package com.pack.pack.rest.api;

/**
 * 
 * @author Saurav
 *
 */
public class ElectionResult {

	private String name;
	
	private int leading = 0;
	
	public ElectionResult() {
		
	}
	
	public ElectionResult(String name, int leading) {
		setName(name);
		setLeading(leading);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLeading() {
		return leading;
	}

	public void setLeading(int leading) {
		this.leading = leading;
	}
}
