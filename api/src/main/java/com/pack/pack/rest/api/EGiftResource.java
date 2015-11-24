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

import com.pack.pack.IeGiftService;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.JeGift;
import com.pack.pack.model.web.JeGifts;
import com.pack.pack.model.web.StatusType;
import com.pack.pack.model.web.dto.ForwardDTO;
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
	@Path("brand/{brandId}/page/{pageNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public JeGifts getEGiftsById(@PathParam("brandId") String brandId,
			@PathParam("pageNo") int pageNo) throws PackPackException {
		IeGiftService service = ServiceRegistry.INSTANCE
				.findCompositeService(IeGiftService.class);
		List<JeGift> list = service.loadeGiftsByBrand(brandId, pageNo);
		JeGifts jEGifts = new JeGifts();
		if (list != null) {
			jEGifts.geteGifts().addAll(list);
		}
		return jEGifts;
	}

	@GET
	@Path("category/{category}/page/{pageNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public JeGifts getEGiftsByCategory(@PathParam("category") String category,
			@PathParam("pageNo") int pageNo) throws PackPackException {
		IeGiftService service = ServiceRegistry.INSTANCE
				.findCompositeService(IeGiftService.class);
		List<JeGift> list = service.loadeGiftsByCategory(category, pageNo);
		JeGifts jEGifts = new JeGifts();
		if (list != null) {
			jEGifts.geteGifts().addAll(list);
		}
		return jEGifts;
	}

	@POST
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JStatus forwardEGift(ForwardDTO dto, @PathParam("id") String id)
			throws PackPackException {
		String fromUserId = dto.getFromUserId();
		String toUserId = dto.getToUserId();
		IeGiftService service = ServiceRegistry.INSTANCE
				.findCompositeService(IeGiftService.class);
		service.sendEGift(id, fromUserId, toUserId);
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully sent the eGift");
		return status;
	}
}