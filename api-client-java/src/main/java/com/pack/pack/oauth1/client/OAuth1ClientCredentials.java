package com.pack.pack.oauth1.client;

public final class OAuth1ClientCredentials {

    private final String consumerKey;
    private final byte[] consumerSecret;


    public OAuth1ClientCredentials(final String consumerKey, final String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret.getBytes();
    }

    public OAuth1ClientCredentials(final String consumerKey, final byte[] consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return new String(consumerSecret);
    }

    public byte[] getConsumerSecretAsByteArray() {
        return consumerSecret;
    }
}