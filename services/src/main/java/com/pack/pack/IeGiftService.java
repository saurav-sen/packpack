package com.pack.pack;

import java.io.InputStream;
import java.util.List;

import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.web.response.model.JeGiftWeb;

/**
 * 
 * @author Saurav
 *
 */
public interface IeGiftService {

	public void uploadEGift(String jsonBody, InputStream attachment,
			String userId) throws PackPackException;

	public void sendEGift(String eGiftId, String fromUserId, String... userIds)
			throws PackPackException;

	public List<JeGiftWeb> loadeGifts(String category, int pageNo)
			throws PackPackException;
}