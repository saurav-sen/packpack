package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JBrands {

	private List<JBrand> brands;

	public List<JBrand> getBrands() {
		if(brands == null) {
			brands = new ArrayList<JBrand>(10);
		}
		return brands;
	}

	public void setBrands(List<JBrand> brands) {
		this.brands = brands;
	}
}