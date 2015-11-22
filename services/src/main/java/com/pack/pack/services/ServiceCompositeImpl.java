package com.pack.pack.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IServiceComposite;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.web.response.model.JPackWeb;
import com.pack.pack.web.response.model.JeGiftWeb;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class ServiceCompositeImpl implements IServiceComposite {

	@Override
	public void uploadPack(String jsonBody, InputStream attachment,
			String userId) throws PackPackException {

	}

	@Override
	public void forwardPack(String packId, String fromUserId, String... userIds)
			throws PackPackException {

	}

	@Override
	public List<JPackWeb> loadLatestPack(String userId, int pageNo)
			throws PackPackException {
		return null;
	}

	@Override
	public void uploadEGift(String jsonBody, InputStream attachment,
			String userId) throws PackPackException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEGift(String eGiftId, String fromUserId, String... userIds)
			throws PackPackException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<JeGiftWeb> loadeGifts(String category, int pageNo)
			throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}
}