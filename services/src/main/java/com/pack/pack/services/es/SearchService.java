package com.pack.pack.services.es;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.es.CityLocation;
import com.pack.pack.model.es.TopicDetail;
import com.pack.pack.model.es.UserDetail;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class SearchService {

	public List<CityLocation> searchCityByName(String pattern)
			throws PackPackException {
		// TODO -- call an check this once we add ES infrastructure
		//return IndexSearchService.INSTANCE.searchCityByName(pattern);
		return Collections.emptyList();
	}

	public List<UserDetail> searchUserByName(String pattern)
			throws PackPackException {
		// TODO -- call an check this once we add ES infrastructure
		//return IndexSearchService.INSTANCE.searchUserByName(pattern);
		return Collections.emptyList();
	}

	public List<TopicDetail> searchTopic(String pattern)
			throws PackPackException {
		// TODO -- call an check this once we add ES infrastructure
		//return IndexSearchService.INSTANCE.searchTopic(pattern);
		return Collections.emptyList();
	}
}