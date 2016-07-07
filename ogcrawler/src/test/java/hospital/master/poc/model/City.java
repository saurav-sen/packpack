package hospital.master.poc.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author Saurav
 *
 */
public class City {

	private String name;
	
	private String oldName;
	
	private String state;
	
	private String country;
	
	private List<Hospital> hospitals;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(required=false)
	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	@XmlAttribute(required=false)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@XmlAttribute
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@XmlElement
	public List<Hospital> getHospitals() {
		if(hospitals == null) {
			hospitals = new ArrayList<Hospital>();
		}
		return hospitals;
	}

	public void setHospitals(List<Hospital> hospitals) {
		this.hospitals = hospitals;
	}
}