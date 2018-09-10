package com.pack.pack.dbpedia.concept.services;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pack.pack.dbpedia.concept.ISemanticElementService;
import com.pack.pack.model.SemanticElement;
import com.pack.pack.model.web.JSemanticElement;
import com.pack.pack.services.couchdb.dbpedia.SemanticElementRepositoryService;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;

/**
 * 
 * @author Saurav
 *
 */
@Component
@Scope("singleton")
public class SemanticElementServiceImpl implements ISemanticElementService {

	@Override
	public JSemanticElement findSemanticElementByDbpediaRefLinkId(
			String dbpediaRefLinkId) throws PackPackException {
		if(dbpediaRefLinkId == null || dbpediaRefLinkId.trim().isEmpty())
			return null;
		SemanticElementRepositoryService service = ServiceRegistry.INSTANCE
				.findService(SemanticElementRepositoryService.class);
		SemanticElement semanticElement = service
				.findDbpediaRefLink(dbpediaRefLinkId);
		return ModelConverter.convert(semanticElement);
	}

	@Override
	public JSemanticElement store(JSemanticElement semanticElement)
			throws PackPackException {
		SemanticElementRepositoryService service = ServiceRegistry.INSTANCE
				.findService(SemanticElementRepositoryService.class);
		service.add(ModelConverter.convert(semanticElement));
		return findSemanticElementByDbpediaRefLinkId(semanticElement.getDbpediaRef());
	}

}
