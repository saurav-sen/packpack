package com.pack.pack.rest.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * 
 * @author Saurav
 *
 */
public class ImageUtil {

	public static Response buildResponse(File file) throws FileNotFoundException {
		final FileInputStream fStream = new FileInputStream(file);
		StreamingOutput octetStream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				try {
					pipe(fStream, output);
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			}

			public void pipe(InputStream inStream, OutputStream outStream)
					throws IOException {
				int count = -1;
				byte[] buffer = new byte[1024];
				while ((count = inStream.read(buffer)) > -1) {
					outStream.write(buffer, 0, count);
				}
				outStream.close();
			}
		};
		return Response.ok(octetStream).build();
	}
}