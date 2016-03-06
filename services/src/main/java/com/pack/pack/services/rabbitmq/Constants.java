package com.pack.pack.services.rabbitmq;

/**
 * 
 * @author Saurav
 *
 */
public interface Constants {

	public static final String REPLY_TO_TOPIC_PREFIX = "topic:";
	public static final String REPLY_TO_USER_PREFIX = "user:";
	
	public static final int STANDARD_PAGE_SIZE = 20;
	
	public static final String NULL_PAGE_LINK = "FIRST_PAGE";
	public static final String END_OF_PAGE = "END_OF_PAGE";
}