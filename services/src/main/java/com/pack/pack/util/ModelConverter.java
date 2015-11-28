package com.pack.pack.util;

import java.util.List;

import com.pack.pack.model.Pack;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPacks;
import com.pack.pack.model.web.JUser;

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
	
	public static JUser convert(User user, String profilePictureUrl) {
		JUser jUser = new JUser();
		jUser.setId(user.getId());
		jUser.setDob(user.getDob());
		jUser.setName(user.getName());
		jUser.setUsername(user.getUsername());
		jUser.setProfilePictureUrl(profilePictureUrl);
		return jUser;
	}
}