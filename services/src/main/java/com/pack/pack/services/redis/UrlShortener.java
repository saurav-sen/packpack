package com.pack.pack.services.redis;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JSharedFeed;
import com.pack.pack.model.web.ShortenUrlInfo;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.ModelConverter;
import com.pack.pack.util.SystemPropertyUtil;
import com.squill.feed.web.model.JRssFeed;

/**
 * 
 * @author Saurav
 *
 */
public class UrlShortener {
	
	private static final String SHARED_LINK_KEY_PREFIX = "SH_";
	
	private UrlShortener() {
	}

	private static ShortenUrlInfo shortenFeedUrl(JRssFeed feed, boolean storeSharedFeed)
			throws PackPackException {
		ShortenUrlInfo info = new ShortenUrlInfo();
		int hash = feed.getOgUrl().hashCode();
		info.setHashcode(String.valueOf(hash));
		//String hash = String.valueOf(feed.getOgUrl().hashCode());
		//info.setHashcode(hash);
		String shortenUrl = Base62.getEncoder().encode(hash);
		//String shortenUrl = Base64.getUrlEncoder().encodeToString(hash.getBytes());
		//shortenUrl = URLEncoder.encode(shortenUrl);
		if(storeSharedFeed) {
			JSharedFeed shareableFeed = ModelConverter.convertToShareableFeed(feed);
			String json = JSONUtil.serialize(shareableFeed, false);
			long ttl = 7 * 24 * 60 * 60;
			RedisCacheService cacheService = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			String redisKey = SHARED_LINK_KEY_PREFIX + hash;
			if (cacheService.isKeyExists(redisKey)) {
				cacheService.setTTL(redisKey, ttl);
			} else {
				cacheService.addToCache(redisKey, json, ttl);
			}
		}
		info.setSuffix(shortenUrl);
		return info;
	}
	
	/*public static void main(String[] args) {
		String str = "https://blog.heitorsilva.com/en/nginx/diferenca-entre-root-e-alias-do-nginx/";
		str = URLEncoder.encode(Base64.getEncoder().encodeToString(String.valueOf(str.hashCode()).getBytes()));
		System.out.println(str);
		str = new String(Base64.getDecoder().decode(URLDecoder.decode(str).getBytes()));
		System.out.println(str);
	}*/
	
	public static ShortenUrlInfo calculateShortenShareableUrl(JRssFeed feed, String baseUrl, boolean storeSharedFeed)
			throws PackPackException {
		ShortenUrlInfo info = shortenFeedUrl(feed, storeSharedFeed);	
		//String url = SystemPropertyUtil.getExternalSharedLinkBaseUrl();
		String url = baseUrl;
		if(!url.endsWith("/")) {
			url = url + "/";
		}
		url = url + info.getSuffix();
		info.setUrl(url);
		return info;
	}
	
	public static JSharedFeed readShortUrlInfo(String shortenUrl)
			throws PackPackException {
		int hash = Base62.getDecoder().decode(shortenUrl);
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		String redisKey = SHARED_LINK_KEY_PREFIX + hash;
		String json = cacheService.getFromCache(redisKey, String.class);
		if (json == null)
			return null;
		return JSONUtil.deserialize(json, JSharedFeed.class);
	}

	/*public static JSharedFeed readShortUrlInfo(String shortenUrl)
			throws PackPackException {
		//String hash = URLDecoder.decode(shortenUrl);
		String hash = shortenUrl;
		hash = new String(Base64.getUrlDecoder().decode(hash));
		RedisCacheService cacheService = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		String redisKey = SHARED_LINK_KEY_PREFIX + hash;
		String json = cacheService.getFromCache(redisKey, String.class);
		if (json == null)
			return null;
		return JSONUtil.deserialize(json, JSharedFeed.class);
	}*/
	
	/*public static void main(String[] args) {
		String hash = URLDecoder.decode("OTc4NjkzMTE5");
		hash = new String(Base64.getDecoder().decode(hash));
		String redisKey = SHARED_LINK_KEY_PREFIX + hash;
		System.out.println(redisKey);
	}*/

//	public static void main(String[] args) {
//		Map<String, String> map = new HashMap<String, String>();
//		String urlPrefix = "https://www.squill.co.in/RSS?next=";
//		String url = "https://qacand--tomcat-lab--rot-ondemand-com.pilot.ciphercloud.org/login?company=ECSuite3&isSessionTimeout=true&_s.crb=%2fB3jwIWoNQj%2bS6FeWpfh%2b%2f%2bPiKk%3d#/login";
//		System.out.println(url);
//		String hash = String.valueOf(url.hashCode());
//		map.put(hash, url);
//		String shortenUrl = Base64.getEncoder().encodeToString(hash.getBytes());
//		shortenUrl = URLEncoder.encode(shortenUrl);
//		String sharedUrl = urlPrefix + shortenUrl;
//		System.out.println(sharedUrl);
//		String hash1 = URLDecoder.decode(shortenUrl);
//		hash1 = new String(Base64.getDecoder().decode(hash1));
//		String url1 = map.get(hash1);
//		System.out.println(url1);
//		System.out.println(url.equals(url1));
//	}
}
