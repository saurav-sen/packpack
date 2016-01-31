package com.pack.pack.services;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IeGiftService;
import com.pack.pack.model.EGift;
import com.pack.pack.model.web.JeGift;
import com.pack.pack.services.couchdb.EGiftRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.AttachmentUtil;
import com.pack.pack.util.ModelConverter;
import com.pack.pack.util.SystemPropertyUtil;

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
		EGiftRepositoryService service = ServiceRegistry.INSTANCE
				.findService(EGiftRepositoryService.class);
		EGift eGift = service.get(id);
		JeGift jeGift = ModelConverter.convert(eGift);
		return jeGift;
	}

	@Override
	public JeGift uploadEGift(String title, String category, String brandId,
			String brandInfo, InputStream file, String fileName, String userId)
			throws PackPackException {
		EGift eGift = addNewEGift(title, category, brandId, brandInfo);
		String home = SystemPropertyUtil.getEGiftImageHome();
		String location = home + File.separator + category;
		File f = new File(location);
		if (!f.exists()) {
			f.mkdir();
		}
		location = location + File.separator + eGift.getId();
		f = new File(location);
		if (!f.exists()) {
			f.mkdir();
		}
		location = location + fileName;
		File originalFile = AttachmentUtil.storeUploadedAttachment(file,
				location);
		File thumbnailFile = AttachmentUtil
				.createThumnailForImage(originalFile);
		String thumbnailFileLocation = thumbnailFile.getAbsolutePath();
		eGift.setImageUrl(location.substring(home.length()));
		eGift.setImageThumbnailUrl(thumbnailFileLocation.substring(home
				.length()));
		EGiftRepositoryService service = ServiceRegistry.INSTANCE
				.findService(EGiftRepositoryService.class);
		service.update(eGift);
		JeGift jeGift = ModelConverter.convert(eGift);
		return jeGift;
	}

	private EGift addNewEGift(String title, String category, String brandId,
			String brandInfo) {
		EGift eGift = new EGift();
		eGift.setTitle(title);
		eGift.setBrandId(brandId);
		eGift.setBrandInfo(brandInfo);
		eGift.setCategory(category);
		EGiftRepositoryService service = ServiceRegistry.INSTANCE
				.findService(EGiftRepositoryService.class);
		service.add(eGift);
		return eGift;
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