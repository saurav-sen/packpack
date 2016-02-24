package com.pack.pack.rest.api;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.pack.pack.IUserService;
import com.pack.pack.model.User;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.JUsers;
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
	@Path("name/{namePattern}")
	@Produces(MediaType.APPLICATION_JSON)
	public JUsers getUsersByName(@PathParam("namePattern") String namePattern)
			throws PackPackException {
		SearchService service = ServiceRegistry.INSTANCE
				.findService(SearchService.class);
		List<User> users = service.searchUserByName(namePattern);
		JUsers jUsers = new JUsers();
		for (User user : users) {
			JUser jUser = ModelConverter.convert(user);
			jUsers.getUsers().add(jUser);
		}
		return jUsers;
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public JStatus registerUser(
			@FormDataParam("name") String name,
			@FormDataParam("email") String email,
			@FormDataParam("password") String password,
			@FormDataParam("city") String city,
			@FormDataParam("country") String country,
			@FormDataParam("state") String state,
			@FormDataParam("locality") String locality,
			@FormDataParam("dob") String dob,
			@FormDataParam("profilePicture") InputStream profilePicture,
			@FormDataParam("profilePicture") FormDataContentDisposition aboutProfilePicture)
			throws PackPackException {
		String profilePictureFileName = aboutProfilePicture.getFileName();
		IUserService service = ServiceRegistry.INSTANCE
				.findCompositeService(IUserService.class);
		return service.registerNewUser(name, email, password, city, country,
				state, locality, dob, profilePicture, profilePictureFileName);
	}
}