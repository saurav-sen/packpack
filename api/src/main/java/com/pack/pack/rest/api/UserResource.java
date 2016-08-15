package com.pack.pack.rest.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.IUserService;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.User;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.JUsers;
import com.pack.pack.model.web.dto.SignupDTO;
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
	
	private JStatus doRegisterUser(String name, String email,
			String password, String city, String country, String dob)
			throws PackPackException {
		String profilePictureFileName = null;
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
				null, profilePictureFileName);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus registerUser(String json) throws PackPackException {
		SignupDTO dto = JSONUtil.deserialize(json, SignupDTO.class, true);
		String name = dto.getName();
		String email = dto.getEmail();
		String password = dto.getPassword();
		String dob = dto.getDob();
		String city = dto.getCity();
		String country = dto.getCountry();
		return doRegisterUser(name, email, password, city, country, dob);
	}
}