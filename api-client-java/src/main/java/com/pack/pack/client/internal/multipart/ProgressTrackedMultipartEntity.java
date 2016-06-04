package com.pack.pack.client.internal.multipart;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.MultipartEntity;

import com.pack.pack.client.api.MultipartRequestProgressListener;

/**
 * 
 * @author Saurav
 *
 */
public class ProgressTrackedMultipartEntity extends MultipartEntity {
	
	private MultipartRequestProgressListener listener;

	public ProgressTrackedMultipartEntity(MultipartRequestProgressListener listener) {
		super();
		this.listener = listener;
	}
	
	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, listener));
	}
}