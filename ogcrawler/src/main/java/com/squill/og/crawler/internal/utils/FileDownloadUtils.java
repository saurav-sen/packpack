package com.squill.og.crawler.internal.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;

/**
 * 
 * @author Saurav
 * @since 13-Mar-2015
 * 
 */
public class FileDownloadUtils {
	
	public static void downloadHtmlBody(String text, String fileName, String downloadHomeDir) throws Exception {
		OutputStream output = null;

		File downloadDir = new File(downloadHomeDir);
		if (!downloadDir.exists()) {
			if (!downloadDir.mkdir()) {
				throw new Exception("Failed to create Download Directory: "
						+ downloadHomeDir);
			}
		}
		
		try {
			System.out.println("Downloading plain-text file...");
			fileName = fileName.replaceAll(",", "_");
			fileName = fileName.replaceAll("\\|", "_");
			output = new BufferedOutputStream(new FileOutputStream(downloadHomeDir + "./" + fileName));
			byte[] buffer = text.getBytes();
			output.write(buffer);
			System.out.println("Plain-text File successfully downloaded!");
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static void downloadAttachment(HttpResponse response, String fileName, String downloadHomeDir) throws Exception {
		InputStream input = null;
		OutputStream output = null;
		byte[] buffer = new byte[CoreConstants2.BLOCK_SIZE];

		File downloadDir = new File(downloadHomeDir);
		if (!downloadDir.exists()) {
			if (!downloadDir.mkdir()) {
				throw new Exception("Failed to create Download Directory: "
						+ downloadHomeDir);
			}
		}
		
		try {
			System.out.println("Downloading Attachment/report ...");
			input = response.getEntity().getContent();
			output = new FileOutputStream(downloadHomeDir + "./" + fileName);
			for (int length; (length = input.read(buffer)) > 0;) {
				output.write(buffer, 0, length);
			}
			System.out.println("Attachment successfully downloaded!");
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException logOrIgnore) {
				}
			if (input != null)
				try {
					input.close();
				} catch (IOException logOrIgnore) {
				}
		}
	}
}