package com.pack.pack.rest.api;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

import com.pack.pack.IUserService;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.User;
import com.pack.pack.model.UserInfo;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.JUsers;
import com.pack.pack.model.web.dto.SignupDTO;
import com.pack.pack.model.web.dto.UserSettings;
import com.pack.pack.rest.api.security.interceptors.Compress;
import com.pack.pack.security.util.EncryptionUtil;
import com.pack.pack.services.couchdb.UserRepositoryService;
import com.pack.pack.services.es.SearchService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/user")
public class UserResource {

	@GET
	@Compress
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
	@Compress
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
	@Compress
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

	/*@POST
	@Path("register")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus registerUser(
			@FormDataParam("name") String name,
			@FormDataParam("email") String email,
			@FormDataParam("password") String password,
			@FormDataParam("city") String city,
			@FormDataParam("dob") String dob,
			@FormDataParam("profilePicture") InputStream profilePicture,
			@FormDataParam("profilePicture") FormDataContentDisposition aboutProfilePicture)
			throws PackPackException {
		String profilePictureFileName = aboutProfilePicture.getFileName();
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
		password = EncryptionUtil.encryptPassword(password);
		return service.registerNewUser(name, email, password, city, dob,
				profilePicture, profilePictureFileName);
	}*/
	
	@PUT
	@Compress
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
	
	private JUser doRegisterUser(String name, String email,
			String password, String city, String country, String dob)
			throws PackPackException {
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
		password = EncryptionUtil.encryptPassword(password);
		return service.registerNewUser(name, email, password, city, country, dob,
				null, null);
	}

	@POST
	@Compress
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
	@Compress
	@Path("id/{id}/follow/category")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public JUser editUserFollowedCategories(
			@PathParam("id") String userId, String followedCategories)
			throws PackPackException {
		IUserService service = ServiceRegistry.INSTANCE
				.findCompositeService(IUserService.class);
		String[] split = followedCategories
				.split(UserInfo.FOLLOWED_CATEGORIES_SEPARATOR);
		List<String> categories = Arrays.asList(split);
		service.editUserFollowedCategories(userId, categories);
		return getUserById(userId);
	}
	
	@GET
	@Compress
	@Path("id/{id}/follow/category")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserFollowedCategories(
			@PathParam("id") String userId) throws PackPackException {
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
		if(list != null) {
			return JSONUtil.serialize(list, false);
		}
		return JSONUtil.serialize(Collections.EMPTY_LIST, false);
	}
	
	@PUT
	@Compress
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
}