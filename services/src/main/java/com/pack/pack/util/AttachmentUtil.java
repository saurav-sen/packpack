package com.pack.pack.util;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.FrameGrab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public class AttachmentUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(AttachmentUtil.class);

	private static final int THUMBNAIL_WIDTH = 100;
	private static final int THUMBNAIL_HEIGHT = 100;
	
	public static File resizeAndStoreUploadedAttachment(InputStream inputStream,
			String fileLoc, int width, int height, S3Path s3Path) throws PackPackException {
		File attachmentFile = storeUploadedAttachment(inputStream, fileLoc, s3Path);
		return createThumnailForImage(attachmentFile, width, height, s3Path);
	}

	public static File storeUploadedAttachment(InputStream inputStream,
			String fileLoc, S3Path s3Path) throws PackPackException {
		OutputStream outStream = null;
		File attachmentFile = new File(fileLoc);
		try {
			outStream = new FileOutputStream(attachmentFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			//outStream = new GZIPOutputStream(new FileOutputStream(attachmentFile));
			while ((read = inputStream.read(bytes)) != -1) {
				outStream.write(bytes, 0, read);
			}
			outStream.flush();
			
			// Upload to S3 bucket.
			S3Util.uploadFileToS3Bucket(attachmentFile, s3Path);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		} finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new PackPackException("TODO", e.getMessage(), e);
			}
		}
		return attachmentFile;
	}
	
	/*public static File createThumnailForImage(File imageFile)
			throws PackPackException {
		return createThumnailForImage(imageFile, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
	}*/

	private static File createThumnailForImage(File imageFile, int width, int height, S3Path s3Path)
			throws PackPackException {
		try {
			BufferedImage image = ImageIO.read(imageFile);
			BufferedImage thumbnailImage = Scalr.resize(image, Method.QUALITY,
					Mode.AUTOMATIC, width, height,
					Scalr.OP_ANTIALIAS);
			File parentFile = imageFile.getParentFile();
			String path = parentFile.getAbsolutePath() + File.separator
					+ "thumbnail";
			
			s3Path.getParent().addChild(new S3Path("thumbnail", true));
			
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdir();
			}
			String imageFileName = imageFile.getName();
			path = path
					+ File.separator
					+ imageFileName
							.substring(0, imageFileName.lastIndexOf("."))
					+ ".jpg";
			File thumbnailImageFile = new File(path);
			ImageIO.write(thumbnailImage, "jpg", thumbnailImageFile);
			
			// Upload to S3 bucket.
			S3Util.uploadFileToS3Bucket(thumbnailImageFile, s3Path);
			
			return thumbnailImageFile;
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		} catch (ImagingOpException e) {
			logger.error(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}

	public static File createThumnailForVideo(File videoFile, S3Path s3Path)
			throws PackPackException {
		try {
			int frameNo = 10;
			BufferedImage frameImage = FrameGrab.getFrame(videoFile, frameNo);
			BufferedImage thumbnailImage = Scalr.resize(frameImage, Method.QUALITY,
					Mode.AUTOMATIC, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT,
					Scalr.OP_ANTIALIAS);
			File parentFile = videoFile.getParentFile();
			String path = parentFile.getAbsolutePath() + File.separator
					+ "thumbnail";
			
			s3Path.getParent().addChild(new S3Path("thumbnail", true));
			
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdir();
			}
			String imageFileName = videoFile.getName();
			path = path
					+ File.separator
					+ imageFileName
							.substring(0, imageFileName.lastIndexOf("."))
					+ ".jpg";
			File thumbnailImageFile = new File(path);
			ImageIO.write(thumbnailImage, "jpg", thumbnailImageFile);
			
			// Upload to S3 bucket.
			S3Util.uploadFileToS3Bucket(thumbnailImageFile, s3Path);
			
			return thumbnailImageFile;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		} catch (JCodecException e) {
			logger.error(e.getMessage(), e);
			throw new PackPackException("TODO", e.getMessage(), e);
		}
	}
}