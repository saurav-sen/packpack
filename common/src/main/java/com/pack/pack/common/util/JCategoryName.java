package com.pack.pack.common.util;
public enum JCategoryName {

	TRAVEL("Travel"),
	PHOTOGRAPHY_WILD_LIFE("Photography (Wild Life)"),
	PHOTOGRAPHY("Photography"),
	MUSIC("Music"),
	SPIRITUAL("Spiritual"),
	FASHION("Fashion"),
	BOOKS("Books");
	
	private String display;
	
	private JCategoryName(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}
}