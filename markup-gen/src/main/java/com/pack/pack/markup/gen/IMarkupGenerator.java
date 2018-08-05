package com.pack.pack.markup.gen;

/**
 * 
 * @author Saurav
 *
 */
public interface IMarkupGenerator {

	public <T> void generate(T object, IMarkup markup) throws Exception;
}
