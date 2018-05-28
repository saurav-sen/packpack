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
	
	public static final int STANDARD_NEWS_PAGE_SIZE = 50;
	
	public static final String NULL_PAGE_LINK = "FIRSTPAGE";
	
	public static final String END_OF_PAGE = "ENDOFPAGE";
	
	public static final String PAGELINK_DIRECTION_SEPERATOR = "_";
	public static final String PAGELINK_DIRECTION_POSITIVE = PAGELINK_DIRECTION_SEPERATOR + "1";
	public static final String PAGELINK_DIRECTION_NEGATIVE = PAGELINK_DIRECTION_SEPERATOR + "-1";
	
	public static final String NEXT_PAGE_LINK_PREFIX = "NEXT";
	public static final String PREV_PAGE_LINK_PREFIX = "PREV";
	
	public static final String HOME = "home";
	public static final String LIFESTYLE = "lifestyle";
    public static final String ART = "art";
    public static final String PHOTOGRAPHY = "photography";
    public static final String MUSIC = "music";
    public static final String EDUCATION = "education";
    public static final String FUN = "fun";
    public static final String SPIRITUAL = "spiritual";
    public static final String OTHERS = "others";
    public static final String MISCELLANEOUS = "miscellanecous";
    public static final String PAINTING = "painting";
    public static final String COOKING = "Cooking";
    
    public static final String FAMILY = "family";
    public static final String SOCIETY = "society";
    
    public static final Map<String, String> secondaryVsPrimaryCategoryMap = new HashMap<String, String>();
    static {
    	secondaryVsPrimaryCategoryMap.put(HOME, HOME);
    	
    	/*secondaryVsPrimaryCategoryMap.put(ART, ART);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.ART.name(), ART);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.ART.getDisplay(), ART);
    	secondaryVsPrimaryCategoryMap.put(EDUCATION, EDUCATION);    
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.EDUCATION.name(), EDUCATION);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.EDUCATION.getDisplay(), EDUCATION);
    	secondaryVsPrimaryCategoryMap.put(PHOTOGRAPHY, PHOTOGRAPHY); 
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PHOTOGRAPHY.getDisplay(), PHOTOGRAPHY);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PHOTOGRAPHY.name(), PHOTOGRAPHY);
    	secondaryVsPrimaryCategoryMap.put(OTHERS, OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.OTHERS.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.OTHERS.getDisplay(), OTHERS);
    	
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PHOTOGRAPHY_WILD_LIFE.getDisplay(), PHOTOGRAPHY);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PHOTOGRAPHY_WILD_LIFE.name(), PHOTOGRAPHY);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.TRAVEL.getDisplay(), PHOTOGRAPHY);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.TRAVEL.name(), PHOTOGRAPHY);*/
    	
    	secondaryVsPrimaryCategoryMap.put(ART, OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.ART.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.ART.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(EDUCATION, OTHERS);    
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.EDUCATION.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.EDUCATION.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(PHOTOGRAPHY, OTHERS); 
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PHOTOGRAPHY.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PHOTOGRAPHY.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(OTHERS, OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.OTHERS.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.OTHERS.getDisplay(), OTHERS);
    	
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PHOTOGRAPHY_WILD_LIFE.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PHOTOGRAPHY_WILD_LIFE.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.TRAVEL.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.TRAVEL.name(), OTHERS);
    	
    	secondaryVsPrimaryCategoryMap.put(MUSIC, OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.MUSIC.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.MUSIC.name(), OTHERS);
    	/*secondaryVsPrimaryCategoryMap.put(PAINTING, OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PAINTING.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.PAINTING.getDisplay(), OTHERS);*/
    	secondaryVsPrimaryCategoryMap.put(LIFESTYLE, OTHERS);    	
    	secondaryVsPrimaryCategoryMap.put(SPIRITUAL, OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.BOOKS.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.BOOKS.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.SPIRITUAL.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.SPIRITUAL.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(FUN, OTHERS);
    	secondaryVsPrimaryCategoryMap.put(MISCELLANEOUS, OTHERS);
    	secondaryVsPrimaryCategoryMap.put(COOKING, OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.COOKING.name(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.COOKING.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.FASHION.getDisplay(), OTHERS);
    	secondaryVsPrimaryCategoryMap.put(JCategoryName.FASHION.name(), OTHERS);
    	
    	secondaryVsPrimaryCategoryMap.put(FAMILY, FAMILY);
    	secondaryVsPrimaryCategoryMap.put(SOCIETY, SOCIETY);
    }
    
    public static final String resolvePrimaryCategory(String secondaryCategory) {
    	String primaryCategory = secondaryVsPrimaryCategoryMap.get(secondaryCategory);
    	if(primaryCategory == null) {
    		primaryCategory = OTHERS;
    	}
    	return primaryCategory;
    }
}