package com.pack.pack.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.JBrands;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/brands")
public class BrandInfoResource {

	@GET
	@CompressWrite
	@Path("companyName/{companyName}")
	@Produces(MediaType.APPLICATION_JSON)
	public JBrands getBrandsInfoByName(
			@PathParam("companyName") String companyName)
			throws PackPackException {
		return null;
	}
}