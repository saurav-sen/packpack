package com.pack.pack.services;

import static com.pack.pack.util.AttachmentUtil.resizeAndStoreUploadedAttachment;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IUserService;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.es.ESUploadService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;
import com.pack.pack.util.S3Path;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class UserServiceImpl implements IUserService {

	@Override
	public JStatus registerNewUser(String name, String email, String password,
			String city, String dob, InputStream profilePicture,
			String profilePictureFileName) throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = new User();
		user.setName(name);
		user.setUsername(email);
		user.setPassword(password);
		user.setCity(city);
		user.setDob(dob);
		service.add(user);
		JStatus status = new JStatus();
		List<User> users = service.getBasedOnUsername(email);
		if (users == null || users.isEmpty()) {
			status.setStatus(StatusType.ERROR);
			status.setInfo("Internal Server Error. Failed to register user: "
					+ email);
			return status;
		}
		user = users.get(0);
		if(profilePicture != null) {
			String profilePictureUrl = storeProfilePicture(user.getId(),
					profilePicture, profilePictureFileName);
			user.setProfilePicture(profilePictureUrl);
		}
		service.update(user);
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully registered the user " + email);
		ESUploadService esService = ServiceRegistry.INSTANCE
				.findService(ESUploadService.class);
		esService.uploadNewUserDetails(user);
		return status;
	}
	
	@Override
	public JUser findUserById(String userId) throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = service.get(userId);
		return ModelConverter.convert(user);
	}

	@Override
	public JUser uploadProfilePicture(String userId,
			InputStream profilePicture, String profilePictureFileName)
			throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = service.get(userId);
		String profilePictureUrl = storeProfilePicture(userId, profilePicture,
				profilePictureFileName);
		user.setProfilePicture(profilePictureUrl);
		service.update(user);
		JUser jUser = ModelConverter.convert(user);
		return jUser;
	}

	private String storeProfilePicture(String userId,
			InputStream profilePicture, String profilePictureFileName)
			throws PackPackException {
		String home = SystemPropertyUtil.getProfilePictureHome();
		String location = home;
		if (!location.endsWith(File.separator)) {
			location = location + File.separator;
		}
		location = location + userId;
		File f = new File(location);
		if (!f.exists()) {
			f.mkdir();
		}
		location = location + File.separator + profilePictureFileName;
		S3Path root = new S3Path(userId, false);
		root.addChild(new S3Path(profilePictureFileName, true));
		resizeAndStoreUploadedAttachment(profilePicture, location, 30, 30, root);
		return location.substring(home.length());
	}
}