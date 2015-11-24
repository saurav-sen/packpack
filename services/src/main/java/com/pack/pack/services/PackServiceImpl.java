package com.pack.pack.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IPackService;
import com.pack.pack.model.web.JPack;
import com.pack.pack.services.exception.PackPackException;

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
	public void uploadPack(String jsonBody, InputStream attachment,
			String userId) throws PackPackException {
		// TODO Auto-generated method stub

	}

	@Override
	public void forwardPack(String packId, String fromUserId, String... userIds)
			throws PackPackException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<JPack> loadLatestPack(String userId, int pageNo)
			throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}

}
