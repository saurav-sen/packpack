package com.squill.crawler.email;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;
import com.pack.pack.services.ext.article.comparator.ArticleInfo;
import com.pack.pack.services.ext.article.comparator.TitleBasedArticleComparator;
import com.pack.pack.services.ext.email.SmtpMessage;
import com.pack.pack.services.ext.email.SmtpTLSMessageService;
import com.pack.pack.services.ext.text.summerize.WebDocumentParser;
import com.pack.pack.services.redis.IBookmarkTempStoreService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.RssFeedUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JRssFeeds;
import com.squill.feed.web.model.TTL;
import com.squill.feed.web.model.UploadType;
import com.squill.og.crawler.Spider;
import com.squill.og.crawler.internal.utils.ArchiveUtil;
import com.squill.og.crawler.internal.utils.HtmlUtil;
import com.squill.og.crawler.internal.utils.HttpRequestExecutor;
import com.squill.og.crawler.internal.utils.NotificationUtil;

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
	
	private static final String POP3S = "pop3s";
	
	private static final String NOTIFY_FLAG = "SQUILL:NOTIFY";

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
	
	private List<ParsedMessage> pollMessagesIfAny() {
		List<ParsedMessage> feeds = new LinkedList<ParsedMessage>();
		Store emailStore = null;
		try {
			Session emailSession = Session
					.getDefaultInstance(pop3TLSProperties);

			emailStore = emailSession.getStore(POP3S);
			emailStore.connect(GMAIL_HOST, SUPPORT_EMAIL_ADDR,
					SUPPORT_EMAIL_PASSWORD);
			Folder inbox = emailStore.getFolder("INBOX");
			SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN),
					false);
			inbox.open(Folder.READ_ONLY);
			Message[] messages = inbox.search(searchTerm);
			Map<String, String> linkVsSubject = new HashMap<String, String>();
			Map<String, SmtpMessage> linkVsSmtpMessage = new HashMap<String, SmtpMessage>();
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
					
					String textBody = EmailReaderUtil
							.getTextFromMessage(message);
					Set<String> extractedLinks = EmailReaderUtil
							.extractLinksFromTextBody(textBody);
					
					if(extractedLinks == null || extractedLinks.isEmpty()) {
						StringBuilder replyContent = new StringBuilder();
						replyContent.append(textBody);
						replyContent.append("\n");
						replyContent.append("Sorry!!! ");
						replyContent.append(displayName);
						replyContent.append(", SQUILL failed to read any web-link from your mail. ");
						replyContent.append("SQUILL hereby does apologize for the inconvenience caused to you.");
						SmtpTLSMessageService.INSTANCE.sendMessage(new SmtpMessage(fromEmail, subject,
								replyContent.toString(), false));
						continue;
					}
					
					SmtpMessage smtpMessage = null;
					Address[] replyTo = message.getReplyTo();
					if (replyTo != null && replyTo.length > 0) {
						StringBuilder replyContent = new StringBuilder();
						replyContent.append(textBody);
						replyContent.append("\n");
						replyContent.append("Thank you very much ");
						replyContent.append(displayName);
						replyContent.append(" for contributing your valued content with SQUILL.");
						smtpMessage = new SmtpMessage(fromEmail, subject,
								replyContent.toString(), false);
					} else {
						$LOG.debug("No replyTo found in the Email Message, hence acknowledgement skipped.");
					}
					
					for (String extractedLink : extractedLinks) {
						linkVsSubject.put(extractedLink.trim(), subject);
						linkVsSmtpMessage.put(extractedLink.trim(), smtpMessage);
					}
				}
			}
			emailStore.close();

			Iterator<String> itr = linkVsSubject.keySet().iterator();
			while (itr.hasNext()) {
				String link = itr.next();
				String subject = linkVsSubject.get(link);
				SmtpMessage smtpMessage = linkVsSmtpMessage.get(link);
				ParsedMessage parsedMessage = read(subject, link, smtpMessage);
				if (parsedMessage == null)
					continue;
				if (subject.toUpperCase().trim().contains(NOTIFY_FLAG) || smtpMessage.getContent().contains(NOTIFY_FLAG)) {
					String notificationMessage = parsedMessage.getFeed()
							.getOgTitle();
					parsedMessage.setNotificationMessage(notificationMessage);
				}
				feeds.add(parsedMessage);
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
	
	private ParsedMessage read(String subject, String link, SmtpMessage smtpMessage) throws Exception {
		if(link == null || link.trim().isEmpty() || !isValidUrl(link)) {
			return null;
		}
		
		IBookmarkTempStoreService service = ServiceRegistry.INSTANCE
				.findCompositeService(IBookmarkTempStoreService.class);
		JRssFeed feed = service.getStoredBookmarkIfAny(link);

		String htmlContent = null;
		if (feed == null) {
			htmlContent = new HttpRequestExecutor().GET(link, null);
			feed = HtmlUtil.parse4mHtml(htmlContent);
		}
		JRssFeedType feedType = resolveFeedType(subject, link);
		if (feedType == null)
			return null;

		feed.setOgType(feedType.name());
		feed.setFeedType(feedType.name());
		if(feedType != JRssFeedType.REFRESHMENT && htmlContent != null) {
			feed = new WebDocumentParser().parseHtmlPayload(htmlContent);
			feed.setOgType(feedType.name());
			feed.setFeedType(feedType.name());
			if(feed.getHtmlSnippet() != null && !feed.getHtmlSnippet().trim().isEmpty()) {
				feed.setFullArticleText(feed.getHtmlSnippet());
			}
			try {
				$LOG.info("********************************************************************************************");
				$LOG.info(StringEscapeUtils.unescapeJava(JSONUtil.serialize(feed)));
				$LOG.info("********************************************************************************************");
			} catch (Exception e) {
				$LOG.error(e.getMessage(), e);
			}
		}
		return new ParsedMessage(feed, smtpMessage);
	}
	
	private boolean isValidUrl(String link) {
		try {
			new URL(link.trim());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean isRefreshmentLink(String link) {
		try {
			URL url = new URL(link);
			String host = url.getHost();
			if ("youtu.be".equalsIgnoreCase(host)
					|| "youtube.com".equalsIgnoreCase(host)
					|| "www.youtu.be".equalsIgnoreCase(host)
					|| "www.youtube.com".equalsIgnoreCase(host)) {
				return true;
			}
		} catch (Exception e) {
			$LOG.error(e.getMessage(), e);
		}
		return false;
	}

	private JRssFeedType resolveFeedType(String subject, String link) {
		boolean isRefreshmentLink = isRefreshmentLink(link);
		if (subject == null || subject.trim().isEmpty()) {
			if(isRefreshmentLink) {
				return JRssFeedType.REFRESHMENT;
			} else if(link != null && !link.trim().isEmpty()) {
				return JRssFeedType.NEWS;
			}
			return null;
		}
		JRssFeedType feedType = null;
		subject = subject.trim();
		if (subject.equalsIgnoreCase("SCIENCE")
				|| subject.equalsIgnoreCase("TECHNOLOGY")
				|| subject.equalsIgnoreCase("SCIENCE AND TECHNOLOGY")
				|| subject.equalsIgnoreCase("SCIENCE & TECHNOLOGY")) {
			feedType = JRssFeedType.NEWS_SCIENCE_TECHNOLOGY;
		} else if (subject.equalsIgnoreCase("SPORTS")) {
			feedType = JRssFeedType.NEWS_SPORTS;
		} else if (subject.equalsIgnoreCase("ARTICLE")) {
			feedType = JRssFeedType.ARTICLE;
		} else if (subject.equalsIgnoreCase("NEWS")
				|| subject.equalsIgnoreCase("POLITICS")
				|| subject.equalsIgnoreCase("CRIME")
				|| subject.equalsIgnoreCase("SOCIAL")
				|| subject.equalsIgnoreCase("WEATHER")
				|| subject.equalsIgnoreCase("DISTASTER")) {
			feedType = JRssFeedType.NEWS;
		} else if(isRefreshmentLink) {
			feedType = JRssFeedType.REFRESHMENT;
		} else {
			feedType = JRssFeedType.NEWS;
		}
		if(feedType == JRssFeedType.REFRESHMENT && !isRefreshmentLink) {
			feedType = JRssFeedType.NEWS;
		}
		if(feedType != JRssFeedType.REFRESHMENT && isRefreshmentLink) {
			feedType = JRssFeedType.REFRESHMENT;
		}
		return feedType;
	}

	private void uploadNewFeeds(List<ParsedMessage> parsedMessages) {
		if (parsedMessages == null || parsedMessages.isEmpty())
			return;

		JRssFeeds otherFeeds = new JRssFeeds();
		JRssFeeds refrehmentFeeds = new JRssFeeds();
		List<SmtpMessage> refreshmentSmtpMessages = new LinkedList<SmtpMessage>();
		Map<String, SmtpMessage> linkVsSmtpMessage = new HashMap<String, SmtpMessage>();
		Map<String, String> linkVsNotificationMsg = new HashMap<String, String>();
		for (ParsedMessage parsedMessage : parsedMessages) {
			JRssFeed feed = parsedMessage.getFeed();
			SmtpMessage smtpMessage = parsedMessage.getSmtpMessage();
			String notificationMsg = parsedMessage.getNotificationMessage();
			if (JRssFeedType.REFRESHMENT.name().equals(feed.getFeedType())) {
				refrehmentFeeds.getFeeds().add(feed);
				if (smtpMessage != null) {
					refreshmentSmtpMessages.add(smtpMessage);
				}
			} else {
				otherFeeds.getFeeds().add(feed);
				if(smtpMessage != null && feed.getOgUrl() != null && !feed.getOgUrl().trim().isEmpty()) {
					linkVsSmtpMessage.put(feed.getOgUrl(), smtpMessage);
				}
			}
			
			if(notificationMsg != null && !notificationMsg.trim().isEmpty()) {
				linkVsNotificationMsg.put(feed.getOgUrl(), notificationMsg);
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
			for(SmtpMessage refreshmentSmtpMessage : refreshmentSmtpMessages) {
				SmtpTLSMessageService.INSTANCE.sendMessage(refreshmentSmtpMessage);
			}
		}
		if (!otherFeeds.getFeeds().isEmpty()) {
			TTL ttl = new TTL();
			ttl.setTime((short) 1);
			ttl.setUnit(TimeUnit.DAYS);
			long batchId = System.currentTimeMillis();
			try {
				$LOG.debug("Uploading NEWS/OTHERS from Email: " + JSONUtil.serialize(otherFeeds));
			} catch (PackPackException e) {
				$LOG.error(e.getMessage(), e);
			}
			
			List<JRssFeed> list = ArchiveUtil.getFeedsUploadedFromArchive(
					ArchiveUtil.DEFAULT_ID,
					ArchiveUtil.DEFAULT_MAX_TIME_DIFF_IN_HOURS,
					UploadType.MANUAL);
			if(list == null || list.isEmpty())
				return;
			List<ArticleInfo> tgtList = new ArrayList<ArticleInfo>();
			for(JRssFeed l : list) {
				ArticleInfo tgt = new ArticleInfo(l.getOgTitle(), null);
				tgt.setReferenceObject(l);
				tgtList.add(tgt);
			}
			
			TitleBasedArticleComparator comparator = new TitleBasedArticleComparator();
			List<JRssFeed> otherFeedsList = otherFeeds.getFeeds();
			Iterator<JRssFeed> itr = otherFeedsList.iterator();
			while(itr.hasNext()) {
				JRssFeed otherFeed = itr.next();
				if(!JRssFeedType.NEWS.name().equals(otherFeed.getFeedType()))
					continue;
				ArticleInfo src = new ArticleInfo(otherFeed.getOgTitle(), null);
				src.setReferenceObject(otherFeed);
				try {
					List<ArticleInfo> probableDuplicates = comparator.checkProbableDuplicates(src, tgtList);
					if(probableDuplicates != null && !probableDuplicates.isEmpty()) {
						itr.remove();
						SmtpMessage smtpMessage = linkVsSmtpMessage.get(otherFeed.getOgUrl());
						if(smtpMessage != null) {
							StringBuilder replyContent = new StringBuilder();
							replyContent.append(smtpMessage.getContent());
							replyContent.append(" But SQUILL feels Sorry!!! for not being able to upload your content i.e. ");
							replyContent.append(otherFeed.getOgTitle());
							replyContent.append(" @ ");
							replyContent.append(otherFeed.getOgUrl());
							replyContent.append(".");
							replyContent.append(" Possibly because it matched with one of the following pre-uploaded content");
							for(ArticleInfo probableDuplicate : probableDuplicates) {
								Object referenceObject = probableDuplicate.getReferenceObject();
								if(referenceObject == null || !(referenceObject instanceof JRssFeed))
									continue;
								JRssFeed referenceFeed = (JRssFeed) referenceObject;
								replyContent.append(" ");
								replyContent.append(referenceFeed.getOgTitle());
								replyContent.append(" @ ");
								replyContent.append(referenceFeed.getOgUrl());
								replyContent.append("   ");
							}
							replyContent.append(". ");
							replyContent.append("SQUILL hereby does apologize for the inconvenience caused to you.");
							smtpMessage.setContent(replyContent.toString());
							//linkVsSmtpMessage.remove(otherFeed.getOgUrl());
						}
						linkVsNotificationMsg.remove(otherFeed.getOgUrl());
					} else {
						otherFeed.setUploadType(UploadType.MANUAL.name());
					}
				} catch (Exception e) {
					$LOG.error(e.getMessage(), e);
				}
			}
			
			Map<String, List<JRssFeed>> map = new HashMap<String, List<JRssFeed>>();
			map.put(ArchiveUtil.DEFAULT_ID, otherFeedsList);
			ArchiveUtil.storeInArchive(map);
			HtmlUtil.generateNewsFeedsHtmlPages(otherFeeds);
			RssFeedUtil.uploadNewsFeeds(otherFeeds, ttl, batchId, true);
			
			Iterator<SmtpMessage> msgItr = linkVsSmtpMessage.values()
					.iterator();
			while (msgItr.hasNext()) {
				SmtpMessage smtpMessage = msgItr.next();
				SmtpTLSMessageService.INSTANCE.sendMessage(smtpMessage);
			}
			
			Iterator<String> notifyItr = linkVsNotificationMsg.values()
					.iterator();
			while (notifyItr.hasNext()) {
				String notificationMessage = notifyItr.next();
				try {
					if (notificationMessage != null
							&& !notificationMessage.trim().isEmpty()) {
						NotificationUtil
								.broadcastNewRSSFeedUploadSummary(notificationMessage);
					}
				} catch (PackPackException e) {
					$LOG.error(
							"Failed Sending Notification:: " + e.getMessage(),
							e);
				}
			}
		}
	}
	
	private class ParsedMessage {
		
		private final JRssFeed feed;
		
		private final SmtpMessage smtpMessage;
		
		private String notificationMessage;
		
		private ParsedMessage(JRssFeed feed, SmtpMessage smtpMessage) {
			this.feed = feed;
			this.smtpMessage = smtpMessage;
		}

		private JRssFeed getFeed() {
			return feed;
		}

		private SmtpMessage getSmtpMessage() {
			return smtpMessage;
		}
		
		private String getNotificationMessage() {
			return notificationMessage;
		}
		
		private void setNotificationMessage(String notificationMessage) {
			this.notificationMessage = notificationMessage;
		}
	}
}