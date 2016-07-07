package hospital.master.poc;

import hospital.master.poc.model.City;
import hospital.master.poc.model.Country;
import hospital.master.poc.model.Hospital;

import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author Saurav
 *
 */
public class IndianHospitalMasterData {
	
	private JAXBContext ctx;
	
	private  static Map<String, String> stateVsCapital = new HashMap<String, String>();
	
	static {
		stateVsCapital.put("Andaman and Nicobar Islands", "Port Blair"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Andhra Pradesh", "Hyderabad"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Arunachal Pradesh", "Itanagar"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Assam", "Guwahati"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Bihar", "Patna"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Chandigarh", "Chandigarh"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Chhattisgarh", "Raipur"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Dadra and Nagar Haveli", "Silvassa"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Daman and Diu", "Daman"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Delhi", "New Delhi"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Goa", "Porvorim"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Gujarat", "Gandhinagar"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Haryana", "Chandigarh"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Himachal Pradesh", "Shimla"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Jammu and Kashmir", "Srinagar"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Jharkhand", "Ranchi"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Karnataka", "Bengaluru"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Kerala", "Thiruvananthapuram"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Lakshadweep", "Kavaratti"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Madhya Pradesh", "Bhopal"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Maharashtra", "Mumbai"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Manipur", "Imphal"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Meghalaya", "Shillong"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Mizoram", "Aizawl"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Nagaland", "Kohima"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Odisha", "Bhubaneswar"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Puducherry", "Pondicherry"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Punjab", "Chandigarh"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Rajasthan", "Jaipur"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Sikkim", "Gangtok"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Tamil Nadu", "Chennai"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Telangana", "Hyderabad"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Tripura", "Agartala"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Uttar Pradesh", "Lucknow"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("Uttarakhand", "Dehradun"); //$NON-NLS-1$ //$NON-NLS-2$
		stateVsCapital.put("West Bengal", "Kolkata"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private Map<String, String> newVsOldCityNameMap = new HashMap<String, String>();
	
	public IndianHospitalMasterData() {
	}
	
	public void init() throws Exception {
		ctx = JAXBContext.newInstance(Country.class, City.class, Hospital.class);
		initCityNameAliases();		
	}
	
	private void initCityNameAliases() throws Exception {
		HttpGet get = new HttpGet("http://en.wikipedia.org/wiki/List_of_renamed_Indian_cities_and_states"); //$NON-NLS-1$
		HttpResponse response = new DefaultHttpClient().execute(get);
		if(response.getStatusLine().getStatusCode() == 200) {
			String html = EntityUtils.toString(response.getEntity());
			Document document = Jsoup.parse(html);
			Elements elements = document.getElementsByClass("mw-content-ltr"); //$NON-NLS-1$
			Elements elements2 = elements.select("ul"); //$NON-NLS-1$
			Iterator<Element> itr = elements2.iterator();
			int count = 0;
			while(itr.hasNext()) {
				count++;
				if(count < 5) {
					itr.next();
					continue;
				}
				Element element = itr.next();
				Elements children = element.children();
				Iterator<Element> itr1 = children.iterator();
				while(itr1.hasNext()) {
					Element element2 = itr1.next();
					String oldName = element2.ownText();
					String[] arr = oldName.split("to"); //$NON-NLS-1$
					if(arr.length == 0)
						continue;
					oldName = arr[0].trim();
					Elements elements3 = element2.children();
					if(elements3 != null && !elements3.isEmpty()) {
						Element element3 = elements3.get(0);
						String newName = element3.attr("title").trim(); //$NON-NLS-1$
						newVsOldCityNameMap.put(newName, oldName);
					}
				}
			}
		}
		/*Iterator<String> itr = newVsOldCityNameMap.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			System.out.println(key + " --> " + newVsOldCityNameMap.get(key));
		}*/
	}
	
	private boolean isStateName(String cityName) {
		return stateVsCapital.containsKey(cityName);
	}
	
	private String resolveCapitalCityName(String stateName) {
		return stateVsCapital.get(stateName);
	}
	
	private String resolveOldCityName(String newCityName) {
		return newVsOldCityNameMap.get(newCityName);
	}
	
	public void generateMasterData(OutputStream outputStream) throws Exception {
		HttpGet get = new HttpGet("http://en.wikipedia.org/wiki/List_of_hospitals_in_India"); //$NON-NLS-1$
		HttpResponse response = new DefaultHttpClient().execute(get);
		if(response.getStatusLine().getStatusCode() == 200) {
			String html = EntityUtils.toString(response.getEntity());
			Document document = Jsoup.parse(html);
			Elements elements = document.body().getElementsByClass("mw-headline"); //$NON-NLS-1$
			Set<Element> set = new TreeSet<Element>(new Comparator<Element>() {
				@Override
				public int compare(Element o1, Element o2) {
					String s1 = o1.text();
					String s2 = o2.text();
					return s1.compareTo(s2);
				}
			});
			Iterator<Element> itr = elements.iterator();
			int count = 0;
			while(itr.hasNext()) {
				Element element = itr.next();
				String state = element.text();
				if(stateVsCapital.containsKey(state)) {
					String capitalCity = stateVsCapital.get(state);
					element.text(capitalCity);
				}
				set.add(element);
				count++;
			}
			System.out.println(count);
			itr = set.iterator();
			Country country = new Country();
			country.setName("INDIA"); //$NON-NLS-1$
			while(itr.hasNext()) {
				Element el = itr.next();
				String name = el.text();
				if("References".equalsIgnoreCase(name)) //$NON-NLS-1$
					continue;
				City city = new City();
				String newCityName = name;
				if(newCityName != null && isStateName(newCityName)) {
					city.setState(name);
					newCityName = resolveCapitalCityName(newCityName);
				}
				if(newCityName != null) {
					city.setName(newCityName);
					String cityAlias = resolveOldCityName(newCityName);
					if(cityAlias == null) {
						cityAlias = newCityName;
					}
					city.setOldName(cityAlias);
				}
				else {
					city.setName(name);
				}
				city.setCountry("INDIA"); //$NON-NLS-1$
				el = el.parent().nextElementSibling();
				Document doc = Jsoup.parse(el.html());
				Elements elementsByTag = doc.getElementsByTag("li"); //$NON-NLS-1$
				Iterator<Element> itr1 = elementsByTag.iterator();
				while(itr1.hasNext()) {
					Element element = itr1.next();
					if(!"".equals(element.text())) { //$NON-NLS-1$
						String hospitalName = ""; //$NON-NLS-1$
						String address = ""; //$NON-NLS-1$
						if(!element.children().isEmpty()) {
							int size = element.children().size();
							for(int i=0; i<size; i++) {
								Element child = element.child(i);
							    hospitalName = hospitalName + child.text();
							    hospitalName = hospitalName.split("&")[0]; //$NON-NLS-1$
							    hospitalName = hospitalName.split("\\[")[0]; //$NON-NLS-1$
							    if(size > 1 && i < size-1) {
							    	if(i > 0) {
							    		address = address + ", "; //$NON-NLS-1$
							    	}
							    }
							}
						}
						else {
							hospitalName = element.text();
						}
						Hospital hospital = new Hospital();
						hospital.setName(hospitalName);
						hospital.setAddress(address);
						city.getHospitals().add(hospital);
					}
				}
				country.getCities().add(city);
			}
			
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(country, outputStream);
		}
	}
	
	public static void main(String[] args) throws Exception {
		IndianHospitalMasterData masterDataGenerator = new IndianHospitalMasterData();
		masterDataGenerator.init();
		masterDataGenerator.generateMasterData(System.out);
	}
}