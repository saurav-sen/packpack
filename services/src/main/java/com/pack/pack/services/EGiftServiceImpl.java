package com.pack.pack.services;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IeGiftService;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.message.FwdPack;
import com.pack.pack.model.EGift;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.PackAttachmentType;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JeGift;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.dto.PackReceipent;
import com.pack.pack.model.web.dto.PackReceipentType;
import com.pack.pack.services.couchdb.EGiftRepositoryService;
import com.pack.pack.services.couchdb.PackRepositoryService;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.email.EmailSender;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.rabbitmq.MessagePublisher;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.services.sms.SMSSender;
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
	public void sendEGift(String eGiftId, String fromUserId, String title,
			String message, PackReceipent... receipents)
			throws PackPackException {
		if (receipents == null || receipents.length == 0)
			return;
		EGiftRepositoryService eGiftService = ServiceRegistry.INSTANCE
				.findService(EGiftRepositoryService.class);
		EGift eGift = eGiftService.get(eGiftId);
		String brandId = eGift.getBrandId();
		if(brandId == null) {
			brandId = CommonConstants.DEFAULT_EGIFT_TOPIC_ID;
		}
		brandId = "brand:" + brandId;
		Pack pack = createPack(eGift, fromUserId, title, message, brandId);

		UserRepositoryService userService = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);

		FwdPack fwdPack = new FwdPack();
		List<PackAttachment> packAttachments = pack.getPackAttachments();
		for (PackAttachment packAttachment : packAttachments) {
			JPackAttachment jPackAttachment = ModelConverter
					.convert(packAttachment);
			fwdPack.getAttachments().add(jPackAttachment);
		}
		fwdPack.setFromUserId(fromUserId);
		User user = userService.get(fromUserId);
		fwdPack.setFromUserName(user.getUsername());
		fwdPack.setFromUserProfilePicUrl(null);
		fwdPack.setLikes(pack.getLikes());
		fwdPack.setPackId(pack.getId());
		fwdPack.setViews(pack.getViews());

		for (PackReceipent receipent : receipents) {
			String toUserId = receipent.getToUserId();
			PackReceipentType type = receipent.getType();
			if (type == null) {
				type = PackReceipentType.USER;
			}
			switch (type) {
			case USER:
				MessagePublisher messagePublisher = ServiceRegistry.INSTANCE
						.findService(MessagePublisher.class);
				User toUser = userService.get(toUserId);
				fwdPack.setMessage("Received a Gift from: " + user.getName());
				messagePublisher.forwardPack(fwdPack, toUser);
				break;
			case EMAIL:
				EmailSender emailService = ServiceRegistry.INSTANCE
						.findService(EmailSender.class);
				emailService.forwardPack(fwdPack, toUserId);
				break;
			case SMS:
				SMSSender smsService = ServiceRegistry.INSTANCE
						.findService(SMSSender.class);
				smsService.forwardPack(fwdPack, toUserId);
				break;
			}
		}
	}

	private Pack createPack(EGift eGift, String creatorId, String title,
			String message, String topicId) {
		Pack pack = new Pack();
		pack.setCreatorId(creatorId);
		pack.setTopicId(topicId);
		pack.setCreationTime(new DateTime(DateTimeZone.getDefault()).getMillis());
		pack.setTitle(title + "[" + eGift.getTitle() + "]");
		pack.setStory(message);
		PackRepositoryService service = ServiceRegistry.INSTANCE
				.findService(PackRepositoryService.class);
		service.add(pack);
		PackAttachment packAttachment = new PackAttachment();
		packAttachment.setAttachmentThumbnailUrl(eGift.getImageThumbnailUrl());
		packAttachment.setAttachmentUrl(eGift.getImageUrl());
		packAttachment.setType(PackAttachmentType.IMAGE);
		pack.getPackAttachments().add(packAttachment);
		service.update(pack);
		return pack;
	}

	@Override
	public Pagination<JeGift> loadeGiftsByCategory(String category,
			String pageLink) throws PackPackException {
		EGiftRepositoryService service = ServiceRegistry.INSTANCE
				.findService(EGiftRepositoryService.class);
		Pagination<EGift> page = service.getBasedOnCategory(category, pageLink);
		String nextLink = page.getNextLink();
		String previousLink = page.getPreviousLink();
		List<EGift> eGifts = page.getResult();
		List<JeGift> jeGifts = new ArrayList<JeGift>();
		for (EGift eGift : eGifts) {
			JeGift jeGift = ModelConverter.convert(eGift);
			jeGifts.add(jeGift);
		}
		return new Pagination<JeGift>(previousLink, nextLink, jeGifts);
	}

	@Override
	public Pagination<JeGift> loadeGiftsByBrand(String brandId, String pageLink)
			throws PackPackException {
		EGiftRepositoryService service = ServiceRegistry.INSTANCE
				.findService(EGiftRepositoryService.class);
		Pagination<EGift> page = service.getBasedOnBrand(brandId, pageLink);
		String nextLink = page.getNextLink();
		String previousLink = page.getPreviousLink();
		List<EGift> eGifts = page.getResult();
		List<JeGift> jeGifts = new ArrayList<JeGift>();
		for (EGift eGift : eGifts) {
			JeGift jeGift = ModelConverter.convert(eGift);
			jeGifts.add(jeGift);
		}
		return new Pagination<JeGift>(previousLink, nextLink, jeGifts);
	}
}