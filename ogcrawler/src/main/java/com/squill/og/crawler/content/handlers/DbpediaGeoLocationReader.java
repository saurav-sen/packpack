package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.squill.og.crawler.entity.extraction.Concept;
import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.services.exception.PackPackException;

public class DbpediaGeoLocationReader {
	
	private static final Logger LOG = LoggerFactory.getLogger(DbpediaGeoLocationReader.class);
	
	private static final String PREFIX = "http://dbpedia.org/resource/";
	
	private static final String[] ONTOLOGY_PLACE_TYPES = new String[] {
			"http://dbpedia.org/ontology/City",
			"http://dbpedia.org/ontology/PopulatedPlace",
			"http://dbpedia.org/ontology/Place",
			"http://dbpedia.org/ontology/Location" };
	
	private static final String[] ONTOLOGY_ORG_TYPES = new String[] {
		 "http://dbpedia.org/ontology/PoliticalParty",
		"http://dbpedia.org/ontology/Organisation",
        "http://dbpedia.org/ontology/Agent" };
	
	private static final String[] ONTOLOGY_PERSON_TYPES = new String[] {
		 "http://dbpedia.org/ontology/Person" };
	
	private List<String> resolvePlaceNamesForOrganization_Query(String orgName) {
		return Collections.emptyList();
	}
	
	private List<String> resolvePlaceNamesForPerson_Query(String personName) {
		return Collections.emptyList();
	}
	
	private List<GeoLocation> resolveGeoLocationForPlace_Query(String placeName) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		String dppediaLink = "http://dbpedia.org/data/" + placeName + ".rdf";
		Model model = ModelFactory.createDefaultModel().read(dppediaLink);
		if(model == null) {
			LOG.error("Could Not find dbpedia link @ " + dppediaLink);
			return geoLocations;
		}
		Property p = model.createProperty("http://www.georss.org/georss/point");
		ResIterator itr = model.listSubjectsWithProperty(p);
		while(itr.hasNext()) {
			RDFNode object = itr.nextResource().getProperty(p).getObject();
			String value = object.toString().trim();
			String[] split = value.split(" ");
			double latitude = Double.parseDouble(split[0]);
			double longitude = Double.parseDouble(split[1]);
			geoLocations.add(new GeoLocation(latitude, longitude));
		}
		return geoLocations;
	}
	
	public GeoLocation resolveGeoLocationsForPlaceByName(String placeName) {
		List<GeoLocation> list = resolveGeoLocationForPlace_Query(filterName(placeName));
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	private List<GeoLocation> resolveGeoLocationsForPlace(String placeName) {
		return resolveGeoLocationForPlace_Query(placeName);
	}
	
	private List<GeoLocation> resolveGeoLocationsForOrganization(String orgName) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		List<String> placeNames = resolvePlaceNamesForOrganization_Query(orgName);
		for(String placeName : placeNames) {
			geoLocations.addAll(resolveGeoLocationsForPlace(placeName));
		}
		return geoLocations;
	}
	
	private List<GeoLocation> resolveGeoLocationsForPerson(String personName) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		List<String> placeNames = resolvePlaceNamesForPerson_Query(personName);
		for(String placeName : placeNames) {
			geoLocations.addAll(resolveGeoLocationsForPlace(placeName));
		}
		return geoLocations;
	}
	
	private boolean isPlace(Concept concept) {
		return checkType(concept, ONTOLOGY_PLACE_TYPES);
	}
	
	private boolean isOrganization(Concept concept) {
		return checkType(concept, ONTOLOGY_ORG_TYPES);
	}
	
	private boolean isPerson(Concept concept) {
		return checkType(concept, ONTOLOGY_PERSON_TYPES);
	}
	
	private boolean checkType(Concept concept, String[] expectedTypes) {
		List<String> ontologyTypes = concept.getOntologyTypes();
		if(ontologyTypes == null) {
			LOG.error("Ontology types not resolved for concept");
			try {
				LOG.debug(JSONUtil.serialize(concept));
			} catch (PackPackException e) {
				LOG.error(e.getMessage(), e);
			}
			return false;
		}
		if(ontologyTypes.isEmpty()) {
			return false;
		}
		for(String expectedType : expectedTypes) {
			if(ontologyTypes.contains(expectedType))
				return true;
		}
		return false;
	}
	
	private String resolveName(Concept concept) {
		String dbpediaRef = concept.getDbpediaRef();
		if(dbpediaRef == null || dbpediaRef.trim().isEmpty()) {
			LOG.error("Dbpedia Link is not resolved for concept");
			try {
				LOG.debug(JSONUtil.serialize(concept));
			} catch (PackPackException e) {
				LOG.error(e.getMessage(), e);
			}
			return null;
		}
		return dbpediaRef.trim().substring(PREFIX.length()).replaceAll("\\/", "").replaceAll("\\s{1,}", "_");
	}
	
	private String filterName(String name) {
		if(name == null || name.trim().isEmpty()) {
			return null;
		}
		return name.trim().substring(PREFIX.length()).replaceAll("\\/", "").replaceAll("\\s{1,}", "_");
	}
	
	private List<GeoLocation> resolveGeoLocationTag(Concept concept) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		if(isPlace(concept)) {
			String placeName = resolveName(concept);
			if(placeName != null) {
				geoLocations.addAll(resolveGeoLocationsForPlace(placeName));
			}
		} else if(isOrganization(concept)) {
			String orgName = resolveName(concept);
			if(orgName != null) {
				geoLocations.addAll(resolveGeoLocationsForOrganization(orgName));
			}
		} else if(isPerson(concept)) {
			String personName = resolveName(concept);
			if(personName != null) {
				geoLocations.addAll(resolveGeoLocationsForPerson(personName));
			}
		}
		return geoLocations;
	}
	
	public List<GeoLocation> resolveGeoLocationTags(List<Concept> concepts) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		for(Concept concept : concepts) {
			geoLocations.addAll(resolveGeoLocationTag(concept));
		}
		return geoLocations;
	}
	
	public static void main(String[] args) {
		List<GeoLocation> geoPoints = new DbpediaGeoLocationReader().resolveGeoLocationsForPlace("New_Delhi");
		if(geoPoints == null) {
			System.out.println("COULD NOT FIND");
		} else {
			GeoLocation geoPoint = geoPoints.get(0);
			System.out.println("Latitude=" + geoPoint.getLatitude() + "  Longitude=" + geoPoint.getLongitude());
		}
	}
}
