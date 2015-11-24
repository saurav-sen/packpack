package com.pack.pack.util;

import java.util.List;

import com.pack.pack.model.Pack;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPacks;

/**
 * 
 * @author Saurav
 *
 */
public class ModelConverter {

	public static JPack convert(Pack pack) {
		return null;
	}
	
	public static JPacks convert(List<Pack> packs) {
		if(packs == null)
			return null;
		JPacks jPacks = new JPacks();
		for(Pack pack : packs) {
			JPack jPack = convert(pack);
			if(jPack != null) {
				jPacks.getPacks().add(jPack);
			}
		}
		return jPacks;
	}
}