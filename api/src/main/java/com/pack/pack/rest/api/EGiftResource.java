package com.pack.pack.rest.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JeGift;
import com.pack.pack.model.web.JeGifts;
import com.pack.pack.model.web.dto.ForwardDTO;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/egifts")
public class EGiftResource {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JeGift getEGiftById(@PathParam("id") String id)
			throws PackPackException {
		return null;
	}

	@GET
	@Path("brand/{brandId}/page/{pageNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public JeGifts getEGiftsById(@PathParam("brandId") String brandId,
			@PathParam("pageNo") int pageNo) throws PackPackException {
		return null;
	}

	@POST
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JStatus forwardEGift(ForwardDTO dto, @PathParam("id") String id)
			throws PackPackException {
		return null;
	}
}