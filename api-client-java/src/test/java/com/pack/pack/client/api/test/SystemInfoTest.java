package com.pack.pack.client.api.test;

import static com.pack.pack.client.api.test.TestConstants.BASE_URL;

import java.util.List;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JCategories;
import com.pack.pack.model.web.JCategory;

/**
 * 
 * @author Saurav
 *
 */
public class SystemInfoTest {

	public void test() throws Exception {
		API api = APIBuilder.create(BASE_URL)
				.setAction(COMMAND.GET_ALL_SYSTEM_SUPPORTED_CATEGORIES).build();
		JCategories categories = (JCategories) api.execute();
		List<JCategory> list = categories.getCategories();
		for (JCategory l : list) {
			System.out.println(l.getLabel() + "::" + l.getName());
		}
	}
}