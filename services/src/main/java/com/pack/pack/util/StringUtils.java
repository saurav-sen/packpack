package com.pack.pack.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Saurav
 *
 */
public final class StringUtils {

	private static final Logger LOG = LoggerFactory
			.getLogger(StringUtils.class);

	private static final String UTF_8 = "UTF-8";

	private StringUtils() {
	}

	public static String compress(String content) {
		if (content == null || content.length() == 0) {
			return content;
		}

		try {
			/*LOG.debug("Content length to compress : " + content.length());
			ByteArrayOutputStream obj = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			gzip.write(content.getBytes(UTF_8));
			gzip.close();
			String compressedContent = obj.toString(UTF_8);
			LOG.debug("Compressed content length : "
					+ compressedContent.length());*/
			String compressedContent = content;
			byte[] encodedBytes = Base64.getEncoder().encode(
					compressedContent.getBytes());
			compressedContent = new String(encodedBytes);
			LOG.debug("Compressed content length (Post Encoding) : "
					+ compressedContent.length());
			return compressedContent;
		} /*catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
			return content;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return content;
		}*/ finally {
			
		}
	}

	public static String decompress(String content) {
		if (content == null || content.length() == 0) {
			return content;
		}

		String decodedContent = content;
		try {
			LOG.debug("Content length to decompress : " + content.length());
			byte[] decodedBytes = Base64.getDecoder().decode(
					content.getBytes(UTF_8));
			decodedContent = new String(decodedBytes);
			/*LOG.debug("Content length to decompress (Post Decoding) : "
					+ decodedContent.length());
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(
					decodedContent.getBytes(UTF_8)));
			BufferedReader bf = new BufferedReader(new InputStreamReader(gis,
					UTF_8));
			String decompressedContent = "";
			String line;
			while ((line = bf.readLine()) != null) {
				decompressedContent += line;
			}
			LOG.debug("Decompressed content lenght : "
					+ decompressedContent.length());
			return decompressedContent;*/
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
			return decodedContent;
		} /*catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return decodedContent;
		}*/
		return decodedContent;
	}
	
	public static String stringify(Collection<?> args) {
		if (args == null) {
			StringBuilder str = new StringBuilder();
			str.append("[");
			str.append("]");
			return str.toString();
		}
		return stringify(args.toArray());
	}
	
	public static String stringify(long[] args) {
		StringBuilder str = new StringBuilder();
		str.append("[");
		if (args == null || args.length == 0) {
			str.append("]");
			return str.toString();
		}
		int len = args.length - 1;
		if (len == 0) {
			Object arg = args[len];
			str.append(arg.toString());
		} else {
			for (int i = 0; i < len; i++) {
				long arg = args[i];
				str.append(arg);
				str.append(", ");
			}
			str.append(args[len]);
		}
		str.append("]");
		return str.toString();
	}
	
	public static String stringify(int[] args) {
		StringBuilder str = new StringBuilder();
		str.append("[");
		if (args == null || args.length == 0) {
			str.append("]");
			return str.toString();
		}
		int len = args.length - 1;
		if (len == 0) {
			Object arg = args[len];
			str.append(arg.toString());
		} else {
			for (int i = 0; i < len; i++) {
				int arg = args[i];
				str.append(arg);
				str.append(", ");
			}
			str.append(args[len]);
		}
		str.append("]");
		return str.toString();
	}
	
	public static String stringify(double[] args) {
		StringBuilder str = new StringBuilder();
		str.append("[");
		if (args == null || args.length == 0) {
			str.append("]");
			return str.toString();
		}
		int len = args.length - 1;
		if (len == 0) {
			Object arg = args[len];
			str.append(arg.toString());
		} else {
			for (int i = 0; i < len; i++) {
				double arg = args[i];
				str.append(arg);
				str.append(", ");
			}
			str.append(args[len]);
		}
		str.append("]");
		return str.toString();
	}
	
	public static String stringify(float[] args) {
		StringBuilder str = new StringBuilder();
		str.append("[");
		if (args == null || args.length == 0) {
			str.append("]");
			return str.toString();
		}
		int len = args.length - 1;
		if (len == 0) {
			Object arg = args[len];
			str.append(arg.toString());
		} else {
			for (int i = 0; i < len; i++) {
				float arg = args[i];
				str.append(arg);
				str.append(", ");
			}
			str.append(args[len]);
		}
		str.append("]");
		return str.toString();
	}

	public static String stringify(Object[] args) {
		StringBuilder str = new StringBuilder();
		str.append("[");
		if (args == null || args.length == 0) {
			str.append("]");
			return str.toString();
		}
		int len = args.length - 1;
		if (len == 0) {
			Object arg = args[len];
			str.append(arg.toString());
		} else {
			for (int i = 0; i < len; i++) {
				Object arg = args[i];
				str.append(arg.toString());
				str.append(", ");
			}
			str.append(args[len].toString());
		}
		str.append("]");
		return str.toString();
	}
}