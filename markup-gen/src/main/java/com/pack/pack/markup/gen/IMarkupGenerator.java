package com.pack.pack.markup.gen;


/**
 * 
 * @author Saurav
 *
 */
public interface IMarkupGenerator {

	public <T> void generateAndUpload(String entityId)
			throws Exception;
}
