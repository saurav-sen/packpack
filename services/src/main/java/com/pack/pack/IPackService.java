package com.pack.pack;

import java.io.InputStream;

import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.PackReceipent;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.objects.BroadcastCriteria;

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
	 * @param id
	 * @return
	 * @throws PackPackException
	 */
	public JPackAttachment getPackAttachmentById(String id) throws PackPackException;

	/**
	 * 
	 * @param packId
	 * @param fromUserId
	 * @param receipents
	 * @throws PackPackException
	 */
	public void forwardPack(String packId, String fromUserId,
			PackReceipent... receipents) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param topicId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JPack> loadLatestPack(String userId, String topicId,
			String pageLink) throws PackPackException;

	/**
	 * 
	 * @param userId
	 * @param topicId
	 * @param packId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JPackAttachment> loadPackAttachments(String userId,
			String topicId, String packId, String pageLink)
			throws PackPackException;

	/**
	 * 
	 * @param file
	 * @param fileName
	 * @param title
	 * @param description
	 * @param story
	 * @param topicId
	 * @param userId
	 * @param mimeType
	 * @param type
	 * @param publish
	 * @return
	 * @throws PackPackException
	 */
	public JPack uploadPack(InputStream file, String fileName, String title,
			String description, String story, String topicId, String userId,
			String mimeType, PackAttachmentType type, boolean publish)
			throws PackPackException;

	/**
	 * 
	 * @param file
	 * @param fileName
	 * @param type
	 * @param packId
	 * @param topicId
	 * @param userId
	 * @return
	 * @throws PackPackException
	 */
	public JPack updatePack(InputStream file, String fileName,
			PackAttachmentType type, String packId, String topicId,
			String userId) throws PackPackException;

	/**
	 * 
	 * @param criteria
	 * @param packId
	 * @param userId
	 * @throws PackPackException
	 */
	public void broadcastPack(BroadcastCriteria criteria, String packId,
			String userId) throws PackPackException;

	/**
	 * 
	 * @param criteria
	 * @param packId
	 * @throws PackPackException
	 */
	public void broadcastSystemPack(BroadcastCriteria criteria, String packId)
			throws PackPackException;
	
	/**
	 * 
	 * @param pack
	 * @return
	 * @throws PackPackException
	 */
	public JPack createNewPack(Pack pack) throws PackPackException;
}