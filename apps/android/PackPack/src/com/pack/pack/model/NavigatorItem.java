package com.pack.pack.model;

/**
 * 
 * @author Saurav
 *
 */
public class NavigatorItem {

	private String title;
	
	private int icon;
	
	public NavigatorItem(String title, int icon) {
		setTitle(title);
		setIcon(icon);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}
}