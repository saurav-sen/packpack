package com.pack.pack.rest.api;

import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/egifts")
public class EGiftResource {

	/*@GET
	@CompressWrite
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JeGift getEGiftById(@PathParam("id") String id)
			throws PackPackException {
		IeGiftService service = ServiceRegistry.INSTANCE
				.findCompositeService(IeGiftService.class);
		return service.getEGiftById(id);
	}

	@GET
	@CompressWrite
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
	@CompressWrite
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
	@CompressRead
	@CompressWrite
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
	}*/
}