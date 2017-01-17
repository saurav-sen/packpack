package com.pack.pack.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO -- call an check this once we add ES infrastructure
//import com.pack.pack.services.es.Constants;

/**
 * 
 * @author Saurav
 *
 */
public final class SystemPropertyUtil {

	private static Properties properties;

	private static Logger logger = LoggerFactory
			.getLogger(SystemPropertyUtil.class);

	private static final String APP_HOME = "app.home";

	private static final String IMAGE_HOME = "image.home";
	private static final String VIDEO_HOME = "video.home";
	private static final String THUMBNAIL_HOME = "thumbnail.home";
	private static final String BASE_URL = "base.url";
	private static final String EGIFT_IMAGE_HOME = "egift.home";

	private static final String PROFILE_PICTURE_HOME = "profile.picture.home";

	private static final String TOPIC_WALLAPER_HOME = "topic.wallpaper.home";

	public static final String URL_SEPARATOR = "/";
	public static final String BROADCAST_API_PREFIX = "broadcast";
	public static final String ATTACHMENT = "attachment";
	public static final String IMAGE = "image";
	public static final String VIDEO = "video";
	public static final String PROFILE = "profile";
	public static final String TOPIC = "topic";
	private static final String ATTACHMENT_URL_SUFFIX = ATTACHMENT
			+ URL_SEPARATOR;
	public static final String IMAGE_ATTACHMENT_URL_SUFFIX = ATTACHMENT_URL_SUFFIX
			+ IMAGE + URL_SEPARATOR;
	public static final String VIDEO_ATTACHMENT_URL_SUFFIX = ATTACHMENT_URL_SUFFIX
			+ VIDEO + URL_SEPARATOR;
	public static final String PROFILE_IMAGE_URL_SUFFIX = ATTACHMENT_URL_SUFFIX
			+ PROFILE + URL_SEPARATOR + IMAGE + URL_SEPARATOR;

	public static final String TOPIC_WALLPAPER_URL_SUFFIX = ATTACHMENT_URL_SUFFIX
			+ TOPIC + URL_SEPARATOR + IMAGE + URL_SEPARATOR;

	private static final String CONFIG_FILE = "../conf/system_internal.properties";

	private static final String DEFAULT_TOPIC_ID_VALUE = "home.topic";

	public static final String HIGH_UNICODE_CHARACTER = "\ufff0";

	private static final String PRODUCTION_ENVIRONMENT = "prod.env";

	private static final String AWS_S3_ACCESS_KEY = "aws.s3.access.key";
	private static final String AWS_S3_ACCESS_SECRET = "aws.s3.access.secret";
	private static final String AWS_S3_ROOT_BUCKET = "aws.s3.root.bucket";
	private static final String AWS_S3_BASE_URL = "aws.s3.base.url";

	private static final String FFMPEG_COMMAND = "ffmpeg.cmd";

	private static final String GOOGLE_GEO_CODING_API_KEY = "google.geo.coding.api_key";

	private static final String WEB_PAGES_ROOT_PATH = "web.pages.root.path";

	private static final String ML_WORKING_DIR = "ml.work.dir";
	private static final String ML_SERVER_START_MODE = "ml.server.mode";

	public static final String ML_SERVER_TEST_MODE = "test";
	public static final String ML_SERVER_CLASSIFY_MODE = "classify";

	private static final String FEED_SELECTION_STRATEGY_KEY = "feed.selection.strategy.name";
	private static final String FEED_DEFAULT_SELECTION_STRATEGY_NAME = "default";
	private static final String FEED_SELECTION_STRATEGY_CONFIG = "feed.selection.config.file";
	private static final String FEED_SELECTION_STRATEGY_DEFAULT_CONFIG = "../conf/feed-selection-strategy.xml";
	
	private static final String REDIS_URI_KEY = "redis.uri";
	
	private static final String REDIS_URI_DEFAULT_VALUE = "redis://localhost";
	
	private SystemPropertyUtil() {
	}

	public static void init() {
		try {
			properties = new Properties();
			properties.load(new FileInputStream(new File(CONFIG_FILE)));
			// TODO -- call an check this once we add ES infrastructure
			// String esBaseUrl = properties.getProperty(Constants.ES_BASE_URL);
			// System.setProperty(Constants.ES_BASE_URL, esBaseUrl);
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private static String getPropertyValue(String key) {
		return properties.getProperty(key);
	}

	public static String getMlServerMode() {
		return getPropertyValue(ML_SERVER_START_MODE);
	}

	public static String getMlWorkingDirectory() {
		return getPropertyValue(ML_WORKING_DIR);
	}

	public static String getFFmpegCommand() {
		return getPropertyValue(FFMPEG_COMMAND);
	}

	public static String getGoogleGeoCodingApiKey() {
		return getPropertyValue(GOOGLE_GEO_CODING_API_KEY);
	}

	public static String getAppHome() {
		return getPropertyValue(APP_HOME);
	}

	public static String getEGiftImageHome() {
		return getPropertyValue(EGIFT_IMAGE_HOME);
	}

	public static String getImageHome() {
		return getPropertyValue(IMAGE_HOME);
	}

	public static String getVideoHome() {
		return getPropertyValue(VIDEO_HOME);
	}

	public static String getThumbnailHome() {
		return getPropertyValue(THUMBNAIL_HOME);
	}

	public static String getProfilePictureHome() {
		return getPropertyValue(PROFILE_PICTURE_HOME);
	}

	public static String getTopicWallpaperHome() {
		return getPropertyValue(TOPIC_WALLAPER_HOME);
	}

	private static String getAttachmentBaseURL() {
		if (isProductionEnvironment()) {
			return getPropertyValue(AWS_S3_BASE_URL);
		}
		return getBaseURL();
	}

	public static String getBaseURL() {
		return getPropertyValue(BASE_URL);
	}

	public static boolean isProductionEnvironment() {
		String propertyValue = getPropertyValue(PRODUCTION_ENVIRONMENT);
		if (propertyValue == null || propertyValue.trim().isEmpty())
			return false;
		try {
			return Boolean.parseBoolean(propertyValue.trim());
		} catch (Exception e) {
			return false;
		}
	}

	public static final String getAwsS3AccessKey() {
		return getPropertyValue(AWS_S3_ACCESS_KEY);
	}

	public static final String getAwsS3AccessSecret() {
		return getPropertyValue(AWS_S3_ACCESS_SECRET);
	}

	public static final String getAwsS3RootBucketName() {
		return getPropertyValue(AWS_S3_ROOT_BUCKET);
	}

	public static final String getWebPagesRootPath() {
		return getPropertyValue(WEB_PAGES_ROOT_PATH);
	}

	public static String getImageAttachmentBaseURL() {
		String baseURL = getAttachmentBaseURL();
		if (!baseURL.endsWith(URL_SEPARATOR)) {
			baseURL = baseURL + URL_SEPARATOR;
		}
		if (isProductionEnvironment()) {
			return baseURL;
		}
		return baseURL + IMAGE_ATTACHMENT_URL_SUFFIX;
	}

	public static String getVideoAttachmentBaseURL() {
		String baseURL = getAttachmentBaseURL();
		if (!baseURL.endsWith(URL_SEPARATOR)) {
			baseURL = baseURL + URL_SEPARATOR;
		}
		if (isProductionEnvironment()) {
			return baseURL;
		}
		return baseURL + VIDEO_ATTACHMENT_URL_SUFFIX;
	}

	public static String getProfilePictureBaseURL() {
		String baseURL = getAttachmentBaseURL();
		if (!baseURL.endsWith(URL_SEPARATOR)) {
			baseURL = baseURL + URL_SEPARATOR;
		}
		if (isProductionEnvironment()) {
			return baseURL;
		}
		return baseURL + PROFILE_IMAGE_URL_SUFFIX;
	}

	public static String getTopicWallpaperBaseURL() {
		String baseURL = getAttachmentBaseURL();
		if (!baseURL.endsWith(URL_SEPARATOR)) {
			baseURL = baseURL + URL_SEPARATOR;
		}
		if (isProductionEnvironment()) {
			return baseURL;
		}
		return baseURL + TOPIC_WALLPAPER_URL_SUFFIX;
	}

	public static String getDefaultSystemTopicId() {
		return DEFAULT_TOPIC_ID_VALUE;
	}

	public static boolean isCacheEnabled() {
		return true;
	}

	public static String getFeedSelectionStrategyName() {
		String value = getPropertyValue(FEED_SELECTION_STRATEGY_KEY);
		return value != null ? value.trim()
				: FEED_DEFAULT_SELECTION_STRATEGY_NAME;
	}

	public static String getFeedSelectionStrategyConfigFileLocation() {
		String value = getPropertyValue(FEED_SELECTION_STRATEGY_CONFIG);
		return value != null ? value : FEED_SELECTION_STRATEGY_DEFAULT_CONFIG;
	}
	
	public static String getRedisURI() {
		String uri = getPropertyValue(REDIS_URI_KEY);
		return uri != null ? uri : REDIS_URI_DEFAULT_VALUE;
	}
}