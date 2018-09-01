package com.pack.pack.services;

import static com.pack.pack.util.AttachmentUtil.storeProfilePictureImage;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.IUserService;
import com.pack.pack.model.User;
import com.pack.pack.model.UserLocation;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.UserSettings;
import com.pack.pack.services.aws.S3Path;
import com.pack.pack.services.couchdb.UserLocationRepositoryService;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.es.ESUploadService;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.GeoLocationUtil;
import com.pack.pack.util.GeoLocationUtil.GeoLocation;
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
	
	private static final Logger $_LOG = LoggerFactory
			.getLogger(UserServiceImpl.class);
	
	@Override
	public JUser registerNewUser(String name, String email, double longitude,
			double latitude, InputStream profilePicture,
			String profilePictureFileName) throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = new User();
		user.setName(name);
		user.setUsername(email);
		List<User> users = service.getBasedOnUsername(email);
		if (users == null || users.isEmpty()) {
			service.add(user);
		} else if(users.size() > 1) {
			$_LOG.error("Multiple Users found with Email = " + email);
			return ModelConverter.convert(users.get(0));
		} else {
			user = users.get(0);
			service.update(user);
		}
		users = service.getBasedOnUsername(email);
		if (users == null || users.isEmpty()) {
			throw new PackPackException("", "Internal Server Error. Failed to register user: "
					+ email);
		}
		user = users.get(0);
		if (profilePicture != null) {
			String profilePictureUrl = storeProfilePicture(user.getId(),
					profilePicture, profilePictureFileName);
			user.setProfilePicture(profilePictureUrl);
			service.update(user);
		}
		
		$_LOG.info("Successfully registered the user " + email);

		if (longitude > 0 && latitude > 0) {
			boolean isNew = false;
			UserLocationRepositoryService service2 = ServiceRegistry.INSTANCE
					.findService(UserLocationRepositoryService.class);
			UserLocation userLocation = service2.findUserLocationById(user.getId());
			if(userLocation == null) {
				userLocation = new UserLocation();
				isNew = true;
			}
			userLocation.setUserId(user.getId());
			userLocation.setLongitude(String.valueOf(longitude));
			userLocation.setLatitude(String.valueOf(latitude));
			if(isNew) {
				service2.add(userLocation);
			} else {
				service2.update(userLocation);
			}
		}

		/*ESUploadService esService = ServiceRegistry.INSTANCE
				.findService(ESUploadService.class);
		esService.uploadNewUserDetails(user);*/
		return ModelConverter.convert(user);
	}
	
	/*@Override
	public JUser registerNewUser(String name, String email, String password,
			double longitude, double latitude, InputStream profilePicture,
			String profilePictureFileName) throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = new User();
		user.setName(name);
		user.setUsername(email);
		user.setPassword(password);
		service.add(user);
		JStatus status = new JStatus();
		List<User> users = service.getBasedOnUsername(email);
		if (users == null || users.isEmpty()) {
			throw new PackPackException("", "Internal Server Error. Failed to register user: "
					+ email);
		}
		user = users.get(0);
		if (profilePicture != null) {
			String profilePictureUrl = storeProfilePicture(user.getId(),
					profilePicture, profilePictureFileName);
			user.setProfilePicture(profilePictureUrl);
		}
		service.update(user);
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully registered the user " + email);

		if (longitude > 0 && latitude > 0) {
			UserLocation userLocation = new UserLocation();
			userLocation.setUserId(user.getId());
			userLocation.setLongitude(String.valueOf(longitude));
			userLocation.setLatitude(String.valueOf(latitude));
			UserLocationRepositoryService service2 = ServiceRegistry.INSTANCE
					.findService(UserLocationRepositoryService.class);
			service2.add(userLocation);
		}

		ESUploadService esService = ServiceRegistry.INSTANCE
				.findService(ESUploadService.class);
		esService.uploadNewUserDetails(user);
		return ModelConverter.convert(user);
	}*/

	@Override
	public JUser registerNewUser(String name, String email, String password,
			String city, String country, String dob,
			InputStream profilePicture, String profilePictureFileName)
			throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = new User();
		user.setName(name);
		user.setUsername(email);
		user.setPassword(password);
		user.setCity(city);
		user.setCountry(country);
		user.setDob(dob);
		List<User> users = service.getBasedOnUsername(email);
		if (users != null && !users.isEmpty()) {
			throw new PackPackException("", "User already exists : "
					+ email);
		}
		service.add(user);
		JStatus status = new JStatus();
		users = service.getBasedOnUsername(email);
		if (users == null || users.isEmpty()) {
			throw new PackPackException("", "Internal Server Error. Failed to register user: "
					+ email);
		}
		user = users.get(0);
		if (profilePicture != null) {
			String profilePictureUrl = storeProfilePicture(user.getId(),
					profilePicture, profilePictureFileName);
			user.setProfilePicture(profilePictureUrl);
		}
		service.update(user);
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully registered the user " + email);

		GeoLocation geoLocation = GeoLocationUtil.resolveGeoLocation(null, city,
				country);
		if (geoLocation != null) {
			UserLocation userLocation = new UserLocation();
			userLocation.setUserId(user.getId());
			userLocation
					.setLongitude(String.valueOf(geoLocation.getLongitude()));
			userLocation.setLatitude(String.valueOf(geoLocation.getLatitude()));
			UserLocationRepositoryService service2 = ServiceRegistry.INSTANCE
					.findService(UserLocationRepositoryService.class);
			service2.add(userLocation);
		}

		ESUploadService esService = ServiceRegistry.INSTANCE
				.findService(ESUploadService.class);
		esService.uploadNewUserDetails(user);
		return ModelConverter.convert(user);
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
		String relativeUrl = userId + "/" + profilePictureFileName;
		storeProfilePictureImage(profilePicture, location, 30, 30, root, relativeUrl);
		return location.substring(home.length());
	}
	
	@Override
	public JUser updateUserSettings(String userId, String key, String value)
			throws PackPackException {
		if (key == null || key.trim().isEmpty()) {
			throw new PackPackException(ErrorCodes.PACK_ERR_72, "Invalid Key.");
		}
		if (value == null || value.trim().isEmpty()) {
			throw new PackPackException(ErrorCodes.PACK_ERR_72,
					"Invalid value.");
		}
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = service.get(userId);
		if (user == null) {
			throw new PackPackException(ErrorCodes.PACK_ERR_73,
					"No user found with supplied ID.");
		}
		boolean bool = false;
		if (UserSettings.USER_NAME.equals(key) && value != null
				&& !value.trim().isEmpty()) {
			user.setName(value);
			bool = true;
		} else if (UserSettings.DISPLAY_NAME.equals(key) && value != null
				&& !value.trim().isEmpty()) {
			user.setDisplayName(value);
			bool = true;
		} else if (UserSettings.USER_ADDRESS.equals(key)) {
			String[] split = value.split(", ");
			if (split.length != 2) {
				throw new PackPackException(ErrorCodes.PACK_ERR_81,
						"Invalid value " + value);
			}
			user.setCity(split[0]);
			user.setCountry(split[1]);
			bool = true;
		}
		if (bool) {
			service.update(user);
		}
		return ModelConverter.convert(user);
	}
	
	@Override
	public boolean checkIfUserNameExists(String userName)
			throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE.findService(UserRepositoryService.class);
		List<User> users = service.getBasedOnUsername(userName);
		if(users == null || users.isEmpty()) {
			return false;
		}
		return true;
	}
	
	@Override
	public void updateUserPassword(String userName, String passwd)
			throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		List<User> users = service.getBasedOnUsername(userName);
		if (users == null || users.isEmpty() || users.size() > 1) {
			throw new PackPackException(ErrorCodes.PACK_ERR_01,
					"Invalid UserName/Email specified");
		}
		User user = users.get(0);
		user.setPassword(passwd);
		service.update(user);
	}
}