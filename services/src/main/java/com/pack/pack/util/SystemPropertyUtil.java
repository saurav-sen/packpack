package com.pack.pack.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Saurav
 *
 */
public class SystemPropertyUtil {
	
	private static Properties properties;
	
	private static Logger logger = LoggerFactory.getLogger(SystemPropertyUtil.class);
	
	private static final String IMAGE_HOME = "image.home";
	private static final String VIDEO_HOME = "video.home";
	private static final String THUMBNAIL_HOME = "thumbnail.home";

	public static void init() {
		try {
			properties = new Properties();
			properties.load(new FileInputStream(new File("../conf/system_internal.properties")));
		} catch (FileNotFoundException e) {
			logger.info(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public static String getImageHome() {
		return properties.getProperty(IMAGE_HOME);
	}
	
	public static String getVideoHome() {
		return properties.getProperty(VIDEO_HOME);
	}
	
	public static String getThumbnailHome() {
		return properties.getProperty(THUMBNAIL_HOME);
	}
}