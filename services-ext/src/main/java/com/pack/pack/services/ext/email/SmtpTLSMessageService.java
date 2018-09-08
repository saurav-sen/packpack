package com.pack.pack.services.ext.email;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Saurav
 *
 */
public class SmtpTLSMessageService {

	private static Logger LOG = LoggerFactory
			.getLogger(SmtpTLSMessageService.class);

	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final String SMTP_PORT = "587";
	
	private static final String USERNAME = "donot-reply@squill.co.in";
	private static final String PASSWORD = "P@$$w0rd4SQui11";

	private static final String DISPLAY_LABEL = "SQUILL";

	private Properties smtpTLSProperties;
	
	private ExecutorService executors;
	
	public static final SmtpTLSMessageService INSTANCE = new SmtpTLSMessageService();
	
	private SmtpTLSMessageService() {
		initialize();
	}

	private void initialize() {
		smtpTLSProperties = new Properties();
		smtpTLSProperties.put("mail.smtp.auth", "true");
		smtpTLSProperties.put("mail.smtp.starttls.enable", "true");
		smtpTLSProperties.put("mail.smtp.host", SMTP_HOST);
		smtpTLSProperties.put("mail.smtp.port", SMTP_PORT);
		
		executors = Executors.newCachedThreadPool();
	}

	public void sendMessage(SmtpMessage message) {
		executors.execute(new SendMessageTask(message));

	}
	
	private void sendMail(String receipentEmailId, String subject,
			String htmlContent, boolean isHtml) {
		LOG.debug("Establishing SMTP session to GMail");
		Session session = Session.getInstance(smtpTLSProperties,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(USERNAME, PASSWORD);
					}
				});

		LOG.info("Preparing to send mail");

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(USERNAME, DISPLAY_LABEL));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(receipentEmailId));
			message.setSubject(subject);
			if(isHtml) {
				message.setContent(htmlContent, "text/html; charset=UTF-8");
			} else {
				message.setContent(htmlContent, "text/plain");
			}

			Transport.send(message);

			LOG.info("Mailed to " + receipentEmailId);

		} catch (Exception e) {
			LOG.error("Failed Sending Mail", e.getMessage(), e);
			// throw new RuntimeException(e);
		}
	}

	private class SendMessageTask implements Runnable {

		private SmtpMessage message;

		public SendMessageTask(SmtpMessage message) {
			this.message = message;
		}

		@Override
		public void run() {
			try {
				sendMail(message.getReceipentEmailId(), message.getSubject(),
						message.getContent(), message.isHtml());
			} catch (Throwable e) {
				LOG.error("Send Mail failed", e);
			}
		}
	}
}
