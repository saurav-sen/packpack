package com.pack.pack.services.aws;


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
	
	private S3Path(S3Path s3Path) {
		this.name = s3Path.name;
		this.isFile = s3Path.isFile;
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

	public S3Path setFile(boolean isFile) {
		this.isFile = isFile;
		return this;
	}
	
	public S3Path getParent() {
		return parent;
	}

	public S3Path setParent(S3Path parent) {
		this.parent = parent;
		return this;
	}
	
	public S3Path addChild(S3Path child) {
		this.child = child;
		this.child.setParent(this);
		return this.child;
	}
	
	public S3Path clone() {
		S3Path temp = this;
		S3Path root = temp;
		int count = 0;
		while((temp = temp.getParent()) != null) {
			root = temp;
			count++;
		}
		temp = root;
		S3Path clone = null;
		S3Path parent = null;
		S3Path newS3Path = null;
		while(temp != null) {
			newS3Path = new S3Path(temp);
			if(parent != null) {
				newS3Path.parent = parent;
				parent.child = newS3Path;
			}
			parent = newS3Path;
			temp = temp.getChild();
			if(count == 0) {
				clone = newS3Path;
			}
			count--;
		}
		return clone;
	}
	
	public static void main(String[] args) {
		S3Path root = new S3Path("34a3a659d7a1b4d9ff1f0e153417fc37", false);
		root.addChild(new S3Path("08c2d03c-38a6-4cad-8e03-7a60e837ebc1.jpg", true));
		
		S3Path clone = root.clone().setFile(false);
		
		StringBuilder str = new StringBuilder();
		while(root != null) {
			str.append(root.getName());
			if(!root.isFile()) {
				str.append("/");
			}
			root = root.getChild();
		}
		System.out.println(str.toString());
		
		S3Path temp = clone;
		while(temp != null) {
			clone = temp;
			temp = temp.getParent();
		}
		
		str = new StringBuilder();
		while(clone != null) {
			str.append(clone.getName());
			if(!clone.isFile()) {
				str.append("/");
			}
			clone = clone.getChild();
		}
		System.out.println(str.toString());
	}
}