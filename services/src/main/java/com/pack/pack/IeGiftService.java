package com.pack.pack;

import java.io.InputStream;
import java.util.List;

import com.pack.pack.model.web.JeGift;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface IeGiftService {
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws PackPackException
	 */
	public JeGift getEGiftById(String id) throws PackPackException;

	/**
	 * 
	 * @param jsonBody
	 * @param attachment
	 * @param userId
	 * @throws PackPackException
	 */
	public void uploadEGift(String jsonBody, InputStream attachment,
			String userId) throws PackPackException;

	/**
	 * 
	 * @param eGiftId
	 * @param fromUserId
	 * @param userIds
	 * @throws PackPackException
	 */
	public void sendEGift(String eGiftId, String fromUserId, String... userIds)
			throws PackPackException;

	/**
	 * 
	 * @param category
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public List<JeGift> loadeGiftsByCategory(String category, int pageNo)
			throws PackPackException;
	
	/**
	 * 
	 * @param brandId
	 * @param pageNo
	 * @return
	 * @throws PackPackException
	 */
	public List<JeGift> loadeGiftsByBrand(String brandId, int pageNo)
			throws PackPackException;
}