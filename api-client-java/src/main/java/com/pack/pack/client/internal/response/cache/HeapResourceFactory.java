package com.pack.pack.client.internal.response.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HeapResourceFactory implements ResourceFactory {

    public Resource generate(
            final String requestId,
            final InputStream instream,
            final InputLimit limit) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        long total = 0;
        int l;
        while ((l = instream.read(buf)) != -1) {
            outstream.write(buf, 0, l);
            total += l;
            if (limit != null && total > limit.getValue()) {
                limit.reached();
                break;
            }
        }
        return new HeapResource(outstream.toByteArray());
    }

    public Resource copy(
            final String requestId,
            final Resource resource) throws IOException {
        byte[] body;
        if (resource instanceof HeapResource) {
            body = ((HeapResource) resource).getByteArray();
        } else {
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            IOUtils.copyAndClose(resource.getInputStream(), outstream);
            body = outstream.toByteArray();
        }
        return new HeapResource(body);
    }

}