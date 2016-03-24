package com.pack.pack.rest.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.IeGiftService;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JeGift;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.EGiftForwardDTO;
import com.pack.pack.model.web.dto.PackReceipent;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;

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
		IeGiftService service = ServiceRegistry.INSTANCE
				.findCompositeService(IeGiftService.class);
		return service.getEGiftById(id);
	}

	@GET
	@Path("brand/{brandId}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JeGift> getEGiftsById(
			@PathParam("brandId") String brandId,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IeGiftService service = ServiceRegistry.INSTANCE
				.findCompositeService(IeGiftService.class);
		return service.loadeGiftsByBrand(brandId, pageLink);
	}

	@GET
	@Path("category/{category}/page/{pageLink}")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination<JeGift> getEGiftsByCategory(
			@PathParam("category") String category,
			@PathParam("pageLink") String pageLink) throws PackPackException {
		IeGiftService service = ServiceRegistry.INSTANCE
				.findCompositeService(IeGiftService.class);
		return service.loadeGiftsByCategory(category, pageLink);
	}

	@PUT
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JStatus forwardEGift(EGiftForwardDTO dto, @PathParam("id") String id)
			throws PackPackException {
		String fromUserId = dto.getFromUserId();
		List<PackReceipent> receipents = dto.getReceipents();
		IeGiftService service = ServiceRegistry.INSTANCE
				.findCompositeService(IeGiftService.class);
		service.sendEGift(id, fromUserId, dto.getTitle(), dto.getMessage(),
				receipents.toArray(new PackReceipent[receipents.size()]));
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully sent the eGift");
		return status;
	}
}