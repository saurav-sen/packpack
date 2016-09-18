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
public @interface CompressRead {

}
