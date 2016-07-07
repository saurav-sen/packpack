package hospital.master.poc.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Saurav
 *
 */
@XmlRootElement
public class Country {

	private String name;
	
	private List<City> cities;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public List<City> getCities() {
		if(cities == null) {
			cities = new ArrayList<City>(200);
		}
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}
}