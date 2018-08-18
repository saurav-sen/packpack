package com.pack.pack.dbpedia.concept;

import com.pack.pack.model.web.JSemanticElement;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public interface ISemanticElementService {

	public JSemanticElement findSemanticElementByAnnotationId(
			String annotationId) throws PackPackException;

	public JSemanticElement findSemanticElementByDbpediaRefLinkId(
			String dbpediaRefLinkId) throws PackPackException;

	public JSemanticElement store(JSemanticElement semanticElement)
			throws PackPackException;
}