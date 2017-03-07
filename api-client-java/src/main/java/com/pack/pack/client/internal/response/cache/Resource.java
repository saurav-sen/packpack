package com.pack.pack.client.internal.response.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * 
 * @author Saurav
 *
 */
public interface Resource extends Serializable {

    /**
     * Returns an {@link InputStream} from which the response
     * body can be read.
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Returns the length in bytes of the response body.
     */
    long length();

    /**
     * Indicates the system no longer needs to keep this
     * response body and any system resources associated with
     * it may be reclaimed.
     */
    void dispose();

}