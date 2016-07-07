package com.squill.og.crawler.internal;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.squill.og.crawler.IContentFilter;
import com.squill.og.crawler.ILinkResolver;
import com.squill.og.crawler.Report;
import com.squill.og.crawler.internal.utils.CoreConstants;
import com.squill.og.crawler.internal.utils.FileDownloadUtils;
import com.squill.og.crawler.internal.utils.ResponseUtil;

/**
 * 
 * @author Saurav
 * 
 */
public class CrawlerTask implements Runnable {

	private BlockingQueue<PageLink> crawlQueue;

	private IContentFilter contentFilter;
	
	private ILinkResolver linkResolver;

	public CrawlerTask(BlockingQueue<PageLink> crawlQueue, IContentFilter contentFilter, 
			ILinkResolver linkResolver) {
		this.crawlQueue = crawlQueue;
		this.contentFilter = contentFilter;
		this.linkResolver = linkResolver;
	}

	private void doCrawl() throws Exception {
		while (!crawlQueue.isEmpty()) {
			//Thread.sleep(5000);
			PageLink pageLink = crawlQueue.poll(2, TimeUnit.MINUTES);
			System.out.println("Crawling: " + pageLink.getLink());
			CrawlContext ctx = pageLink.getContext();
			CookieStore cookieStore = ctx.getCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			/*
			 * DefaultHttpClient client = new DefaultHttpClient();
			 * client.setCookieStore(cookieStore);
			 */
			HttpGet get = new HttpGet(pageLink.getLink());
			HttpResponse response = new DefaultHttpClient().execute(get,
					localContext);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode >= 200 && responseCode < 300) { // OK
				Header contentDisposition = response
						.getFirstHeader(CoreConstants.CONTENT_DISPOSITION);
				Header contentType = response
						.getFirstHeader(CoreConstants.CONTENT_TYPE);
				if (contentDisposition != null) {
					handleAttachment(response, ctx, contentType,
							contentDisposition);
				} else if(contentType != null 
						&& CoreConstants.MIME_TYPE_APPLICATION_PDF.equalsIgnoreCase(
								contentType.getValue())) {
					String fileName = pageLink.getLink().substring(pageLink.getLink().lastIndexOf("/") + 1).trim();
					String downloadHomeDir = ctx.getDownloadHomeDir();
					String userId = ctx.getUserId();
					if(userId != null) {
						downloadHomeDir = downloadHomeDir + File.separator + ctx.getUserId();
					}
					File dir = new File(downloadHomeDir);
					if(!dir.exists()) {
						dir.mkdir();
					}
					FileDownloadUtils.downloadAttachment(response, fileName, downloadHomeDir);
					Report report = new Report(fileName, ctx.getUserId());
					ctx.addReport(report);
				} else {
					handleResponse(response, ctx, contentType, pageLink);
				}
			}
		}
	}

	private void handleResponse(HttpResponse response, CrawlContext ctx,
			Header contentType, PageLink pageLink) throws Exception {
		String html = ResponseUtil.getResponseBodyContent(response);
		if (html == null)
			return;
		Document document = Jsoup.parse(html);
		String documentTitle = document.title();
		String pLink = null;
		if(pageLink != null) {
			pLink = pageLink.getLink();
		}
		HtmlPage htmlPage = ResponseUtil.getParseableHtml(
				ctx.getHostURLPrefix(), html, pLink, contentFilter);
		if (contentFilter.isDownloadable(document)) {
			String fileName = documentTitle.replaceAll(" ", "_").replaceAll(
					":", "")
					+ CoreConstants.HTML_FILE_EXT;
			if (fileName != null) {
				String downloadHomeDir = ctx.getDownloadHomeDir();
				String downloadableContent = contentFilter.getDownloadableContent(htmlPage);
				String userId = ctx.getUserId();
				if(userId != null) {
					downloadHomeDir = downloadHomeDir + File.separator + ctx.getUserId();
				}
				File dir = new File(downloadHomeDir);
				if(!dir.exists()) {
					dir.mkdir();
				}
				if(!downloadHomeDir.endsWith(File.separator)) {
					downloadHomeDir = downloadHomeDir + File.separator;
				}
				FileDownloadUtils.downloadHtmlBody(downloadableContent,
						fileName,
						downloadHomeDir);
				Report report = new Report(fileName, ctx.getUserId());
				ctx.addReport(report);
			}
		}

		PageLinkExtractor linkExtractor = new PageLinkExtractor(contentFilter, linkResolver);
		List<PageLink> links = linkExtractor.extractAllPageLinks(htmlPage, ctx);
		// List<CrawlerTask> tasks = new ArrayList<CrawlerTask>();
		for (PageLink link : links) {
			if (!ctx.isScheduledForCrawl(link)) {
				crawlQueue.offer(link, 2, TimeUnit.MINUTES);
				ctx.scheduledForCrawl(link);
			}
		}
	}

	private void handleAttachment(HttpResponse response, CrawlContext ctx,
			Header contentType, Header contentDisposition) throws Exception {
		String value = contentDisposition.getValue();
		if (value == null)
			return;
		String userID = ctx.getUserId();
		String[] split = value.split(CoreConstants.SEMICOLON);
		String fileName = userID + CoreConstants.PDF_FILE_EXT;
		if (split[0].trim().equalsIgnoreCase(
				CoreConstants.CONTENT_DISPOSITION_ATTACHMENT)) {
			if (split.length > 1) {
				String[] split2 = split[1].split(CoreConstants.EQUAL);
				if (split2[0].trim().equalsIgnoreCase(
						CoreConstants.ATTCAHMENT_FILE_NAME)
						&& split2.length > 1) {
					fileName = split2[1].trim();
					fileName = fileName.replaceAll("\"", "");
				}
			}
			boolean download = false;
			if (contentType != null) {
				String mimeType = contentType.getValue().trim();
				if (CoreConstants.MIME_TYPE_APPLICATION_PDF.equals(mimeType)) {
					download = true;
				}
			}
			if (download) {
				String downloadHomeDir = ctx.getDownloadHomeDir();
				FileDownloadUtils.downloadAttachment(response, fileName,
						downloadHomeDir + File.separator + userID);
				Report report = new Report(fileName, ctx.getUserId());
				ctx.addReport(report);
			}
		}
	}

	@Override
	public void run() {
		try {
			doCrawl();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}