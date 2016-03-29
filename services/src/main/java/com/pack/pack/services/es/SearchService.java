package com.pack.pack.services.es;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.model.User;
import com.pack.pack.model.es.CityLocation;
import com.pack.pack.model.web.JTopics;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class SearchService {

	public List<CityLocation> searchCityByName(String pattern) throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> searchUserByName(String pattern) throws PackPackException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public JTopics searchTopic(String pattern) throws PackPackException {
		return null;
	}
}