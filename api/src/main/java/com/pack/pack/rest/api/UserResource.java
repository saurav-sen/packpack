package com.pack.pack.rest.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.JUsers;
import com.pack.pack.model.web.dto.UserDTO;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/users")
public class UserResource {

	@GET
	@Path("id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JUser getUserById(@PathParam("id") String id)
			throws PackPackException {
		return null;
	}

	@GET
	@Path("username/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public JUser getUserByUsername(@PathParam("username") String username)
			throws PackPackException {
		return null;
	}

	@GET
	@Path("name/{namePattern}")
	@Produces(MediaType.APPLICATION_JSON)
	public JUsers getUsersByName(@PathParam("namePattern") String namePattern)
			throws PackPackException {
		return null;
	}
	
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public JUser login(UserDTO loginDTO) throws PackPackException {
		return null;
	}
}