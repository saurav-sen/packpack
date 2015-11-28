package com.pack.pack;

import java.io.InputStream;
import java.util.List;

import com.pack.pack.model.web.JPack;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface IPackService {
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws PackPackException
	 */
	public JPack getPackById(String id) throws PackPackException;
	
	/**
	 * 
	 * @param jPack
	 * @param attachment
	 * @param userId
	 * @throws PackPackException
	 */
	public void uploadPack(JPack jPack, InputStream attachment, 
			String userId) throws PackPackException;
	
	/**
	 * 
	 * @param packId
	 * @param fromUserId
	 * @param userIds
	 * @throws PackPackException
	 */
	public void forwardPack(String packId, String fromUserId, String ...userIds) 
			throws PackPackException;
	
	/**
	 * 
	 * @param userId
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public List<JPack> loadLatestPack(String userId, int pageNo) 
			throws PackPackException;
}