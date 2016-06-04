package com.pack.pack.client.internal.multipart;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.pack.pack.client.api.MultipartRequestProgressListener;

/**
 * 
 * @author Saurav
 *
 */
public class CountingOutputStream extends FilterOutputStream {
	
	private MultipartRequestProgressListener listener;
	
	private long transferred = 0;

	public CountingOutputStream(OutputStream out, MultipartRequestProgressListener listener) {
		super(out);
		this.listener = listener;
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
		if(listener != null) {
			transferred += len;
			listener.countTransferProgress(transferred);
		}
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
		if(listener != null) {
			transferred += b.length;
			listener.countTransferProgress(transferred);
		}
	}
	
	@Override
	public void write(int b) throws IOException {
		super.write(b);
		if(listener != null) {
			transferred++;
			listener.countTransferProgress(transferred);
		}
	}
}