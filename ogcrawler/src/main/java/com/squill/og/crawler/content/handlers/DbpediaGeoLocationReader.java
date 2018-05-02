package com.squill.og.crawler.content.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.squill.feed.web.model.JConcept;
import com.squill.og.crawler.hooks.GeoLocation;
import com.squill.og.crawler.internal.utils.JSONUtil;
import com.squill.services.exception.OgCrawlException;

public class DbpediaGeoLocationReader {
	
	private static final Logger LOG = LoggerFactory.getLogger(DbpediaGeoLocationReader.class);
	
	private static final String PREFIX = "dbpedia.org/resource/";
	
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
		 "http://dbpedia.org/ontology/Person",
		 "http://schema.org/Person" };
	
	private Map<String, Model> linkVsModelMap = new HashMap<String, Model>();
	
	private Model createModel(String dppediaLink) {
		Model model = linkVsModelMap.get(dppediaLink);
		if(model == null) {
			model = ModelFactory.createDefaultModel().read(dppediaLink);
			linkVsModelMap.put(dppediaLink, model);
		}
		return model;
	}
	
	public void dispose() {
		linkVsModelMap.clear();
	}
	
	private List<GeoLocation> resolveGeoLocationForOrganization_Query(String orgName) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		String dppediaLink = "http://dbpedia.org/data/" + orgName + ".rdf";
		Model model = createModel(dppediaLink);
		if(model == null) {
			LOG.error("Could Not find dbpedia link @ " + dppediaLink);
		}
		geoLocations.addAll(georss_Query(model));
		if(geoLocations.isEmpty()) {
			geoLocations.addAll(city_Query(model));
		}
		if(geoLocations.isEmpty()) {
			geoLocations.addAll(country_Query(model));
		}
		if(LOG.isDebugEnabled() && geoLocations.isEmpty()) {
			LOG.debug("COULD NOT FIND");
		}
		return geoLocations;
	}
	
	private List<GeoLocation> city_Query(Model model) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		Property p = model.createProperty("http://dbpedia.org/ontology/city");
		Set<RDFNode> set = model.listObjectsOfProperty(p).toSet();
		if(set != null && !set.isEmpty()) {
			for(RDFNode object : set) {
				if(!object.isResource())
					continue;
				String dbpediaRef = object.toString().trim();
				String[] values = resolveNames(dbpediaRef);
				if(values == null || values.length < 1)
					continue;
				String value = values[1];
				List<GeoLocation> geoPoints = resolveGeoLocationsForPlace(value);
				if(geoPoints != null) {
					geoLocations.addAll(geoPoints);
				}
			}
		}
		return geoLocations;
	}
	
	private List<GeoLocation> country_Query(Model model) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		Property p = model.createProperty("http://dbpedia.org/ontology/country");
		Set<RDFNode> set = model.listObjectsOfProperty(p).toSet();
		if(set != null && !set.isEmpty()) {
			for(RDFNode object : set) {
				if(!object.isResource())
					continue;
				String dbpediaRef = object.toString().trim();
				String[] values = resolveNames(dbpediaRef);
				if(values == null || values.length < 1)
					continue;
				String value = values[1];
				List<GeoLocation> geoPoints = resolveGeoLocationsForPlace(value);
				if(geoPoints != null) {
					geoLocations.addAll(geoPoints);
				}
			}
		}
		return geoLocations;
	}
	
	private List<GeoLocation> residence_Query(Model model) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		Property p = model.createProperty("http://dbpedia.org/ontology/residence");
		Set<RDFNode> set = model.listObjectsOfProperty(p).toSet();
		if(set != null && !set.isEmpty()) {
			for(RDFNode object : set) {
				if(!object.isResource())
					continue;
				String dbpediaRef = object.toString().trim();
				String[] values = resolveNames(dbpediaRef);
				if(values == null || values.length < 1)
					continue;
				String value = values[1];
				List<GeoLocation> geoPoints = resolveGeoLocationsForPlace(value);
				if(geoPoints != null) {
					geoLocations.addAll(geoPoints);
				}
			}
		}
		return geoLocations;
	}
	
	private List<GeoLocation> birthPlace_Query(Model model) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		Property p = model.createProperty("http://dbpedia.org/ontology/birthPlace");
		Set<RDFNode> set = model.listObjectsOfProperty(p).toSet();
		if(set != null && !set.isEmpty()) {
			for(RDFNode object : set) {
				if(!object.isResource())
					continue;
				String dbpediaRef = object.toString().trim();
				String[] values = resolveNames(dbpediaRef);
				if(values == null || values.length < 1)
					continue;
				String value = values[1];
				List<GeoLocation> geoPoints = resolveGeoLocationsForPlace(value);
				if(geoPoints != null) {
					geoLocations.addAll(geoPoints);
				}
			}
		}
		return geoLocations;
	}
	
	private List<GeoLocation> resolveGeoLocationForPerson_Query(String personName) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		String dppediaLink = "http://dbpedia.org/data/" + personName + ".rdf";
		Model model = createModel(dppediaLink);
		if(model == null) {
			LOG.error("Could Not find dbpedia link @ " + dppediaLink);
		}
		geoLocations.addAll(residence_Query(model));
		if(geoLocations.isEmpty()) {
			geoLocations.addAll(birthPlace_Query(model));
		}
		if(LOG.isDebugEnabled() && geoLocations.isEmpty()) {
			LOG.debug("COULD NOT FIND");
		}
		return geoLocations;
	}
	
	private List<GeoLocation> resolveGeoLocationForPlace_Query(String placeName) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		String dppediaLink = "http://dbpedia.org/data/" + placeName + ".rdf";
		Model model = createModel(dppediaLink);
		if(model == null) {
			LOG.error("Could Not find dbpedia link @ " + dppediaLink);
			return geoLocations;
		}
		geoLocations.addAll(georss_Query(model));
		if(LOG.isDebugEnabled() && geoLocations.isEmpty()) {
			LOG.debug("COULD NOT FIND");
		}
		return geoLocations;
	}
	
	private List<GeoLocation> georss_Query(Model model) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		Property p = model.createProperty("http://www.georss.org/georss/point");
		Set<RDFNode> set = model.listObjectsOfProperty(p).toSet();
		if(set != null && !set.isEmpty()) {
			for(RDFNode object : set) {
				String value = object.toString().trim();
				String[] split = value.split(" ");
				double latitude = Double.parseDouble(split[0]);
				double longitude = Double.parseDouble(split[1]);
				GeoLocation geoLocation = new GeoLocation(latitude, longitude);
				geoLocations.add(geoLocation);
			}
		}
		return geoLocations;
	}
	
	public List<GeoLocation> resolveGeoLocationsForPlaceByName(String originalPlaceName) {
		List<GeoLocation> list = GeoLocationDataHolder.INSTANCE.getGeoLocationByEntityName(originalPlaceName);
		if(list != null) {
			return list;
		}
		String placeName = filterName(originalPlaceName);
		list = GeoLocationDataHolder.INSTANCE.getGeoLocationByEntityName(placeName);
		if(list != null) {
			return list;
		}
		list = resolveGeoLocationForPlace_Query(filterName(placeName));
		if(list == null) {
			list = new LinkedList<GeoLocation>();
		}
		GeoLocationDataHolder.INSTANCE.addInfoOfGeoLocations(originalPlaceName, list);
		GeoLocationDataHolder.INSTANCE.addInfoOfGeoLocations(placeName, list);
		return list;
	}
	
	private List<GeoLocation> resolveGeoLocationsForPlace(String resolvedPlaceName) {
		return resolveGeoLocationForPlace_Query(resolvedPlaceName);
	}
	
	private List<GeoLocation> resolveGeoLocationsForOrganization(String resolvedOrgName) {
		return resolveGeoLocationForOrganization_Query(resolvedOrgName);
	}
	
	private List<GeoLocation> resolveGeoLocationsForPerson(String resolvedPersonName) {
		return resolveGeoLocationForPerson_Query(resolvedPersonName);
	}
	
	private boolean isPlace(JConcept concept) {
		return checkType(concept, ONTOLOGY_PLACE_TYPES);
	}
	
	private boolean isOrganization(JConcept concept) {
		return checkType(concept, ONTOLOGY_ORG_TYPES);
	}
	
	private boolean isPerson(JConcept concept) {
		return checkType(concept, ONTOLOGY_PERSON_TYPES);
	}
	
	private boolean checkType(JConcept concept, String[] expectedTypes) {
		List<String> ontologyTypes = concept.getOntologyTypes();
		if(ontologyTypes == null) {
			LOG.error("Ontology types not resolved for concept");
			try {
				LOG.debug(JSONUtil.serialize(concept));
			} catch (OgCrawlException e) {
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
	
	private String[] resolveNames(JConcept concept) {
		String dbpediaRef = concept.getDbpediaRef();
		if(dbpediaRef == null || dbpediaRef.trim().isEmpty()) {
			LOG.error("Dbpedia Link is not resolved for concept");
			try {
				LOG.debug(JSONUtil.serialize(concept));
			} catch (OgCrawlException e) {
				LOG.error(e.getMessage(), e);
			}
			return null;
		}
		String originalName = concept.getContent();
		int index = dbpediaRef.trim().lastIndexOf(PREFIX);
		if(index >= 0) {
			originalName = dbpediaRef.trim().substring(index + PREFIX.length());
		}
		String resolvedName = originalName.replaceAll("\\/", "").replaceAll("\\s{1,}", "_");
		return new String[] {originalName, resolvedName};
	}
	
	private String[] resolveNames(String dbpediaRef) {
		if(dbpediaRef == null || dbpediaRef.trim().isEmpty()) {
			LOG.error("Dbpedia Link is not resolved for concept");
			return null;
		}
		String originalName = null;
		int index = dbpediaRef.trim().lastIndexOf(PREFIX);
		if(index >= 0) {
			originalName = dbpediaRef.trim().substring(index + PREFIX.length());
		}
		String resolvedName = null;
		if(originalName != null) {
			resolvedName = originalName.replaceAll("\\/", "").replaceAll("\\s{1,}", "_");
		}
		return new String[] {originalName, resolvedName};
	}
	
	private String filterName(String name) {
		if(name == null || name.trim().isEmpty()) {
			return null;
		}
		int index = name.trim().lastIndexOf(PREFIX);
		if(index >= 0) {
			return name.trim().substring(index + PREFIX.length()).replaceAll("\\/", "").replaceAll("\\s{1,}", "_");
		}
		return name.trim().replaceAll("\\/", "").replaceAll("\\s{1,}", "_");
	}
	
	private List<GeoLocation> resolveGeoLocationTag(JConcept concept) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		if (isPlace(concept)) {
			String[] names = resolveNames(concept);
			if (names != null && names.length > 1) {
				String placeName = names[1];
				String originalPlaceName = names[0];
				if(originalPlaceName != null) {
					List<GeoLocation> list = GeoLocationDataHolder.INSTANCE.getGeoLocationByEntityName(originalPlaceName);
					if(list != null && !list.isEmpty()) {
						return list;
					}
				}
				if (placeName != null) {
					List<GeoLocation> list = GeoLocationDataHolder.INSTANCE.getGeoLocationByEntityName(placeName);
					if(list != null && !list.isEmpty()) {
						return list;
					}
					geoLocations.addAll(resolveGeoLocationsForPlace(placeName));
				}
				GeoLocationDataHolder.INSTANCE.addInfoOfGeoLocations(
						originalPlaceName, geoLocations);
				GeoLocationDataHolder.INSTANCE.addInfoOfGeoLocations(placeName,
						geoLocations);
			}
		} if (isOrganization(concept)) {
			String[] names = resolveNames(concept);
			if (names != null && names.length > 1) {
				String orgName = names[1];
				String originalOrgName = names[0];
				if(originalOrgName != null) {
					List<GeoLocation> list = GeoLocationDataHolder.INSTANCE.getGeoLocationByEntityName(originalOrgName);
					if(list != null && !list.isEmpty()) {
						return list;
					}
				}
				if (orgName != null) {
					List<GeoLocation> list = GeoLocationDataHolder.INSTANCE.getGeoLocationByEntityName(orgName);
					if(list != null && !list.isEmpty()) {
						return list;
					}
					geoLocations
							.addAll(resolveGeoLocationsForOrganization(orgName));
				}
				GeoLocationDataHolder.INSTANCE.addInfoOfGeoLocations(
						originalOrgName, geoLocations);
				GeoLocationDataHolder.INSTANCE.addInfoOfGeoLocations(orgName,
						geoLocations);
			}
		} if (isPerson(concept)) {
			String[] names = resolveNames(concept);
			if (names != null && names.length > 1) {
				String personName = names[1];
				String originalPersonName = names[0];
				if(originalPersonName != null) {
					List<GeoLocation> list = GeoLocationDataHolder.INSTANCE.getGeoLocationByEntityName(originalPersonName);
					if(list != null && !list.isEmpty()) {
						return list;
					}
				}
				if (personName != null) {
					List<GeoLocation> list = GeoLocationDataHolder.INSTANCE.getGeoLocationByEntityName(personName);
					if(list != null && !list.isEmpty()) {
						return list;
					}
					geoLocations
							.addAll(resolveGeoLocationsForPerson(personName));
				}
				GeoLocationDataHolder.INSTANCE.addInfoOfGeoLocations(
						originalPersonName, geoLocations);
				GeoLocationDataHolder.INSTANCE.addInfoOfGeoLocations(
						personName, geoLocations);
			}
		}
		return geoLocations;
	}
	
	public List<GeoLocation> resolveGeoLocationTags(List<JConcept> concepts) {
		List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
		for(JConcept concept : concepts) {
			geoLocations.addAll(resolveGeoLocationTag(concept));
		}
		return geoLocations;
	}
	
	private static void testForPlace() {
		String placeName = "Qatar";
		List<GeoLocation> geoPoints = new DbpediaGeoLocationReader().resolveGeoLocationsForPlace(placeName);
		System.out.println(placeName);
		if(geoPoints == null || geoPoints.isEmpty()) {
			System.out.println("COULD NOT FIND");
		} else {
			for(GeoLocation geoPoint : geoPoints) {
				System.out.println("Latitude=" + geoPoint.getLatitude() + "  Longitude=" + geoPoint.getLongitude());
			}
		}
	}
	
	private static void testForPerson() {
		String resolvedPersonName = "Narendra_Modi";
		List<GeoLocation> geoPoints = new DbpediaGeoLocationReader().resolveGeoLocationsForPerson(resolvedPersonName);
		System.out.println(resolvedPersonName);
		if(geoPoints == null || geoPoints.isEmpty()) {
			System.out.println("COULD NOT FIND");
		} else {
			for(GeoLocation geoPoint : geoPoints) {
				System.out.println("Latitude=" + geoPoint.getLatitude() + "  Longitude=" + geoPoint.getLongitude());
			}
		}
	}
	
	private static void testForOrganization() {
		String resolvedOrganizationName = "Jadavpur_University";
		List<GeoLocation> geoPoints = new DbpediaGeoLocationReader().resolveGeoLocationForOrganization_Query(resolvedOrganizationName);
		System.out.println(resolvedOrganizationName);
		if(geoPoints == null || geoPoints.isEmpty()) {
			System.out.println("COULD NOT FIND");
		} else {
			for(GeoLocation geoPoint : geoPoints) {
				System.out.println("Latitude=" + geoPoint.getLatitude() + "  Longitude=" + geoPoint.getLongitude());
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println("============================================================================");
		testForPlace();
		System.out.println("============================================================================");
		System.out.println();
		System.out.println("============================================================================");
		testForPerson();
		System.out.println("============================================================================");
		System.out.println();
		System.out.println("============================================================================");
		testForOrganization();
		System.out.println("============================================================================");
	}
}
