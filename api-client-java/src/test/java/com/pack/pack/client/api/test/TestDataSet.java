package com.pack.pack.client.api.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.model.web.JTopic;

public class TestDataSet {
	
	//private static final String NAME = "Saurav Sen";
	private static final String LOCALITY = "Sanath Nagar";
	private static final String CITY = "Hyderabad";
	private static final String COUNTRY = "India";
	//private static final String PROFILE_PICTURE = "D:/Saurav/myphoto.jpg";
	
	//public static final String USERNAME = "sourabhnits@gmail.com";
	public static final String PASSWORD = "$$EN@0x5f3759df";//"P@$$w0rd123";
	
	private static String[] names = new String[] {"Lawrence Conniff  ", "Darnell Valois  ", "Jean Hersom  ", "Valentin Lawley  ", "Freeman Plantz  ", "Boris Goodin  ", "Kris Dipaolo  ", "Carlos Brandstetter  ", "Rich Layfield  ", "Peter Turberville  ", "Lowell Roberie  ", "Blaine Roda  ", "Herschel Parkerson  ", "Haywood Dziedzic  ", "Lewis Games  ", "Millard Clancy  ", "Jose Pillsbury  ", "Lee Beegle  ", "Jordan Quinby  ", "Coy Ahner  ", "Jules Donaghy  ", "Silas Knott  ", "Buster Brubaker  ", "Cary Cogdell  ", "Mickey Craghead  ", "Jeffery Gutirrez  ", "Carey Daughtridge  ", "Cesar Kolodziej  ", "Jerome Mendel  ", "Jason Mcginley  ", "Anderson Serrano  ", "Tyson Suther  ", "Shawn Gosney  ", "Granville Weide  ", "Antoine Sanfilippo  ", "Waldo Robinett  ", "Charley Corrao  ", "Clemente Millen  ", "Damien Council  ", "Lesley Abrego  ", "Ray Neault  ", "Vito Coty  ", "Brock Ranieri  ", "Desmond Folsom  ", "Claude Cothren  ", "Monroe Joly  ", "Mckinley Lisowski  ", "Sandy Gaal  ", "Jessie Landrith  ", "Bo Placencia  ", "Sharon Boozer  ", "Catalina Sama  ", "Vikki Poon  ", "Lue Sifuentes  ", "Enda Provencher  ", "Awilda Janowski  ", "Emogene Marasco  ", "Ida Olivieri  ", "Annmarie Britton  ", "Arline Valla  ", "Marylouise Monterroso  ", "Jaimee Belt  ", "Delphia Babineaux  ", "Madge Griffeth  ", "Irish Blinn  ", "Shae Lough  ", "Winnifred Robeson  ", "Karisa Considine  ", "Betsey Leblanc  ", "Terrilyn Beagle  ", "Valda Stroope  ", "Rosie Rexrode  ", "Lorie Meurer  ", "Tashina Vasques  ", "Sharice Hoffer  ", "Vanetta Hwang  ", "Ayanna Wei  ", "Saundra Mariano  ", "Candis Cap  ", "Macy Duplessis  ", "Andra Romines  ", "Mireille Mickel  ", "Shantay Neiss  ", "Arvilla Piccirillo  ", "Elenore Ehrenberg  ", "Danyell Rohman  ", "Chae Gibney  ", "Tiesha Mirabal  ", "Wynell Thakkar  ", "Velia Span  ", "Dorthey Kinley  ", "Ava Blewett  ", "Codi Christain  ", "Athena Levenson  ", "Roselle Millett  ", "Leonarda Kowalski  ", "Micha Rolen  ", "Hortensia Foti  ", "Tracee Smits  ", "Jeanie Coil"};
	
	private String[] topicCategories = new String[] {"phtotography", "art", "writter", "others"};
	private String[] topicWallpapers = new String[] {"D:/Saurav/packpack/api-client-java/src/test/resources/wallpapers/Freedom.jpg"};
	private String[] packImages = new String[] {"D:/Saurav/packpack/api-client-java/src/test/resources/images/123.JPG", 
			"D:/Saurav/packpack/api-client-java/src/test/resources/images/456.JPG", 
			"D:/Saurav/packpack/api-client-java/src/test/resources/images/678.jpg", 
			"D:/Saurav/packpack/api-client-java/src/test/resources/images/678_1.jpg"};
	
	private static final String ATTACHMENT_STORY = "<h4>An Unordered List:</h4><ul><li>Coffee</li><li>Tea</li><li>Milk</li></ul><br/>cx, nsj.";
	
	private static final String CATEGORY_PHOTOGRAPHY = CommonConstants.SOCIETY;//"photography";
	
	private Map<Integer, List<String>> attachmentIdMap = new HashMap<Integer, List<String>>();
	
	private Map<Integer, JTopic> topicsMap = new HashMap<Integer, JTopic>();
	
	private Map<Integer, String> userIdsMap = new HashMap<Integer, String>();
	
	private static final TestDataSet instance = new TestDataSet();
	
	private TestDataSet() {
		
	}
	
	public String getSpecialSignUpVerifierCode() {
		return "TEST_VERIFIER";
	}

	public String getUserFullName(int seqNo) {
		int index = Math.abs(new Random().nextInt() % names.length);
		return names[index];
	}
	
	public String getUserEmail(int seqNo) {
		/*String ID = userIdsMap.get(seqNo);
		if(ID == null) {
			ID = UUID.fromString("abcdefghijklmnopqrstuvwxyz").toString() + "@gmail.com";
			userIdsMap.put(seqNo, ID);
		}
		return ID;*/
		return "sourabhnits@gmail.com";
	}
	
	public String getUserLocality(int seqNo) {
		return LOCALITY;
	}
	
	public String getUserCity(int seqNo) {
		return CITY;
	}
	
	public String getUserCountry(int seqNo) {
		return COUNTRY;
	}
	
	public String getUserDOB(int seqNo) {
		int date = Math.abs(new Random().nextInt() % 32);
		int month = Math.abs(new Random().nextInt() % 13);
		int year = 1940 + (int)Math.round(Math.random() * (2003 - 1940));
		return date + "/" + month + "/" + year;
	}
	
	public String getUserPassword(int seqNo) {
		return PASSWORD;
	}
	
	public String getAttachmentStory(int seqNo) {
		return ATTACHMENT_STORY;
	}
	
	public void addAttachmentIdIntoMap(int seqNo, String id) {
		List<String> list = attachmentIdMap.get(seqNo);
		if(list == null) {
			list = new ArrayList<String>();
			attachmentIdMap.put(seqNo, list);
		}
		list.add(id);
	}
	
	public String getRandomAttachmentIdFromMap(int seqNo) {
		List<String> list = attachmentIdMap.get(seqNo);
		int index = Math.abs(new Random().nextInt() % list.size());
		return list.get(index);
	}
	
	public String getTopicCategory(int seqNo) {
		return CATEGORY_PHOTOGRAPHY;
	}
	
	public void addToTopicsMap(int seqNo, JTopic topic) {
		topicsMap.put(seqNo, topic);
	}
	
	public JTopic getTopicFromMap(int seqNo) {
		return topicsMap.get(seqNo);
	}
	
	public String randomNewTopicTitle() {
		return "Title " + randomNewTopicCategory() + " " + new Random().nextInt();
	}
	
	public String randomNewTopicCategory() {
		int index = Math.abs(new Random().nextInt() % topicCategories.length);
		return topicCategories[index];
	}
	
	public String randomNewTopicWallpaperFilePath() {
		int index = Math.abs(new Random().nextInt() % topicWallpapers.length);
		return topicWallpapers[index];
	}
	
	public String randomNewTopicDescription() {
		return "Description " + randomNewTopicCategory() + " " + new Random().nextInt();
	}
	
	public String randomPackImageFilePath() {
		int index = Math.abs(new Random().nextInt() % packImages.length);
		return packImages[index];
	}
	
	public String randomAttachmentTitle() {
		return "Sample Image Title " + UUID.randomUUID().toString().hashCode();
	}
	
	public String randomAttachmentDescription() {
		return "Sample Image Description " + UUID.randomUUID().toString().hashCode();
	}
	
	public static TestDataSet getInstance() {
		return instance;
	}
}
