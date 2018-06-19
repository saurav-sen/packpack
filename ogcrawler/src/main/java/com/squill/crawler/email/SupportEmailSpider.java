package com.squill.crawler.email;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static Logger LOG = LoggerFactory
			.getLogger(SupportEmailSpider.class);

	public SupportEmailSpider() {
		pop3TLSProperties = new Properties();
		pop3TLSProperties.put("mail.pop3.host", GMAIL_HOST);
		pop3TLSProperties.put("mail.pop3.port", "995");
		pop3TLSProperties.put("mail.pop3.starttls.enable", "true");
	}

	@Override
	public void run() {
		uploadNewFeeds(pollMessagesIfAny());
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
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				String subject = message.getSubject();
				LOG.info("Received Message From: "
						+ message.getFrom()[0].toString());
				LOG.info("Received Message Subject = " + subject);
				Set<String> extractedLinks = EmailReaderUtil
						.extractLinks(message);
				for (String extractedLink : extractedLinks) {
					linkVsSubject.put(extractedLink, subject);
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
			LOG.error("Failed Polling messages from GMAIL support email.",
					e.getMessage(), e);
		} finally {
			try {
				if (emailStore != null && emailStore.isConnected()) {
					emailStore.close();
				}
			} catch (MessagingException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return feeds;
	}

	private JRssFeed read(String subject, String link) throws Exception {
		String htmlContent = new HttpRequestExecutor().GET(link, null);
		JRssFeed feed = HtmlUtil.parse4mHtml(htmlContent);
		feed.setOgType(JRssFeedType.REFRESHMENT.name());
		feed.setFeedType(JRssFeedType.REFRESHMENT.name());
		return feed;
	}

	private void uploadNewFeeds(List<JRssFeed> feeds) {
		TTL ttl = new TTL();
		ttl.setTime((short) 2);
		ttl.setUnit(TimeUnit.DAYS);
		long batchId = System.currentTimeMillis();
		JRssFeeds jRssFeeds = new JRssFeeds();
		jRssFeeds.getFeeds().addAll(feeds);
		RssFeedUtil.uploadRefreshmentFeeds(jRssFeeds, ttl, batchId, false);
	}
}