package com.pack.pack.services.es;

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
		return IndexSearchService.INSTANCE.searchCityByName(pattern);
	}

	public List<UserDetail> searchUserByName(String pattern)
			throws PackPackException {
		return IndexSearchService.INSTANCE.searchUserByName(pattern);
	}

	public List<TopicDetail> searchTopic(String pattern)
			throws PackPackException {
		return IndexSearchService.INSTANCE.searchTopic(pattern);
	}
}