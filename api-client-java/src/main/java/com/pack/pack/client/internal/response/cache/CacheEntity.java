package com.pack.pack.client.internal.response.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;

/**
 * 
 * @author Saurav
 *
 */
class CacheEntity implements HttpEntity, Serializable {

    private static final long serialVersionUID = -3467082284120936233L;

    private final HttpCacheEntry cacheEntry;

    public CacheEntity(final HttpCacheEntry cacheEntry) {
        super();
        this.cacheEntry = cacheEntry;
    }

    public Header getContentType() {
        return this.cacheEntry.getFirstHeader(HTTP.CONTENT_TYPE);
    }

    public Header getContentEncoding() {
        return this.cacheEntry.getFirstHeader(HTTP.CONTENT_ENCODING);
    }

    public boolean isChunked() {
        return false;
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        Resource resource = this.cacheEntry.getResource();
		return (resource != null) ? resource.length() : 0L;
    }

    public InputStream getContent() throws IOException {
        return this.cacheEntry.getResource().getInputStream();
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = this.cacheEntry.getResource().getInputStream();
        try {
            copy(instream, outstream);
        } finally {
            instream.close();
        }
    }
    
    private void copy(final InputStream in, final OutputStream out) throws IOException {
        byte[] buf = new byte[2048];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    public boolean isStreaming() {
        return false;
    }

    public void consumeContent() throws IOException {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}