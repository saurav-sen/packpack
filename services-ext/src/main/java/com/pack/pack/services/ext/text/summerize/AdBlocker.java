package com.pack.pack.services.ext.text.summerize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.util.TextUtils;

public class AdBlocker {

    private static final String AD_HOSTS_FILE = "pgl.yoyo.org.txt";
    private static final Set<String> AD_HOSTS = new HashSet<>();
    
    public static final AdBlocker INSTANCE = new AdBlocker();
    
    private AdBlocker() {
    	loadHostEntries();
    }
    
    private void loadHostEntries() {
    	try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/" + AD_HOSTS_FILE);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while((line = reader.readLine()) != null) {
				AD_HOSTS.add(line.trim());
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
    }

    public boolean isAdvertisement(String url) {
        try {
            URL httpUrl = new URL(url);
            return isAdHost(httpUrl != null ? httpUrl.getHost() : "");
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Recursively walking up sub domain chain until we exhaust or find a match,
     * effectively doing a longest substring matching here
     */
    private boolean isAdHost(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        host = host.toLowerCase();
        if(host.contains("adservice.google")) {
            return true;
        }
        int index = host.indexOf(".");
        return index >= 0 && (AD_HOSTS.contains(host) ||
                index + 1 < host.length() && isAdHost(host.substring(index + 1)));
    }
}