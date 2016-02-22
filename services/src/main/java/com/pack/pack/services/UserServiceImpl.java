package com.pack.pack.services;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IUserService;
import com.pack.pack.model.Address;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.services.couchdb.UserRepositoryService;
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
public class UserServiceImpl implements IUserService {

	@Override
	public JStatus registerNewUser(String name, String email, String password,
			String city, String country, String state, String locality,
			String dob, InputStream profilePicture,
			String profilePictureFileName) throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = new User();
		user.setName(name);
		user.setUsername(email);
		user.setPassword(password);
		Address address = new Address();
		address.setCity(city);
		address.setCountry(country);
		address.setLocality(locality);
		address.setState(state);
		user.setAddress(address);
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
		String profilePictureUrl = storeProfilePicture(user.getId(),
				profilePicture, profilePictureFileName);
		user.setProfilePicture(profilePictureUrl);
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully registered the user " + email);
		return status;
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
		location = location + userId + File.separator + profilePictureFileName;
		AttachmentUtil.storeUploadedAttachment(profilePicture, location);
		return location.substring(home.length());
	}
}