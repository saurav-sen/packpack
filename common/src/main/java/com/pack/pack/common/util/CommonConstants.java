package com.pack.pack.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Saurav
 *
 */
public class CommonConstants {

	public static final String DEFAULT_TOPIC_ID = "default_topic";
	
	public static final String DEFAULT_EGIFT_TOPIC_ID = "default_egift_topic";
	
	public static final int STANDARD_PAGE_SIZE = 20;
	
	public static final String NULL_PAGE_LINK = "FIRST_PAGE";
	
	public static final String END_OF_PAGE = "END_OF_PAGE";
	
	public static final String HOME = "home";
	public static final String LIFESTYLE = "lifestyle";
    public static final String ART = "art";
    public static final String PHOTOGRAPHY = "photography";
    public static final String MUSIC = "music";
    public static final String EDUCATION = "education";
    public static final String FUN = "fun";
    public static final String SPIRITUAL = "spiritual";
    public static final String OTHERS = "others";
    
    public static final Map<String, String> secondaryVsPrimaryCategoryMap = new HashMap<String, String>();
    static {
    	secondaryVsPrimaryCategoryMap.put(HOME, HOME);
    	
    	secondaryVsPrimaryCategoryMap.put(ART, ART);
    	secondaryVsPrimaryCategoryMap.put(MUSIC, ART);
    	
    	secondaryVsPrimaryCategoryMap.put(PHOTOGRAPHY, PHOTOGRAPHY);
    	secondaryVsPrimaryCategoryMap.put(LIFESTYLE, PHOTOGRAPHY);
    	
    	secondaryVsPrimaryCategoryMap.put(EDUCATION, EDUCATION);
    	secondaryVsPrimaryCategoryMap.put(SPIRITUAL, EDUCATION);
    	secondaryVsPrimaryCategoryMap.put(FUN, EDUCATION);
    	
    	secondaryVsPrimaryCategoryMap.put(OTHERS, OTHERS);
    }
    
    public static final String resolvePrimaryCategory(String secondaryCategory) {
    	return secondaryVsPrimaryCategoryMap.get(secondaryCategory);
    }
}