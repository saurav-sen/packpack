package com.pack.pack;

import java.io.InputStream;

import com.pack.pack.model.web.JeGift;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.PackReceipent;
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
	 * @param title
	 * @param category
	 * @param brandId
	 * @param brandInfo
	 * @param file
	 * @param fileName
	 * @param userId
	 * @return
	 * @throws PackPackException
	 */
	public JeGift uploadEGift(String title, String category, String brandId,
			String brandInfo, InputStream file, String fileName, String userId)
			throws PackPackException;

	/**
	 * 
	 * @param eGiftId
	 * @param fromUserId
	 * @param title
	 * @param message
	 * @param receipents
	 * @throws PackPackException
	 */
	public void sendEGift(String eGiftId, String fromUserId, String title,
			String message, PackReceipent... receipents) throws PackPackException;

	/**
	 * 
	 * @param category
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JeGift> loadeGiftsByCategory(String category,
			String pageLink) throws PackPackException;

	/**
	 * 
	 * @param brandId
	 * @param pageLink
	 * @return
	 * @throws PackPackException
	 */
	public Pagination<JeGift> loadeGiftsByBrand(String brandId, String pageLink)
			throws PackPackException;
}