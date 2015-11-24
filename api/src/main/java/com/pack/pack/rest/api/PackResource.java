package com.pack.pack.rest.api;

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

import com.pack.pack.IPackService;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPacks;
import com.pack.pack.model.web.JStatus;
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
@Path("/pack")
public class PackResource {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JPack getById(@PathParam("id") String id) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		return service.getPackById(id);
	}

	@GET
	@Path("usr/{userId}/page/{pageNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public JPacks getAll(@PathParam("userId") String userId,
			@PathParam("pageNo") int pageNo) throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		List<JPack> list = service.loadLatestPack(userId, pageNo);
		JPacks jPacks = new JPacks();
		if (list != null) {
			jPacks.getPacks().addAll(list);
		}
		return jPacks;
	}

	@PUT
	@Path("usr/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JPack uploadPack(@PathParam("userId") String userId, JPack pack)
			throws PackPackException {
		// IServiceComposite service =
		// ServiceRegistry.INSTANCE.findCompositeService();
		// service.uploadPack(arg0, arg1, arg2);
		return null;
	}

	@POST
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JStatus forwardPack(ForwardDTO dto, @PathParam("packId") String id)
			throws PackPackException {
		IPackService service = ServiceRegistry.INSTANCE
				.findCompositeService(IPackService.class);
		String fromUserId = dto.getFromUserId();
		String toUserId = dto.getToUserId();
		service.forwardPack(id, fromUserId, toUserId);
		JStatus status = new JStatus();
		status.setStatus(StatusType.OK);
		status.setInfo("Successfully forwarded");
		return status;
	}
}