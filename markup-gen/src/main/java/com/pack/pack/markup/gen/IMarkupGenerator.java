package com.pack.pack.markup.gen;

/**
 * 
 * @author Saurav
 *
 */
public interface IMarkupGenerator {

	public <T> void generate(T object, IMarkup markup) throws Exception;

	public <T> void generate(String entityId, IMarkup markup) throws Exception;
}
