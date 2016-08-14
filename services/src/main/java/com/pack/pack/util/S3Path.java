package com.pack.pack.util;


/**
 * 
 * @author Saurav
 *
 */
public class S3Path {

	private String name;
	
	private S3Path child;
	
	private boolean isFile;
	
	private S3Path parent;
	
	public S3Path(String name, boolean isFile) {
		setName(name);
		setFile(isFile);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public S3Path getChild() {
		return child;
	}
	
	public void setChild(S3Path child) {
		this.child = child;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}
	
	public S3Path getParent() {
		return parent;
	}

	public void setParent(S3Path parent) {
		this.parent = parent;
	}
	
	public S3Path addChild(S3Path child) {
		this.child = child;
		this.child.setParent(this);
		return this.child;
	}
}