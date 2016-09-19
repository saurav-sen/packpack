package com.pack.pack.client.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

/**
 * 
 * @author Saurav
 *
 */
public class GzipCompressingEntity extends HttpEntityWrapper {

	private static final String GZIP_CODEC = "gzip";

    public GzipCompressingEntity(final HttpEntity entity) {
        super(entity);
    }

    @Override
    public Header getContentEncoding() {
        return new BasicHeader(HTTP.CONTENT_ENCODING, GZIP_CODEC);
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public boolean isChunked() {
        // force content chunking
        return true;
    }

    @Override
    public InputStream getContent() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
    	if(outstream == null) {
    		throw new NullPointerException("OutStream is NULL");
    	}
        final GZIPOutputStream gzip = new GZIPOutputStream(outstream);
        try {
            wrappedEntity.writeTo(gzip);
        } finally {
            gzip.close();
        }
    }
}
