package com.pack.pack.util;

import java.util.ArrayList;
import java.util.List;

import com.pack.pack.model.Comment;
import com.pack.pack.model.Pack;
import com.pack.pack.model.Topic;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPacks;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.registry.ServiceRegistry;

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
	
	public static Comment convert(JComment jComment) {
		Comment comment = new Comment();
		comment.setComment(jComment.getComment());
		comment.setDateTime(jComment.getDateTime());
		comment.setFromUser(jComment.getFromUserName());
		comment.setPackId(jComment.getPackId());
		return comment;
	}
	
	public static JTopic convert(Topic topic) {
		JTopic jTopic = new JTopic();
		jTopic.setDescription(topic.getDescription());
		jTopic.setFollowers(topic.getFollowers());
		jTopic.setName(topic.getName());
		String userId = topic.getOwnerId();
		jTopic.setOwnerId(userId);
		jTopic.setId(topic.getId());
		User user = getUserInfo(userId);
		if(user != null) {
			jTopic.setOwnerName(user.getName());
		}
		return jTopic;
	}
	
	public static List<JTopic> convertTopicList(List<Topic> topics) {
		List<JTopic> jTopics = new ArrayList<JTopic>();
		for(Topic topic : topics) {
			JTopic jTopic = convert(topic);
			jTopics.add(jTopic);
		}
		return jTopics;
	}
	
	private static User getUserInfo(String userId) {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		return service.get(userId);
	}
}