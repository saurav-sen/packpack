package com.pack.pack.client.api;

/**
 * 
 * @author Saurav
 *
 */
public interface APIConstants {
	
	//public static final String BASE_URL = "http://54.169.84.61:8080/packpack/";//"http://192.168.35.12:8080/packpack/";
	public static final String DEFAULT_SERVICE_URL_PREFIX = "http://54.169.84.61:8080/packpack/";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String CONTENT_TYPE_HEADER = "Content-Type";
	public static final String APPLICATION_JSON = "application/json";
	
	public static final String CACHE_STORAGE = "CACHE_STORAGE";
	
	public static interface Login {
		public static final String CLIENT_KEY = "clientKey";
		public static final String CLIENT_SECRET = "clientSecret";
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static final String OLD_ACCESS_TOKEN = "old_accessToken";
		public static final String OLD_ACCESS_TOKEN_SECRET = "old_accessTokenSecret";
	}
	
	public static interface User {
		public static final String ID = "userId";
		public static final String USERNAME = "username";
		public static final String NAME_SEARCH_PATTERN = "namePattern";
		public static final String PROFILE_PICTURE = "profilePicture";
		
		public static interface Register {
			public static final String NAME = "name";
			public static final String EMAIL = "email";
			public static final String PASSWORD = "password";
			public static final String CITY = "city";
			public static final String COUNTRY = "country";
			public static final String STATE = "state";
			public static final String LOCALITY = "locality";
			public static final String DOB = "dob";			
		}
		
		public static interface Settings {
			public static final String KEY = "key";
			public static final String VALUE = "value";
		}
	}
	
	public static interface PageInfo {
		public static final String PAGE_LINK = "pageLink";
	}
	
	public static interface Topic {
		public static final String ID = "topicId";
		public static final String OWNER_ID = "ownerId";
		public static final String OWNER_NAME = "ownerName";
		public static final String NAME = "name";
		public static final String DESCRIPTION = "description";
		public static final String CATEGORY = "category";
		public static final String WALLPAPER = "wallpaper";
		public static final String LOCALITY_ADDRESS = "locality";
		public static final String CITY = "city";
		public static final String COUNTRY = "country";
	}
	
	public static interface TopicSettings {
		public static final String KEY = "key";
		public static final String VALUE = "value";
	}
	
	public static interface TopicCategories {
		public static final String FOLLOWED_CATEGORIES = "followedCategories";
		public static final String SEPARATOR = ":";
	}

	public static interface Pack {
		public static final String ID = "packId";
		public static final String TITLE = "title";
		public static final String DESCRIPTION = "description";
		public static final String STORY = "story";
	}
	
	public static interface PackAttachment {
		public static final String ID = "packAttachmentId";
	}
	
	public static interface ForwardPack {
		public static final String FROM_USER_ID = "fromUserId";
		public static final String TO_USER_ID = "toUserId";
		public static final String TO_USER_EMAIL = "toUserEmail";
	}
	
	public static interface Comment {
		public static final String FROM_USER_ID = "fromUserId";
		public static final String FROM_USER_NAME = "fromUserName";
		public static final String COMMENT = "comment";
	}
	
	public static interface EGift {
		public static final String ID = "eGiftId";
		public static final String CATEGORY = "category";
	}
	
	public static interface Brand {
		public static final String ID = "brandId";
		public static final String COMPANY_NAME = "compnayName";
	}
	
	public static interface ForwardEGift {
		public static final String FROM_USER_ID = "fromUserId";
		public static final String TO_USER_ID = "toUserId";
		public static final String TO_USER_EMAIL = "toUserEmail";
		
		public static final String TITLE = "title";
		public static final String MESSAGE = "message";
	}
	
	public static interface Attachment {
		public static final String FILE_NAME = "fileName";
		public static final String FILE_ATTACHMENT = "file";
		public static final String TITLE = "title";
		public static final String DESCRIPTION = "description";
		public static final String STORY = "story";
		public static final String IS_COMPRESSED = "isCompressed";
		public static final String ATTACHMENT_URL = "attachmentUrl";
		public static final String ATTACHMENT_THUMBNAIL_URL = "attachmentThumbnailUrl";
	}
	
	public static interface AttachmentStory {
		public static final String ID = "attachmentStoryId";
		public static final String STORY = "story";
	}
	
	public static interface Image {
		public static final String WIDTH = "w";
		public static final String HEIGHT = "h";
	}
	
	public static interface ProtectedResource {
		public static final String RESOURCE_URL = "url";
	}
	
	public static interface ExternalResource {
		public static final String RESOURCE_URL = "external_url";
		public static final String INCLUDE_OAUTH_TOKEN = "include_oauth_token";
	}
	
	public static interface Discussion {
		public static final String ID = "discussionId";
		public static final String TITLE = "title";
		public static final String CONTENT = "content";
		public static final String TYPE = "type";
	}
	
	public static interface GeoLocation {
		public static final String LONGITUDE = "longitude";
		public static final String LATITUDE = "latitude";
	}
}