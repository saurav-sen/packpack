package com.pack.pack.rest.api;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.pack.pack.model.CategoryName;
import com.pack.pack.model.web.Info;
import com.pack.pack.model.web.JCategories;
import com.pack.pack.model.web.JCategory;
import com.pack.pack.model.web.SystemInfo;
import com.pack.pack.model.web.Timestamp;
import com.pack.pack.rest.api.security.interceptors.CompressWrite;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
@Singleton
@Provider
@Path("/sys.info")
public class SystemInfoResource {

	@GET
	@CompressWrite
	@Produces(value = MediaType.APPLICATION_JSON)
	public SystemInfo getSystemInfo() {
		SystemInfo sysInfo = new SystemInfo();
		Info info = new Info();
		info.setKey("default.topic.id");
		info.setValue(SystemPropertyUtil.getDefaultSystemTopicId());
		sysInfo.getInfos().add(info);
		return sysInfo;
	}

	@GET
	@Path("categories")
	@Produces(value = MediaType.APPLICATION_JSON)
	public JCategories getSupportedCategories() {
		JCategories result = new JCategories();
		CategoryName[] categoryNames = CategoryName.values();
		for (CategoryName categoryName : categoryNames) {
			String name = categoryName.name();
			String label = categoryName.getDisplay();
			JCategory category = new JCategory();
			category.setName(name);
			category.setLabel(label);
			result.getCategories().add(category);
		}
		return result;
	}

	@GET
	@Path("ntp")
	@Produces(value = MediaType.APPLICATION_JSON)
	public Timestamp ntp() {
		return new Timestamp(System.currentTimeMillis(),
				TimeUnit.MILLISECONDS.name());
	}
}