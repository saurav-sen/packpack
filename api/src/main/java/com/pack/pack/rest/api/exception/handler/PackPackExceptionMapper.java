package com.pack.pack.rest.api.exception.handler;

import javax.json.JsonObjectBuilder;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.json.JsonProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.services.exception.ErrorCodes;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Provider
public class PackPackExceptionMapper implements ExceptionMapper<Throwable> {
	
	private static Logger logger = LoggerFactory.getLogger(PackPackExceptionMapper.class);

	@Override
	public Response toResponse(Throwable exception) {
		JsonObjectBuilder jsonObjectBuilder = new JsonProviderImpl().createBuilderFactory(null).createObjectBuilder();
		if(exception != null) {
			logger.info(exception.getMessage());
			if(exception.getCause() != null) {
				logger.info(exception.getCause().getMessage());
			}
			logger.trace(exception.getMessage(), exception);
		}
		Throwable ex = diggThrowable(exception);
		if(ex != null) {
			exception = ex;
		}
		if(exception instanceof PackPackException) {
			PackPackException packPackException = diggPackPackException((PackPackException)exception);
			jsonObjectBuilder.add(PackPackException.ERR_CODE, packPackException.getErrorCode());
			jsonObjectBuilder.add(PackPackException.ERR_MSG, packPackException.getMessage());
			int statusCode = 500;
			if(packPackException.isUserError()) {
				statusCode = 400;
			}
			return Response.status(statusCode).entity(jsonObjectBuilder.build())
					.type(MediaType.APPLICATION_JSON_TYPE).build();
		} else if(exception instanceof BadRequestException) {
			jsonObjectBuilder.add(PackPackException.ERR_CODE, ErrorCodes.PACK_ERR_92);
			jsonObjectBuilder.add(PackPackException.ERR_MSG, "Bad Request. Cause:: " + exception.getMessage());
			return Response.status(400).entity(jsonObjectBuilder.build())
					.type(MediaType.APPLICATION_JSON_TYPE).build();
		} else if(exception instanceof Exception) {
			jsonObjectBuilder.add(PackPackException.ERR_CODE, ErrorCodes.PACK_ERR_61);
			jsonObjectBuilder.add(PackPackException.ERR_MSG, "Internal Server Error");
			return Response.status(500).entity(jsonObjectBuilder.build()).type(MediaType.APPLICATION_JSON_TYPE)
					.build();
		} else {
			jsonObjectBuilder.add(PackPackException.ERR_CODE, ErrorCodes.PACK_ERR_61);
			jsonObjectBuilder.add(PackPackException.ERR_MSG, "Internal Server Error");
			return Response.status(500).entity(jsonObjectBuilder.build())
					.type(MediaType.APPLICATION_JSON_TYPE).build();
		}
	}
	
	private PackPackException diggPackPackException(PackPackException e) {
		PackPackException ex = e;
		int count = 0;
		Throwable e1 = e;
		while(e1.getCause() != null && count < 50) {
			if((e1.getCause() instanceof PackPackException)) {
				PackPackException pEx = (PackPackException)e1.getCause();
				if(ErrorCodes.PACK_ERR_63.equals(pEx.getErrorCode())) { //Do not unwrap it further as it will not be wise.
					return pEx;
				}
				ex = diggPackPackException((PackPackException)e1.getCause());
				break;
			}
			e1 = e1.getCause();
			count++;
		}
		return ex;
	}

	private Throwable diggThrowable(Throwable exception) {
		if(exception == null)
			return null;
		Throwable ex = exception;
		int count = 0;
		while(ex != null && !(ex instanceof PackPackException) && count < 50) {
			ex = exception.getCause();
			count++;
		}
		return ex;
	}
}