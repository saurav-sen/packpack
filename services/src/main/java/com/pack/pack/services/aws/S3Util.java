package com.pack.pack.services.aws;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.redis.RedisCacheService;
import com.pack.pack.services.registry.ServiceRegistry;

import static com.pack.pack.util.SystemPropertyUtil.*;

/**
 * 
 * @author Saurav
 *
 */
public class S3Util {

	public static final String SUFFIX = "/";
	
	private static Logger LOG = LoggerFactory.getLogger(S3Util.class);
	
	private static final String RELATIVE_URL_REDIS_KEY_PREFIX = "relativeUrl:";
	
	public static void main(String[] args) {
		File file = new File("C:/Users/CipherCloud/Pictures/DSC06028.JPG");
		S3Path s3Path = new S3Path("2b5ed241c514196e670ee73df1f540fa", false);
		s3Path.addChild(new S3Path("abc", false)).addChild(
				new S3Path("DSC06028.JPG", true));
		uploadFileToS3Bucket_1(file, s3Path);
	}
	
	public static boolean isPublishedUrl(String relativeUrl) {
		try {
			RedisCacheService service = ServiceRegistry.INSTANCE.findService(RedisCacheService.class);
			Boolean bool = service.getFromCache(RELATIVE_URL_REDIS_KEY_PREFIX + relativeUrl, Boolean.class);
			return bool != null ? false : true;
		} catch (PackPackException e) {
			LOG.info(e.getMessage(), e);
		}
		return false;
	}
	
	public static void uploadFileToS3Bucket(File file, S3Path s3Path,
			String relativeUrl, boolean isCompressed) throws PackPackException {
		uploadFileToS3Bucket(file, s3Path, relativeUrl, isCompressed, false);
	}

	public static void uploadFileToS3Bucket(File file, S3Path s3Path,
			String relativeUrl, boolean isCompressed, boolean isVideo)
			throws PackPackException {
		if (!isProductionEnvironment())
			return;
		RedisCacheService service = ServiceRegistry.INSTANCE
				.findService(RedisCacheService.class);
		service.addToCache(RELATIVE_URL_REDIS_KEY_PREFIX + relativeUrl, true);
		S3UploadTaskExecutor.INSTANCE.execute(new UploadTaskImpl(file, s3Path,
				relativeUrl, isCompressed, isVideo));
		// return str.toString();
	}
	
	private static void uploadFileToS3Bucket_0(File file, S3Path s3Path, String relativeUrl) {
		uploadFileToS3Bucket_1(file, s3Path);
	}
	
	private static String uploadFileToS3Bucket_1(File file, S3Path s3Path) {
		AWSCredentials credentials = new BasicAWSCredentials(
				getAwsS3AccessKey(), getAwsS3AccessSecret());
		AmazonS3 s3client = new AmazonS3Client(credentials);
		String bucketName = getAwsS3RootBucketName();
		StringBuilder str = new StringBuilder();
		while (s3Path != null && !s3Path.isFile()) {
			String folderName = s3Path.getName();
			str.append(folderName);
			if (!checkExists(bucketName, str.toString(), s3client)) {
				//createFolder(bucketName, folderName, s3client);
				createFolder(bucketName, str.toString(), s3client);
			}
			str.append(SUFFIX);
			s3Path = s3Path.getChild();
		}
		if (s3Path != null && s3Path.isFile()) {
			str.append(s3Path.getName());
			String fileName = str.toString();
			s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
					.withCannedAcl(CannedAccessControlList.PublicRead));
		}
		return str.toString();
	}

	private static boolean checkExists(String bucketName, String s3Path,
			AmazonS3 s3client) {
		ObjectMetadata objectMetadata = null;
		try {
			objectMetadata = s3client.getObjectMetadata(bucketName, s3Path);
		} catch (AmazonServiceException e) {
			return false;
		} catch (AmazonClientException e) {
			return false;
		}
		return objectMetadata != null;
	}

	private static void createFolder(String bucketName, String folderName,
			AmazonS3 s3client) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);
		s3client.putObject(putObjectRequest);
	}
	
	private static class UploadTaskImpl implements UploadTask {
		
		private File file;
		
		private S3Path s3Path;
		
		private String relativeUrl;
		
		private boolean isCompressed;
		
		private boolean isVideo;
		
		UploadTaskImpl(File file, S3Path s3Path, String relativeUrl, boolean isCompressed, boolean isVideo) {
			this.file = file;
			this.s3Path = s3Path;
			this.relativeUrl = relativeUrl;
			this.isCompressed = isCompressed;
			this.isVideo = isVideo;
		}
		
		private File compressIfRequired(File originalFile) {
			return originalFile;
		}
		
		@Override
		public void execute() throws Exception {
			long t0 = System.currentTimeMillis();
			File originalFile = this.file;
			if (isVideo && !isCompressed) {
				originalFile = compressIfRequired(originalFile);
			}
			uploadFileToS3Bucket_0(originalFile, this.s3Path, this.relativeUrl);
			long t1 = System.currentTimeMillis();
			LOG.info("Total Time to upload file <" + originalFile.getName()
					+ "> to S3 bucket = " + (t1 - t0) / (1000 * 60)
					+ " minutes");
			RedisCacheService service = ServiceRegistry.INSTANCE
					.findService(RedisCacheService.class);
			service.removeFromCache(RELATIVE_URL_REDIS_KEY_PREFIX
					+ this.relativeUrl);
		}
	}
}