package com.pack.pack.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saurav
 *
 */
public class SystemInfo {

	private List<Info> infos;
	
	public List<Info> getInfos() {
		if(infos == null) {
			infos = new ArrayList<Info>(5);
		}
		return infos;
	}
}