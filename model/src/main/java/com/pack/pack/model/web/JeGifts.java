package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class JeGifts {

	private List<JeGift> eGifts;

	public List<JeGift> geteGifts() {
		if(eGifts == null) {
			eGifts = new ArrayList<JeGift>();
		}
		return eGifts;
	}

	public void seteGifts(List<JeGift> eGifts) {
		this.eGifts = eGifts;
	}
}