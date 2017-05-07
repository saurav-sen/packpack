package com.pack.pack.markup.gen.util;

import java.io.File;

import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class PromotedFileUtil {

	private PromotedFileUtil() {
	}

	public static String calculatePathForTopicDetailsPage(
			String encryptedTopicId) {
		return calculatePathForTopicDetailsPage(encryptedTopicId, null);
	}

	public static String calculatePathForTopicDetailsPage(
			String encryptedTopicId, String topicCategory) {
		String path = SystemPropertyUtil.getWebPagesRootPath();
		if (path != null) {
			StringBuilder pathBuilder = new StringBuilder(path);
			if (!path.endsWith(File.separator)) {
				pathBuilder = pathBuilder.append(File.separator);
			}
			pathBuilder = pathBuilder.append("topics");
			File file = new File(pathBuilder.toString());
			if (!file.exists()) {
				file.mkdir();
			}
			if (topicCategory != null && !topicCategory.isEmpty()) {
				pathBuilder = pathBuilder.append(File.separator).append(
						topicCategory);
				file = new File(pathBuilder.toString());
				if (!file.exists()) {
					file.mkdir();
				}
			}
			/*
			 * pathBuilder= pathBuilder.append(File.separator)
			 * .append(encryptedTopicId); file = new File(path); if
			 * (!file.exists()) { file.mkdir(); }
			 */
			pathBuilder = pathBuilder.append(File.separator)
					.append(encryptedTopicId).append(".html");
			path = pathBuilder.toString();
		}
		return path;
	}

	public static String calculatePathForPackDetailsPage(String encryptedPackId) {
		String path = SystemPropertyUtil.getWebPagesRootPath();
		if (path != null) {
			StringBuilder pathBuilder = new StringBuilder(path);
			if (!path.endsWith(File.separator)) {
				pathBuilder = pathBuilder.append(File.separator);
			}
			pathBuilder = pathBuilder.append("packs").append(File.separator)
					.append(encryptedPackId);
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			pathBuilder = pathBuilder.append(File.separator).append(
					"index.html");
			path = pathBuilder.toString();
		}
		return path;
	}

	public static String calculatePathForPromotedAttachmentPage(
			String attachmentId) {
		String path = SystemPropertyUtil.getWebPagesRootPath();
		if (path != null) {
			StringBuilder pathBuilder = new StringBuilder(path);
			if (!path.endsWith(File.separator)) {
				pathBuilder = pathBuilder.append(File.separator);
			}
			pathBuilder = pathBuilder.append("attachments");/*
															 * .append(File.
															 * separator)
															 * .append
															 * (attachmentId);
															 */
			File file = new File(pathBuilder.toString());
			if (!file.exists()) {
				file.mkdir();
			}
			pathBuilder = pathBuilder.append(File.separator).append(
					attachmentId + ".html");
			path = pathBuilder.toString();
		}
		return path;
	}
}