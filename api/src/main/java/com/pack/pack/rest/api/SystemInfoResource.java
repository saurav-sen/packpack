package com.pack.pack.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.Info;
import com.pack.pack.model.web.SystemInfo;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Provider
@Path("/sys.info")
public class SystemInfoResource {

	@GET
	@Produces(value=MediaType.APPLICATION_JSON)
	public SystemInfo getSystemInfo() {
		SystemInfo sysInfo = new SystemInfo();
		Info info = new Info();
		info.setKey("default.topic.id");
		info.setValue(SystemPropertyUtil.getDefaultSystemTopicId());
		sysInfo.getInfos().add(info);
		return sysInfo;
	}
}