package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JPacks {

	private List<JPack> packs;

	public List<JPack> getPacks() {
		if(packs == null) {
			packs = new ArrayList<JPack>();
		}
		return packs;
	}

	public void setPacks(List<JPack> packs) {
		this.packs = packs;
	}
}