package com.pack.pack.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import static com.pack.pack.util.SystemPropertyUtil.*;

/**
 * 
 * @author Saurav
 *
 */
public class S3Util {

	private static final String SUFFIX = "/";
	
	public static void main(String[] args) {
		File file = new File("C:/Users/CipherCloud/Pictures/DSC06028.JPG");
		S3Path s3Path = new S3Path("2b5ed241c514196e670ee73df1f540fa", false);
		s3Path.addChild(new S3Path("abc", false)).addChild(
				new S3Path("DSC06028.JPG", true));
		uploadFileToS3Bucket(file, s3Path);
	}

	public static String uploadFileToS3Bucket(File file, S3Path s3Path) {
		if (!isProductionEnvironment())
			return null;
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
}