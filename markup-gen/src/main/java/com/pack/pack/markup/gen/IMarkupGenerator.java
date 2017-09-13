package com.pack.pack.markup.gen;


/**
 * 
 * @author Saurav
 *
 */
public interface IMarkupGenerator {

	public <T> void generate(String entityId, IMarkup markup)
			throws Exception;
}
