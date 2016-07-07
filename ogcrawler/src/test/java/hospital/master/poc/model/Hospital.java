package hospital.master.poc.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * 
 * @author Saurav
 *
 */
public class Hospital {

	private String name;
	
	private String address;
	
	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(required=false)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}