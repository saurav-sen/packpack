package com.pack.pack.rest.api.security.oauth1;

import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.oauth1.signature.OAuth1SignatureFeature;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.oauth1.OAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1ServerProperties;
import org.glassfish.jersey.server.oauth1.internal.AccessTokenResource;
import org.glassfish.jersey.server.oauth1.internal.RequestTokenResource;

import com.pack.pack.oauth.OAuthConstants;
import com.pack.pack.rest.api.oauth.provider.jersey.OAuth10SecurityProvider;

/**
 * 
 * @author Saurav
 *
 */
public class OAuth10SupportFeature implements Feature {

	
	
	private final OAuth1Provider oAuth1Provider = new OAuth10SecurityProvider();
    private final String requestTokenUri = OAuthConstants.OAUTH_REQUEST_TOKEN_PATH;
    private final String accessTokenUri = OAuthConstants.OAUTH_ACCESS_TOKEN_PATH;

    public OAuth10SupportFeature() {
	}

    @Override
    public boolean configure(FeatureContext context) {
        if (oAuth1Provider != null) {
            context.register(oAuth1Provider);
        }

       // context.register(OAuth1ServerFilter.class);

        if (!context.getConfiguration().isRegistered(OAuth1SignatureFeature.class)) {
            context.register(OAuth1SignatureFeature.class);
        }

        final Map<String, Object> properties = context.getConfiguration().getProperties();
        final Boolean propertyResourceEnabled = OAuth1ServerProperties.getValue(properties,
                OAuth1ServerProperties.ENABLE_TOKEN_RESOURCES, null, Boolean.class);

        boolean registerResources = propertyResourceEnabled != null
                ? propertyResourceEnabled : requestTokenUri != null & accessTokenUri != null;

        if (registerResources) {
            String requestUri = OAuth1ServerProperties.getValue(properties, OAuth1ServerProperties.REQUEST_TOKEN_URI,
                    null, String.class);
            if (requestUri == null) {
                requestUri = requestTokenUri == null ? "requestToken" : requestTokenUri;
            }

            String accessUri = OAuth1ServerProperties.getValue(properties, OAuth1ServerProperties.ACCESS_TOKEN_URI,
                    null, String.class);
            if (accessUri == null) {
                accessUri = accessTokenUri == null ? "accessToken" : accessTokenUri;
            }

            final Resource requestResource = Resource.builder(RequestTokenResource.class).path(requestUri).build();
            final Resource accessResource = Resource.builder(AccessTokenResource.class).path(accessUri).build();

            context.register(new OAuthModelProcessor(requestResource, accessResource));
        }
        return true;
    }


    @Priority(100)
    private static class OAuthModelProcessor implements ModelProcessor {
        private final Resource[] resources;

        private OAuthModelProcessor(Resource... resources) {
            this.resources = resources;
        }

        @Override
        public ResourceModel processResourceModel(ResourceModel resourceModel, Configuration configuration) {
            final ResourceModel.Builder builder = new ResourceModel.Builder(resourceModel, false);
            for (Resource resource : resources) {
                builder.addResource(resource);
            }

            return builder.build();
        }

        @Override
        public ResourceModel processSubResource(ResourceModel subResourceModel, Configuration configuration) {
            return subResourceModel;
        }
    }
}