package com.pack.pack.markup.gen.util;

import java.io.File;

import com.pack.pack.security.util.EncryptionUtil;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
public class PathUtil {

	private PathUtil() {
	}

	public static String calculatePathForTopicDetailsPage(String topicId,
			String topicCategory) {
		String path = SystemPropertyUtil.getWebPagesRootPath();
		if (path != null) {
			StringBuilder pathBuilder = new StringBuilder(path);
			if (!path.endsWith(File.separator)) {
				pathBuilder = pathBuilder.append(File.separator);
			}
			String dirName = EncryptionUtil.encryptTextUsingSystemKey(topicId);
			pathBuilder = pathBuilder.append("topics").append(File.separator)
					.append(topicCategory).append(File.separator)
					.append(dirName);
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
}