package com.pack.pack.rest.api.security;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.oauth1.signature.OAuth1Parameters;
import org.glassfish.jersey.oauth1.signature.OAuth1Secrets;
import org.glassfish.jersey.oauth1.signature.OAuth1Signature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureException;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.oauth1.OAuth1Exception;
import org.glassfish.jersey.server.oauth1.internal.OAuthServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pack.pack.rest.api.oauth.token.RequestToken;
import com.pack.pack.rest.api.oauth.token.TokenGenerator;
import com.pack.pack.rest.api.oauth.token.TokenRegistry;


@Component
@Path("/oauth")
public class OAuthProvider {
	
	@Context
	private ContainerRequest request;
	
	@Inject
	private OAuth1Signature oAuth1Signature;
	
	private static Logger logger = LoggerFactory.getLogger(OAuthProvider.class);
	
	@POST
	@Path("request_token")
	@Produces(value=MediaType.APPLICATION_JSON)
	public RequestToken postAuthenticateClient() throws Exception {
		 return doAuthenticateClient(request);
	}
	
	@GET
	@Path("request_token")
	@Produces(value=MediaType.APPLICATION_JSON)
	public RequestToken getAuthenticateClient() throws Exception {
		 return doAuthenticateClient(request);
	}
	
	private RequestToken doAuthenticateClient(ContainerRequestContext context) throws Exception {
		logger.info("Authorization: " + context.getHeaderString(OAuthConstants.AUTHORIZATION_HEADER));
        try {
            OAuthServerRequest oauthReq = new OAuthServerRequest(context);
            OAuth1Parameters params = new OAuth1Parameters();
            params.readRequest(oauthReq);
            String consumerKey = params.getConsumerKey();
            if(consumerKey == null || consumerKey.trim().isEmpty()) {
            	consumerKey = OAuthConstants.DEFAULT_CLIENT_KEY;
            	//throw new WebApplicationException(401);
            }
            String consumerSecret = OAuthConsumerKeyMap.INSTANCE.getConsumerSecret(consumerKey.trim());
            if(consumerSecret == null || consumerSecret.trim().isEmpty()) {
            	consumerSecret = OAuthConstants.DEFAULT_CLIENT_SECRET;
            	//throw new WebApplicationException(401);
            }
            OAuth1Secrets secrets = new OAuth1Secrets().consumerSecret(consumerSecret);
            boolean isValid = oAuth1Signature.verify(oauthReq, params, secrets);
            if (!isValid) {
                throw new WebApplicationException(401);
            }
            RequestToken token = new TokenGenerator().generateNewRequestToken();
            TokenRegistry.INSTANCE.addRequestToken(token);
            return token;
        } catch (OAuth1SignatureException e) {
            logger.info("OAuthSignatureException: Verification failed", e);
            throw new OAuth1Exception(Status.UNAUTHORIZED, "OAuthSignatureException: Verification failed");
        }
	}
}