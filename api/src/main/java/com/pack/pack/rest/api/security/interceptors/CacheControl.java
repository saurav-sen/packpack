package com.pack.pack.rest.api.security.interceptors;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

/**
 * 
 * @author Saurav
 *
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheControl {

	String type() default "private";
	
	boolean mustRevalidate() default false;
	
	long maxAge() default 600;
}
