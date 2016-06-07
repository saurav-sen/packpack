package com.pack.pack.client.api;

/**
 * 
 * @author Saurav
 *
 */
public interface MultipartRequestProgressListener {

	public void countTransferProgress(long progress, long total);
}