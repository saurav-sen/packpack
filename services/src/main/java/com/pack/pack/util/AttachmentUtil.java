package com.pack.pack.util;

import static com.pack.pack.util.SystemPropertyUtil.isProductionEnvironment;

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
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.services.aws.S3Path;
import com.pack.pack.services.aws.S3Util;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public class AttachmentUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(AttachmentUtil.class);

	/*private static final int THUMBNAIL_WIDTH = 100;
	private static final int THUMBNAIL_HEIGHT = 100;*/
	
	public static File resizeAndStoreUploadedAttachment(InputStream inputStream,
			String fileLoc, int width, int height, S3Path s3Path, String relativeUrl) throws PackPackException {
		File attachmentFile = storeUploadedAttachment(inputStream, fileLoc, s3Path, relativeUrl, true);
		return createThumnailForImage(attachmentFile, width, height, s3Path);
	}

	public static File storeUploadedAttachment(InputStream inputStream,
			String fileLoc, S3Path s3Path, String relativeUrl, boolean isCompressed) throws PackPackException {
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
			S3Util.uploadFileToS3Bucket(attachmentFile, s3Path, relativeUrl, isCompressed);
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
	
	public static void main(String[] args) throws Exception {
		File f = new File("C:/Users/CipherCloud/Pictures/DSC06028.JPG");
		S3Path root = new S3Path("DSC06028", false);
		root.addChild(new S3Path("test.jpg", true));
		createThumnailForImage(f, 500, 500, root);
	}

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
			
			if (isProductionEnvironment()) {
				s3Path.addChild(new S3Path("thumbnail", true));
			}
			//s3Path.getParent().addChild(new S3Path("thumbnail", true));
			
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
			
			// Calculate relative URL.
			StringBuilder str = new StringBuilder(S3Util.SUFFIX);
			S3Path tmp = s3Path;
			while (s3Path != null && !s3Path.isFile()) {
				String folderName = s3Path.getName();
				str.append(folderName);
				str.append(S3Util.SUFFIX);
				s3Path = s3Path.getChild();
			}
			s3Path = tmp;
			// Upload to S3 bucket.
			S3Util.uploadFileToS3Bucket(thumbnailImageFile, s3Path, str.toString(), true);
			
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
	
	private static BufferedImage grabFrameFromVideoFile(File videoFile)
			throws IOException, JCodecException {
		File outputFile = videoFile;
		/*String fFmpegCommand = SystemPropertyUtil.getFFmpegCommand();
		 if(fFmpegCommand != null) {
			String tmpDir = System.getProperty("java.io.tmpdir");
			if(!tmpDir.endsWith(File.separator)) {
				tmpDir = tmpDir + File.separator;
			}
			String outputFilePath = tmpDir + videoFile.getName().substring(0, videoFile.getName().lastIndexOf(".")) + ".mp4";
			fFmpegCommand = fFmpegCommand + " -i " + videoFile.getAbsolutePath() + " " + outputFilePath;
			Process process = Runtime.getRuntime().exec(fFmpegCommand);
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			outputFile = new File(outputFilePath);
		}*/
		BufferedImage frameImage = null;
		FileChannelWrapper ch = null;
		int frameNo = 1;
		try {
			ch = NIOUtils.readableFileChannel(outputFile);
			frameImage = ((FrameGrab) new FrameGrab(ch)
					.seekToFramePrecise(frameNo)).getFrame();
		} finally {
			NIOUtils.closeQuietly(ch);
		}
		return frameImage;
	}
	
	
	/*public static void main(String[] args) throws Exception {
		BufferedImage grabFrameFromVideoFile = grabFrameFromVideoFile(new File("D:/Saurav/VM/packpack/65547c86-b2ba-448f-8f32-4206a7d49376_2.mp4"));
		assert(grabFrameFromVideoFile != null);
	}*/

	public static File createThumnailForVideo(File videoFile, S3Path s3Path)
			throws PackPackException {
		try {
			BufferedImage frameImage = grabFrameFromVideoFile(videoFile);
			if(frameImage == null)
				return null;
			/*BufferedImage thumbnailImage = Scalr.resize(frameImage, Method.QUALITY,
					Mode.AUTOMATIC, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT,
					Scalr.OP_ANTIALIAS);*/
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
			ImageIO.write(frameImage, "jpg", thumbnailImageFile);
			
			// Calculate relative URL.
			StringBuilder str = new StringBuilder(S3Util.SUFFIX);
			S3Path tmp = s3Path;
			while (s3Path != null && !s3Path.isFile()) {
				String folderName = s3Path.getName();
				str.append(folderName);
				str.append(S3Util.SUFFIX);
				s3Path = s3Path.getChild();
			}
			s3Path = tmp;
			// Upload to S3 bucket.
			S3Util.uploadFileToS3Bucket(thumbnailImageFile, s3Path, str.toString(), true);
			
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