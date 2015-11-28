package com.pack.pack.services;

import java.io.InputStream;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IPackService;
import com.pack.pack.model.Pack;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JPack;
import com.pack.pack.services.couchdb.PackRepositoryService;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.MessagePublisher;
import com.pack.pack.services.rabbitmq.objects.FwdPack;
import com.pack.pack.services.registry.ServiceRegistry;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class PackServiceImpl implements IPackService {

	@Override
	public JPack getPackById(String id) throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void uploadPack(JPack jPack, InputStream attachment,
			String userId) throws PackPackException {
		PackRepositoryService repoService = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		Pack pack = new Pack();
		pack.setCreationTime(new DateTime());
		pack.setCreatorId(userId);
		pack.setPackImageId(null); //TODO -- save input stream in couch DB
		pack.setStory(jPack.getStory());
		pack.setTitle(jPack.getTitle());
		repoService.add(pack);
	}

	@Override
	public void forwardPack(String packId, String fromUserId, String... userIds)
			throws PackPackException {
		// TODO Auto-generated method stub
		PackRepositoryService repoService = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		Pack pack = repoService.findById(packId);
		FwdPack fwdPack = new FwdPack();
		fwdPack.setAccessUrl(null);//TODO
		//fwdPack.setComments(pack.);
		fwdPack.setFromUserId(fromUserId);
		UserRepositoryService userRepoService = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = userRepoService.get(fromUserId);
		fwdPack.setFromUserName(user.getName());
		fwdPack.setFromUserProfilePicUrl(null); //TODO
		fwdPack.setLikes(pack.getLikes());
		fwdPack.setViews(pack.getViews());
		MessagePublisher messagingService = ServiceRegistry.INSTANCE.findService(MessagePublisher.class);
		messagingService.forwardPack(fwdPack, user);
	}

	@Override
	public List<JPack> loadLatestPack(String userId, int pageNo)
			throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}
}