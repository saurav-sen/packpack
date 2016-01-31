package com.pack.pack.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.web.Info;
import com.pack.pack.model.web.SystemInfo;
import static com.pack.pack.util.SystemPropertyUtil.DEFAULT_TOPIC_ID_KEY;
import static com.pack.pack.util.SystemPropertyUtil.DEFAULT_TOPIC_ID_VALUE;;

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
		info.setKey(DEFAULT_TOPIC_ID_KEY);
		info.setValue(DEFAULT_TOPIC_ID_VALUE);
		sysInfo.getInfos().add(info);
		return sysInfo;
	}
}