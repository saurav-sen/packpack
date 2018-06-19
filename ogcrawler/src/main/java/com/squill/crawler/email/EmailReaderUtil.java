package com.squill.crawler.email;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Saurav
 *
 */
public class EmailReaderUtil {
	
	private static final Pattern REGEX = Pattern
			.compile("(?:(?:https?|ftp):\\/\\/)?[\\w/\\-?=%.]+\\.[\\w/\\-?=%.]+");
	
	private static final Logger LOG = LoggerFactory.getLogger(EmailReaderUtil.class);

	private EmailReaderUtil() {
	}

	public static Set<String> extractLinks(Message message)
			throws MessagingException, IOException {
		String textBody = getTextFromMessage(message);
		return extractLinksFromTextBody(textBody);
	}

	private static Set<String> extractLinksFromTextBody(String textBody) {
		Matcher matcher = REGEX.matcher(textBody);
		Set<String> links = new HashSet<String>();
		while (matcher.find()) {
			links.add(matcher.group());
		}
		return filter(links);
	}

	private static Set<String> filter(Set<String> links) {
		Iterator<String> itr = links.iterator();
		while (itr.hasNext()) {
			String link = itr.next();
			try {
				link = link.trim();
				if (!link.startsWith("https://") && !link.startsWith("http://")) {
					link = "http://" + link;
				}
				URL url = new URL(link);
				String path = url.getPath();
				if (path == null || path.trim().isEmpty()) {
					itr.remove();
				}
			} catch (MalformedURLException e) {
				LOG.error(e.getMessage(), e);
				itr.remove();
			}
		}
		return links;
	}

	public static String getTextFromMessage(Message message)
			throws MessagingException, IOException {
		String result = "";
		if (message.isMimeType("text/plain")) {
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)
			throws MessagingException, IOException {
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				result = result + "\n" + bodyPart.getContent();
				break; 
			} /*else if (bodyPart.isMimeType("text/html")) {
				String html = (String) bodyPart.getContent();
				result = result + "\n" + Jsoup.parse(html).text();
			}*/ else if (bodyPart.getContent() instanceof MimeMultipart) {
				result = result
						+ getTextFromMimeMultipart((MimeMultipart) bodyPart
								.getContent());
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		String text = "The link of this question: https://stackoverflow.com/questions/6038061/regular-expression-to-find-urls-within-a-string\n"
				+ "Also there are some urls: www.google.com, facebook.com, http://test.com/method?param=wasd\n"
				+ "The code below catches all urls in text and returns urls in list.";
		Set<String> links = extractLinksFromTextBody(text);
		for (String link : links) {
			System.out.println(link);
		}
	}
}
