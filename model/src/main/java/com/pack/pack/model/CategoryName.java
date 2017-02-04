package com.pack.pack.model;

/**
 * 
 * @author Saurav
 *
 */
public enum CategoryName {

	TRAVEL("Travel"),
	PHOTOGRAPHY_WILD_LIFE("Photography (Wild Life)"),
	PHOTOGRAPHY("Photography"),
	MUSIC("Music"),
	SPIRITUAL("Spiritual"),
	FASHION("Fashion"),
	BOOKS("Books");
	
	private String display;
	
	private CategoryName(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}
}