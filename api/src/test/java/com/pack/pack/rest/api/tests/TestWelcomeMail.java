package com.pack.pack.rest.api.tests;

import java.util.UUID;

import com.pack.pack.markup.gen.MarkupGenerator;
import com.pack.pack.services.ext.email.SmtpMessage;
import com.pack.pack.services.ext.email.SmtpTLSMessageService;

public class TestWelcomeMail {

	public static void main(String[] args) throws Exception {
		String htmlContent = MarkupGenerator.INSTANCE
				.generateWelcomeEmailHtmlContent("Saurav Sen", 5,
						String.valueOf(Math.abs(UUID.randomUUID().toString().hashCode())));
		SmtpMessage smtpMessage = new SmtpMessage("sourabhnits@gmail.com",
				"Welcome To SQUILL", htmlContent, true);
		SmtpTLSMessageService.INSTANCE.sendMessage(smtpMessage);
		System.out.println("Done");
	}

}
