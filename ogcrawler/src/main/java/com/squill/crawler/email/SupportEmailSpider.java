package com.squill.crawler.email;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.ext.email.SmtpMessage;
import com.pack.pack.services.ext.email.SmtpTLSMessageService;
import com.pack.pack.services.ext.text.summerize.WebDocumentParser;
import com.pack.pack.util.RssFeedUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.internal.utils.HtmlUtil;
import com.squill.og.crawler.internal.utils.HttpRequestExecutor;

/**
 * 
 * @author Saurav
 *
 */
public class SupportEmailSpider implements Spider {

	private Properties pop3TLSProperties;

	private static final String GMAIL_HOST = "smtp.gmail.com";

	private static final String SUPPORT_EMAIL_ADDR = "support@squill.co.in";
	private static final String SUPPORT_EMAIL_PASSWORD = "P@$$w0rd4SQui11";

	private static Logger $LOG = LoggerFactory
			.getLogger(SupportEmailSpider.class);

	public SupportEmailSpider() {
		pop3TLSProperties = new Properties();
		pop3TLSProperties.put("mail.pop3.host", GMAIL_HOST);
		pop3TLSProperties.put("mail.pop3.port", "995");
		pop3TLSProperties.put("mail.pop3.starttls.enable", "true");
	}

	@Override
	public void run() {
		try {
			uploadNewFeeds(pollMessagesIfAny());
		} catch (Exception e) {
			$LOG.error(e.getMessage(), e);
		}
	}

	private List<JRssFeed> pollMessagesIfAny() {
		List<JRssFeed> feeds = new LinkedList<JRssFeed>();
		Store emailStore = null;
		try {
			Session emailSession = Session
					.getDefaultInstance(pop3TLSProperties);

			emailStore = emailSession.getStore("pop3s");
			emailStore.connect(GMAIL_HOST, SUPPORT_EMAIL_ADDR,
					SUPPORT_EMAIL_PASSWORD);
			Folder inbox = emailStore.getFolder("INBOX");
			SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN),
					false);
			inbox.open(Folder.READ_ONLY);
			Message[] messages = inbox.search(searchTerm);
			Map<String, String> linkVsSubject = new HashMap<String, String>();
			if (messages.length > 0) {
				Set<String> registeredEmailPublishersList = EmailReaderUtil
						.registeredEmailPublishersList();
				if (registeredEmailPublishersList.isEmpty()) {
					$LOG.debug("No Registered Publishers in the List, hence skipping email upload job.");
					return Collections.emptyList();
				}
				for (int i = 0; i < messages.length; i++) {
					Message message = messages[i];
					String subject = message.getSubject();
					String from = message.getFrom()[0].toString();
					int index = from.indexOf("<");
					String displayName = from.substring(0, index);
					$LOG.info("Received Message From: " + from);
					String fromEmail = from.substring(index+1).trim();
					fromEmail = fromEmail.replaceAll(">", "");
					message.setFlag(Flag.SEEN, true);
					if (!registeredEmailPublishersList.contains(fromEmail)) {
						$LOG.debug(fromEmail + " is not there in registered publishers list, hence skipping upload unauthorized.");
						continue;
					}
					$LOG.info("Received Message Subject = " + subject);
					Set<String> extractedLinks = EmailReaderUtil
							.extractLinks(message);
					for (String extractedLink : extractedLinks) {
						linkVsSubject.put(extractedLink, subject);
					}

					Address[] replyTo = message.getReplyTo();
					if (replyTo != null && replyTo.length > 0) {
						String replyContent = "Thank you very much " + displayName + " for contributing your valued content with SQUILL.";
						/*String replyToAddr = InternetAddress.toString(message.getReplyTo());
						index = replyToAddr.indexOf("<");*/
						SmtpMessage smtpMessage = new SmtpMessage(
								fromEmail,
								subject, replyContent, false);
						SmtpTLSMessageService.INSTANCE.sendMessage(smtpMessage);
					} else {
						$LOG.debug("No replyTo found in the Email Message, hence acknowledgement skipped.");
					}
				}
			}
			emailStore.close();

			Iterator<String> itr = linkVsSubject.keySet().iterator();
			while (itr.hasNext()) {
				String link = itr.next();
				String subject = linkVsSubject.get(link);
				JRssFeed feed = read(subject, link);
				if (feed == null)
					continue;
				feeds.add(feed);
			}
		} catch (Exception e) {
			$LOG.error("Failed Polling messages from GMAIL support email.",
					e.getMessage(), e);
		} finally {
			try {
				if (emailStore != null && emailStore.isConnected()) {
					emailStore.close();
				}
			} catch (MessagingException e) {
				$LOG.error(e.getMessage(), e);
			}
		}
		return feeds;
	}

	private JRssFeed read(String subject, String link) throws Exception {
		String htmlContent = new HttpRequestExecutor().GET(link, null);
		JRssFeed feed = HtmlUtil.parse4mHtml(htmlContent);
		JRssFeedType feedType = resolveFeedType(subject);
		feed.setOgType(feedType.name());
		feed.setFeedType(feedType.name());
		if(feedType != JRssFeedType.REFRESHMENT) {
			feed = new WebDocumentParser().parseHtmlPayload(htmlContent);
			feed.setFeedType(feedType.name());
		}
		return feed;
	}

	private JRssFeedType resolveFeedType(String subject) {
		if (subject == null || subject.trim().isEmpty())
			return JRssFeedType.REFRESHMENT;
		subject = subject.trim();
		if (subject.equalsIgnoreCase("SCIENCE")
				|| subject.equalsIgnoreCase("TECHNOLOGY")
				|| subject.equalsIgnoreCase("SCIENCE AND TECHNOLOGY")
				|| subject.equalsIgnoreCase("SCIENCE & TECHNOLOGY")) {
			return JRssFeedType.NEWS_SCIENCE_TECHNOLOGY;
		} else if (subject.equalsIgnoreCase("SPORTS")) {
			return JRssFeedType.NEWS_SPORTS;
		} else if (subject.equalsIgnoreCase("ARTICLE")) {
			return JRssFeedType.ARTICLE;
		} else if (subject.equalsIgnoreCase("NEWS")
				|| subject.equalsIgnoreCase("POLITICS")
				|| subject.equalsIgnoreCase("CRIME")
				|| subject.equalsIgnoreCase("SOCIAL")
				|| subject.equalsIgnoreCase("WEATHER")
				|| subject.equalsIgnoreCase("DISTASTER")) {
			return JRssFeedType.NEWS;
		}
		return JRssFeedType.REFRESHMENT;
	}

	private void uploadNewFeeds(List<JRssFeed> feeds) {
		if (feeds == null || feeds.isEmpty())
			return;
		JRssFeeds newsFeeds = new JRssFeeds();
		JRssFeeds refrehmentFeeds = new JRssFeeds();
		for (JRssFeed feed : feeds) {
			if (JRssFeedType.REFRESHMENT.name().equals(feed.getFeedType())) {
				refrehmentFeeds.getFeeds().add(feed);
			} else {
				newsFeeds.getFeeds().add(feed);
			}
		}
		if (!refrehmentFeeds.getFeeds().isEmpty()) {
			TTL ttl = new TTL();
			ttl.setTime((short) 2);
			ttl.setUnit(TimeUnit.DAYS);
			long batchId = System.currentTimeMillis();
			try {
				$LOG.debug("Uploading REFRESHMENT from Email: " + JSONUtil.serialize(refrehmentFeeds));
			} catch (PackPackException e) {
				$LOG.error(e.getMessage(), e);
			}
			RssFeedUtil.uploadRefreshmentFeeds(refrehmentFeeds, ttl, batchId,
					true);
		}
		if (!newsFeeds.getFeeds().isEmpty()) {
			TTL ttl = new TTL();
			ttl.setTime((short) 1);
			ttl.setUnit(TimeUnit.DAYS);
			long batchId = System.currentTimeMillis();
			try {
				$LOG.debug("Uploading NEWS/OTHERS from Email: " + JSONUtil.serialize(newsFeeds));
			} catch (PackPackException e) {
				$LOG.error(e.getMessage(), e);
			}
			RssFeedUtil.uploadNewsFeeds(newsFeeds, ttl, batchId, true);
		}
	}
}