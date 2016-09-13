package com.pack.pack.model;

/**
 * 
 * @author Saurav
 *
 */
public class TopicProperty {

	private String key;
	
	private String value;
	
	public TopicProperty() {
	}
	
	public TopicProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TopicProperty) {
			TopicProperty prop = (TopicProperty) obj;
			return key.equals(prop.key);
		}
		return false;
	}
}