package com.pack.pack.rest.api.security.interceptors;

/**
 * 
 * @author Saurav
 *
 */
public @interface CacheControl {

	String type() default "private";
	
	boolean mustRevalidate() default false;
	
	long maxAge() default 600;
}
