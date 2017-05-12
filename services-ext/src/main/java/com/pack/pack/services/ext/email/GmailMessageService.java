package com.pack.pack.services.ext.email;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.pack.pack.model.AttachmentType;
import com.pack.pack.model.Pack;
import com.pack.pack.model.PackAttachment;
import com.pack.pack.model.web.dto.PackReceipent;
import com.pack.pack.services.couchdb.PackAttachmentRepositoryService;
import com.pack.pack.services.registry.ServiceRegistry;
import com.pack.pack.util.SystemPropertyUtil;

/**
 * 
 * @author Saurav
 *
 */
/*@Component
@Lazy
@Scope("singleton")*/
public class GmailMessageService {

	private static Logger LOG = LoggerFactory
			.getLogger(GmailMessageService.class);

	private static final String APPLICATION_NAME = "PackPackApp";

	private static final String APP_SECRET_FILE_NAME = "client_secret_806379591288-l75ra2099up31uv3qgg8t0csvr292lib.apps.googleusercontent.com.json";

	private File dataStoreDir;

	private FileDataStoreFactory dsFactory;

	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	private HttpTransport httpTransport;

	private Gmail service;

	private static final List<String> SCOPES = Arrays.asList(
			GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_COMPOSE,
			GmailScopes.GMAIL_MODIFY);

	//@PostConstruct
	public void initialize() throws Exception {
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		String dataStoreDirPath = SystemPropertyUtil.getAppHome();
		if (dataStoreDirPath != null) {
			if (!dataStoreDirPath.endsWith(File.separator)) {
				dataStoreDirPath = dataStoreDirPath + File.separator;
			}
			dataStoreDirPath = dataStoreDirPath + "credentials"
					+ File.separator + "packpack";
		} else {
			dataStoreDirPath = ".." + File.separator + "credentials"
					+ File.separator + "packpack";
		}
		dataStoreDir = new File(dataStoreDirPath);
		dsFactory = new FileDataStoreFactory(dataStoreDir);

		Credential credential = authorize();
		service = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	public void forwardPack(Pack pack, PackReceipent receipent,
			String fromUserEmail) throws Exception {
		PackAttachmentRepositoryService repositoryService = ServiceRegistry.INSTANCE
				.findService(PackAttachmentRepositoryService.class);
		List<PackAttachment> packAttachments = repositoryService
				.getAllListOfPackAttachments(pack.getId());
		List<File> mailAttachments = new ArrayList<File>();
		if (packAttachments != null && !packAttachments.isEmpty()) {
			for (PackAttachment packAttachment : packAttachments) {
				String filePath = packAttachment.getAttachmentUrl();
				AttachmentType type = packAttachment.getType();
				File f = null;
				switch (type) {
				case IMAGE:
					String imageHome = SystemPropertyUtil.getImageHome();
					if (!imageHome.endsWith(File.separator)
							&& !filePath.startsWith(File.separator)) {
						filePath = imageHome + File.separator + filePath;
					}
					f = new File(filePath);
					mailAttachments.add(f);
					break;
				case VIDEO:
					String videoHome = SystemPropertyUtil.getVideoHome();
					if (!videoHome.endsWith(File.separator)
							&& !filePath.startsWith(File.separator)) {
						filePath = videoHome + File.separator + filePath;
					}
					f = new File(filePath);
					mailAttachments.add(f);
					break;
				}
			}
		}
		sendPack(receipent.getToUserId(), fromUserEmail, pack.getTitle(),
				pack.getStory(), mailAttachments);
	}

	private void sendPack(String to, String from, String subject, String body,
			List<File> attachments) throws IOException, MessagingException {
		MimeMessage email = createEmailWithAttachment(to, from, subject, body,
				attachments);
		sendMessage(service, "me", email);
	}

	private Credential authorize() throws Exception {
		LOG.info("Authorizing with GMail");
		String appKeyFile = null;
		if (SystemPropertyUtil.getAppHome() == null) {
			appKeyFile = "D:/Saurav/packpack/services-ext/conf/"
					+ APP_SECRET_FILE_NAME;
		} else {
			appKeyFile = SystemPropertyUtil.getAppHome() + File.separator
					+ "conf" + File.separator + APP_SECRET_FILE_NAME;
		}
		InputStream in = new FileInputStream(new File(appKeyFile));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
				JSON_FACTORY, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(dsFactory).setAccessType("offline")
				.build();
		Credential credential = new AuthorizationCodeInstalledApp(flow,
				new LocalServerReceiver()).authorize("user");
		LOG.debug("Credentials saved to " + dataStoreDir.getAbsolutePath());
		return credential;
	}

	private void sendMessage(Gmail service, String userId, MimeMessage email)
			throws MessagingException, IOException {
		Message message = createMessageWithEmail(email);
		message = service.users().messages().send(userId, message).execute();

		LOG.info("Message id: " + message.getId());
		LOG.info(message.toPrettyString());
	}

	private Message createMessageWithEmail(MimeMessage email)
			throws MessagingException, IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		email.writeTo(bytes);
		String encodedEmail = Base64.encodeBase64URLSafeString(bytes
				.toByteArray());
		Message message = new Message();
		message.setRaw(encodedEmail);
		return message;
	}

	private MimeMessage createEmailWithAttachment(String to, String from,
			String subject, String bodyText, List<File> attachments)
			throws MessagingException, IOException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);
		InternetAddress tAddress = new InternetAddress(to);
		InternetAddress fAddress = new InternetAddress(from);

		email.setFrom(fAddress);
		email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
		email.setSubject(subject);

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(bodyText, "text/plain");
		mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		for (File attachment : attachments) {
			String fileDir = attachment.getParent();
			String filename = attachment.getName();
			mimeBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(fileDir + filename);

			mimeBodyPart.setDataHandler(new DataHandler(source));
			mimeBodyPart.setFileName(filename);
			String contentType = Files.probeContentType(FileSystems
					.getDefault().getPath(fileDir, filename));
			mimeBodyPart.setHeader("Content-Type", contentType + "; name=\""
					+ filename + "\"");
			mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");

			multipart.addBodyPart(mimeBodyPart);
		}

		email.setContent(multipart);

		return email;
	}

	/*
	 * private MimeMessage createEmail(String to, String from, String subject,
	 * String bodyText) throws MessagingException { Properties props = new
	 * Properties(); Session session = Session.getDefaultInstance(props, null);
	 * 
	 * MimeMessage email = new MimeMessage(session); InternetAddress tAddress =
	 * new InternetAddress(to); InternetAddress fAddress = new
	 * InternetAddress(from);
	 * 
	 * email.setFrom(new InternetAddress(from));
	 * email.addRecipient(javax.mail.Message.RecipientType.TO, new
	 * InternetAddress(to)); email.setSubject(subject); email.setText(bodyText);
	 * return email; }
	 */
}