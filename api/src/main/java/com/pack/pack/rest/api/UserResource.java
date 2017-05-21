package com.pack.pack.rest.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.IUserService;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.markup.gen.MarkupGenerator;
import com.pack.pack.model.User;
import com.pack.pack.model.UserInfo;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.JUsers;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.PasswordResetDTO;
import com.pack.pack.model.web.dto.SignupDTO;
import com.pack.pack.model.web.dto.UserSettings;
import com.pack.pack.rest.api.security.interceptors.CompressRead;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.security.util.EncryptionUtil;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.es.SearchService;
import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.ext.email.SmtpMessage;
import com.pack.pack.services.ext.email.SmtpTLSMessageService;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;

import freemarker.template.TemplateException;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/user")
public class UserResource {

	private Logger LOG = LoggerFactory.getLogger(UserResource.class);

	@GET
	@CompressWrite
	@Path("id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JUser getUserById(@PathParam("id") String id)
			throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = service.get(id);
		JUser jUser = ModelConverter.convert(user);
		return jUser;
	}

	@GET
	@CompressWrite
	@Path("username/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public JUser getUserByUsername(@PathParam("username") String username)
			throws PackPackException {
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		List<User> users = service.getBasedOnUsername(username);
		JUser jUser = null;
		if (users != null && !users.isEmpty()) {
			User user = users.get(0);
			jUser = ModelConverter.convert(user);
		}
		return jUser;
	}

	@GET
	@CompressWrite
	@Path("name/{namePattern}")
	@Produces(MediaType.APPLICATION_JSON)
	public JUsers getUsersByName(@PathParam("namePattern") String namePattern)
			throws PackPackException {
		SearchService service = ServiceRegistry.INSTANCE
				.findService(SearchService.class);
		List<UserDetail> users = service.searchUserByName(namePattern);
		JUsers jUsers = new JUsers();
		for (UserDetail user : users) {
			JUser jUser = ModelConverter.convert(user);
			jUsers.getUsers().add(jUser);
		}
		return jUsers;
	}

	/*
	 * @POST
	 * 
	 * @Path("register")
	 * 
	 * @Consumes(MediaType.MULTIPART_FORM_DATA)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public JStatus registerUser(
	 * 
	 * @FormDataParam("name") String name,
	 * 
	 * @FormDataParam("email") String email,
	 * 
	 * @FormDataParam("password") String password,
	 * 
	 * @FormDataParam("city") String city,
	 * 
	 * @FormDataParam("dob") String dob,
	 * 
	 * @FormDataParam("profilePicture") InputStream profilePicture,
	 * 
	 * @FormDataParam("profilePicture") FormDataContentDisposition
	 * aboutProfilePicture) throws PackPackException { String
	 * profilePictureFileName = aboutProfilePicture.getFileName(); IUserService
	 * service = ServiceRegistry.INSTANCE
	 * .findCompositeService(IUserService.class); UserRepositoryService
	 * repoService = ServiceRegistry.INSTANCE
	 * .findService(UserRepositoryService.class); List<User> users =
	 * repoService.getBasedOnUsername(email); if (users != null &&
	 * !users.isEmpty()) { throw new PackPackException("TODO",
	 * "Duplicate user. User with username = " + email + " already registered");
	 * } password = EncryptionUtil.encryptPassword(password); return
	 * service.registerNewUser(name, email, password, city, dob, profilePicture,
	 * profilePictureFileName); }
	 */

	@PUT
	@CompressRead
	@CompressWrite
	@Path("id/{id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public JUser uploadProfilePicture(
			@FormDataParam("profilePicture") InputStream profilePicture,
			@PathParam("id") String userId) throws PackPackException {
		String profilePictureFileName = userId + "_profilePicture.jpg";
		IUserService service = ServiceRegistry.INSTANCE
				.findCompositeService(IUserService.class);
		return service.uploadProfilePicture(userId, profilePicture,
				profilePictureFileName);
	}

	private JUser doRegisterUser(String name, String email, String password,
			String city, String country, String dob) throws PackPackException {
		IUserService service = ServiceRegistry.INSTANCE
				.findCompositeService(IUserService.class);
		UserRepositoryService repoService = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		List<User> users = repoService.getBasedOnUsername(email);
		if (users != null && !users.isEmpty()) {
			throw new PackPackException("TODO",
					"Duplicate user. User with username = " + email
							+ " already registered");
		}
		// password = EncryptionUtil.encryptPassword(password);
		JUser newUser = service.registerNewUser(name, email, password, city,
				country, dob, null, null);
		sendWelcomeMail(newUser);
		return newUser;
	}

	private void sendWelcomeMail(JUser newUser) {
		try {
			String htmlContent = MarkupGenerator.INSTANCE
					.generateWelcomeEmailHtmlContent(newUser.getName(), -1,
							"http://www.squill.co.in/");
			SmtpMessage smtpMessage = new SmtpMessage(newUser.getUsername(),
					"Welcome To SQUILL", htmlContent, true);
			SmtpTLSMessageService.INSTANCE.sendMessage(smtpMessage);
		} catch (IOException e) {
			LOG.error("Error Sending Welcome Mail", e);
		} catch (TemplateException e) {
			LOG.error("Error Sending Welcome Mail", e);
		}
	}

	@POST
	@CompressRead
	@CompressWrite
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JUser registerUser(String json) throws PackPackException {
		SignupDTO dto = JSONUtil.deserialize(json, SignupDTO.class, true);
		String name = dto.getName();
		String email = dto.getEmail();
		String password = dto.getPassword();
		String dob = dto.getDob();
		String city = dto.getCity();
		String country = dto.getCountry();
		return doRegisterUser(name, email, password, city, country, dob);
	}

	@PUT
	@CompressRead
	@CompressWrite
	@Path("id/{id}/follow/category")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public JUser editUserFollowedCategories(@PathParam("id") String userId,
			String followedCategories) throws PackPackException {
		IUserService service = ServiceRegistry.INSTANCE
				.findCompositeService(IUserService.class);
		String[] split = followedCategories
				.split(UserInfo.FOLLOWED_CATEGORIES_SEPARATOR);
		List<String> categories = Arrays.asList(split);
		service.editUserFollowedCategories(userId, categories);
		return getUserById(userId);
	}

	@GET
	@CompressWrite
	@Path("id/{id}/follow/category")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserFollowedCategories(@PathParam("id") String userId)
			throws PackPackException {
		List<String> list = null;
		UserRepositoryService service = ServiceRegistry.INSTANCE
				.findService(UserRepositoryService.class);
		User user = service.get(userId);
		List<UserInfo> infos = user.getExtraInfoMap();
		if (infos != null && !infos.isEmpty()) {
			for (UserInfo info : infos) {
				if (UserInfo.FOLLOWED_CATEGORIES.equals(info.getKey())) {
					String followedCategories = info.getValue();
					String[] split = followedCategories
							.split(UserInfo.FOLLOWED_CATEGORIES_SEPARATOR);
					list = Arrays.asList(split);
					break;
				}
			}
		}
		if (list != null) {
			return JSONUtil.serialize(list, false);
		}
		return JSONUtil.serialize(Collections.EMPTY_LIST, false);
	}

	@PUT
	@CompressRead
	@CompressWrite
	@Path("id/{id}/settings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JUser updateUserDetails(@PathParam("id") String userId, String json)
			throws PackPackException {
		UserSettings dto = JSONUtil.deserialize(json, UserSettings.class, true);
		IUserService service = ServiceRegistry.INSTANCE
				.findCompositeService(IUserService.class);
		return service.updateUserSettings(userId, dto.getKey(), dto.getValue());
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("passwd/reset/usr/{userName}")
	public JStatus issuePasswordResetVerifier(
			@PathParam("userName") String userName) throws PackPackException {
		try {
			// Generate OTP
			String OTP = UUID.randomUUID().toString();
			OTP = EncryptionUtil.generateSH1HashKey(OTP, true, true);
			OTP = String.valueOf(OTP.hashCode());

			// Prepare HTML email content containing OTP info
			String html = MarkupGenerator.INSTANCE
					.generatePasswordResetVerifierMail(OTP);

			// Send EMail using SMTP (over TLS)
			SmtpMessage msg = new SmtpMessage(userName,
					"SQUILL password assistance", html, true);
			SmtpTLSMessageService.INSTANCE.sendMessage(msg);

			// Store the OTP info for verification purpose (TTL=900 seconds/15
			// minutes)
			RedisCacheService service = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			service.addToCache("passwd_reset_" + userName, OTP, 900); // 15
																		// minutes
																		// TTL

			// Success
			JStatus status = new JStatus();
			status.setStatus(StatusType.OK);
			status.setInfo("Password Reset details sent over EMail @ "
					+ userName);
			return status;
		} catch (Exception e) {
			LOG.error("Failed Issuing password reset verifier code",
					e.getMessage(), e);
			throw new PackPackException(ErrorCodes.PACK_ERR_01,
					"Failed Issuing password reset verifier code");
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("passwd/reset/usr/{userName}")
	public JStatus resetPassword(@PathParam("userName") String userName,
			String json) {
		JStatus response = new JStatus();
		try {
			String OTP = null;

			PasswordResetDTO dto = JSONUtil.deserialize(json,
					PasswordResetDTO.class, true);
			String verifier = dto.getVerifier();
			String passwd = dto.getNewPassword();

			String key = "passwd_reset_" + userName;
			RedisCacheService service = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			OTP = service.getFromCache(key, String.class);

			if (OTP != null && OTP.equals(verifier)) {
				service.removeFromCache(key);
				IUserService userService = ServiceRegistry.INSTANCE
						.findCompositeService(IUserService.class);
				userService.updateUserPassword(userName, passwd);
				response.setInfo("Successfully Updated user credentials");
				response.setStatus(StatusType.OK);
				return response;
			}

			response.setInfo("Invalid verfier code");
			response.setStatus(StatusType.ERROR);
		} catch (Exception e) {
			response.setInfo("Failed verifying OTP for password reset");
			LOG.error("Failed verifying OTP for password reset",
					e.getMessage(), e);
		}
		return response;
	}
}