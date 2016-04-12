package com.pack.pack.oauth1.client;

import java.net.MalformedURLException;

public interface OAuth1Builder {

	public OAuth1RequestFlow build() throws MalformedURLException;
}
