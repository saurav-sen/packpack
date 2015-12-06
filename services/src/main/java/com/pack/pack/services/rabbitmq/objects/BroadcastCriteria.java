package com.pack.pack.services.rabbitmq.objects;

/**
 * 
 * @author Saurav
 *
 */
public class BroadcastCriteria {

	private String city;

	private String state;

	private String country;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(city + ":").append(state + ":")
				.append(country + ":").append("@")
				.append(this.getClass().getName()).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && (obj instanceof BroadcastCriteria)) {
			return ((BroadcastCriteria) obj).toString().equals(toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}