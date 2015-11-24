package com.pack.pack.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IeGiftService;
import com.pack.pack.model.web.JeGift;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class EGiftServiceImpl implements IeGiftService {

	@Override
	public JeGift getEGiftById(String id) throws PackPackException {
		// TODO Auto-generated method stub
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
	public List<JeGift> loadeGiftsByCategory(String category, int pageNo)
			throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JeGift> loadeGiftsByBrand(String brandId, int pageNo)
			throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}

}
