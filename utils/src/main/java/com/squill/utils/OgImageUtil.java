package com.squill.utils;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public final class OgImageUtil {

	private static final Logger $LOG = LoggerFactory
			.getLogger(OgImageUtil.class);

	private OgImageUtil() {
	}

	public static void main(String[] args) {
		System.out
				.println(downloadImage(
						"https://static.toiimg.com/thumb/msid-69999953,width-1070,height-580,imgsize-475928,resizemode-6,overlay-toi_sw,pt-32,y_pad-40/photo.jpg",
						"http://www.squill.in/sh/", "F:/image-magic-POC"));
	}

	public static String downloadImage(String ogImageUrl) {
		return downloadImage(ogImageUrl,
				SystemPropertyUtil.getExternalSharedLinkBaseUrl(),
				SystemPropertyUtil.getDefaultArchiveHtmlFolder());
	}

	private static String downloadImage(String ogImageUrl, String linkBaseUrl,
			String baseStorageDirectory) {
		String newOgImageUrl = linkBaseUrl;
		if (!newOgImageUrl.trim().endsWith("/")) {
			newOgImageUrl = newOgImageUrl.trim() + "/";
		}
		$LOG.debug("Input ogImageUrl = " + ogImageUrl);
		String today = DateTimeUtil.today("_");
		newOgImageUrl = newOgImageUrl + today + "/";
		String defaultArchiveHtmlFolder = baseStorageDirectory;
		if (!defaultArchiveHtmlFolder.trim().endsWith(File.separator)) {
			defaultArchiveHtmlFolder = defaultArchiveHtmlFolder
					+ File.separator;
		}
		String fileName = FilenameUtils.getName(ogImageUrl);
		if (fileName == null) {
			fileName = UUID.randomUUID().toString() + ".jpg";
		} else {
			String fileExtension = ".jpg";
			int index = fileName.lastIndexOf(".");
			if (index >= 0) {
				fileExtension = fileName.substring(index);
			}
			fileName = UUID.randomUUID().toString() + fileExtension;
		}
		newOgImageUrl = newOgImageUrl + fileName;
		String targetDirectory = defaultArchiveHtmlFolder + today;
		File targetDirectoryFile = new File(targetDirectory);
		if (!targetDirectoryFile.exists()) {
			boolean success = targetDirectoryFile.mkdir();
			if (!success) {
				$LOG.debug("Failed to create directory " + targetDirectory
						+ ", hence skipping download and resize of ogImage");
			}
		}
		OutputStream imageWriter = null;
		try {
			URL imageUrl = new URL(ogImageUrl);
			InputStream imageReader = new BufferedInputStream(
					imageUrl.openStream());
			String filePath = targetDirectory + File.separator + fileName;
			imageWriter = new BufferedOutputStream(new FileOutputStream(
					filePath));
			int readByte;

			while ((readByte = imageReader.read()) != -1) {
				imageWriter.write(readByte);
			}
			if (imageWriter != null) {
				imageWriter.close();
			}
			/*BufferedImage bitmap = ImageIO.read(new File(filePath));
			ImageDimension dimension = calculateResizeDimensions(bitmap);*/
			ImageDimension dimension = readOriginalImageDimension(filePath);
			if(dimension == null) {
				$LOG.error("Failed reading original image dimension @ " + filePath + " for ogImage @ " + ogImageUrl);
				newOgImageUrl = ogImageUrl;
				return newOgImageUrl;
			}
			dimension = calculateResizeDimensions(dimension.newHeight, dimension.newWidth);
			if(dimension == null) {
				$LOG.error("Failed calculating new image dimension @ " + filePath + " for ogImage @ " + ogImageUrl);
				newOgImageUrl = ogImageUrl;
				return newOgImageUrl;
			}
			if (!resizeImage(filePath, dimension)) {
				$LOG.error("Failed to resize" + newOgImageUrl);
				newOgImageUrl = ogImageUrl;
				return newOgImageUrl;
			}
		} catch (Exception e) {
			$LOG.error("Input ogImageUrl = " + ogImageUrl);
			$LOG.error(e.getMessage(), e);
			newOgImageUrl = ogImageUrl;
		}
		$LOG.debug("Output ogImageUrl = " + ogImageUrl);
		return newOgImageUrl;
	}

	private static class ImageDimension {
		private int newHeight;
		private int newWidth;

		ImageDimension(int newHeight, int newWidth) {
			this.newHeight = newHeight;
			this.newWidth = newWidth;
		}
	}

	private static boolean resizeImage(String filePath, ImageDimension dimension)
			throws IOException, InterruptedException {
		if(dimension == null)
			return false;
		StringBuilder cmd = new StringBuilder();
		cmd.append("magick convert ");
		cmd.append(filePath);
		cmd.append(" ");
		cmd.append("-resize");
		cmd.append(" ");
		cmd.append(dimension.newWidth);
		cmd.append("X");
		cmd.append(dimension.newHeight);
		cmd.append(" ");
		cmd.append(filePath);
		$LOG.debug("Resize ogImage COmmand = " + cmd.toString());
		Process p = Runtime.getRuntime().exec(cmd.toString());
		return p.waitFor() == 0;
	}

	/*private static ImageDimension calculateResizeDimensions(BufferedImage bitmap) {
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		return calculateResizeDimensions(height, width);
	}*/
	
	
	private static ImageDimension readOriginalImageDimension(String imageFilePath) {
		try {
			StringBuilder cmd = new StringBuilder();
			cmd.append("magick identify -ping -format %w:%h ");
			cmd.append(imageFilePath);
			$LOG.debug("Identify original image dimension COmmand = " + cmd.toString());
			Process p = Runtime.getRuntime().exec(cmd.toString());
			String line = null;
			StringBuilder output = new StringBuilder();
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = input.readLine()) != null) {
				output.append(line);
			}
			input.close();
			String[] splits = output.toString().trim().split(":");
			if(splits.length != 2) {
				$LOG.error("Identify original image dimension COmmand Output = " + output.toString());
				return null;
			}
			int width = Integer.parseInt(splits[0].trim());
			int height = Integer.parseInt(splits[1].trim());
			return new ImageDimension(height, width);
		} catch (Exception e) {
			return null;
		}
	}
	
	/*private static ImageDimension calculateResizeDimensions(String imageFilePath) {
		StringBuilder cmd = new StringBuilder();
		cmd.append("magick identify -ping -format '%w %h' ");
		cmd.append(imageFilePath);
		$LOG.debug("Identify original image dimension COmmand = " + cmd.toString());
		Process p = Runtime.getRuntime().exec(cmd.toString());
		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		while ((line = input.readLine()) != null) {
			System.out.println(line);
		}
		input.close();
		return calculateResizeDimensions(height, width);
	}*/

	private static ImageDimension calculateResizeDimensions(int bitmapHeight,
			int bitmapWidth) {
		// int shortSideMax = 900;
		// int longSideMax = 1440;

		Point size = new Point(800, 1350);

		int shortSideMax = size.x;
		int longSideMax = size.x + 200;// (int)(size.y * 0.6f);

		int height = bitmapHeight;
		int width = bitmapWidth;

		float aspectRatio = (float) width / (float) height;
		if (aspectRatio >= 1.2f) {
			longSideMax = size.x;
			shortSideMax = (int) (longSideMax / 1.2f);
		} else if (aspectRatio <= 0.8f) {
			shortSideMax = size.x;
			longSideMax = (int) (shortSideMax / 0.8f);
		}

		float resizeRatio = 1.0f;
		if (width >= height) {
			if (width <= longSideMax && height <= shortSideMax) {
				float wRatio = (float) longSideMax / (float) width;
				float hRatio = (float) shortSideMax / (float) height;
				resizeRatio = Math.min(wRatio, hRatio);
				// return new ImageDimension(height, width);
			} else {
				float wRatio = (float) longSideMax / (float) width;
				float hRatio = (float) shortSideMax / (float) height;
				resizeRatio = Math.min(wRatio, hRatio);
			}
			/*
			 * if(resizeRatio == 1.0f) { resizeRatio = 1.2f; }
			 */
		} else {
			if (height <= longSideMax && width <= shortSideMax) {
				float wRatio = (float) longSideMax / (float) width;
				float hRatio = (float) shortSideMax / (float) height;
				resizeRatio = Math.min(wRatio, hRatio);
				// return new ImageDimension(height, width);
			} else {
				float wRatio = (float) shortSideMax / (float) width;
				float hRatio = (float) longSideMax / (float) height;
				resizeRatio = Math.min(wRatio, hRatio);
			}
			/*
			 * if(resizeRatio == 1.0f) { resizeRatio = 1.2f; }
			 */
		}
		height = (int) (height * resizeRatio);
		width = (int) (width * resizeRatio);
		return new ImageDimension(height, width);
	}
}
