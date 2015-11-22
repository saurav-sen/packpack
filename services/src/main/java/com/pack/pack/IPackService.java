package com.pack.pack;

import java.io.InputStream;
import java.util.List;

import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.web.response.model.JPackWeb;

/**
 * 
 * @author Saurav
 *
 */
public interface IPackService {

	/**
	 * 
	 * @param jsonBody
	 * @param attachment
	 * @param userId
	 * @throws PackPackException
	 */
	public void uploadPack(String jsonBody, InputStream attachment, 
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
	public List<JPackWeb> loadLatestPack(String userId, int pageNo) 
			throws PackPackException;
}